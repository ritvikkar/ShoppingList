package com.ritvikkar.shoppinglist;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.ritvikkar.shoppinglist.adapter.ItemAdapter;
import com.ritvikkar.shoppinglist.data.Item;
import com.ritvikkar.shoppinglist.touch.ItemsListTouchHelperCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_NEW_ITEM = 101;
    public static final int REQUEST_EDIT_ITEM = 102;
    public static final String KEY_EDIT = "KEY_EDIT";
    public static final String ITEM_ID = "itemID";
    public static final String SHOPPING_LIST = "Shopping List";
    public static final String YOUR_ITEM_LIST = "Your Item List";

    private ItemAdapter itemsAdapter;
    private CoordinatorLayout layoutContent;

    private int itemToEditPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((MainApplication)getApplication()).openRealm();

        setUpRealm();

        layoutContent = (CoordinatorLayout) findViewById(
                R.id.layoutContent);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.btnAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemActivity();
            }
        });

        FloatingActionButton btnEmail = (FloatingActionButton) findViewById(R.id.btnEmail);
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });


        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.btnRemove);
        fabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItemsDialogue();
            }
        });
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, SHOPPING_LIST);
        intent.putExtra(Intent.EXTRA_TEXT, YOUR_ITEM_LIST);
        intent.putExtra(Intent.EXTRA_TEXT, getMyStringMessage());
        intent.setData(Uri.parse("mailto:"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private String getMyStringMessage(){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < itemsAdapter.getItemCount(); i++) {
            builder.append(i + 1).append(". ")
                    .append(itemsAdapter.getItem(i).getName())
                    .append(" (")
                    .append(itemsAdapter.getItem(i).getCategory())
                    .append(") $")
                    .append(itemsAdapter.getItem(i).getPrice())
                    .append("\n")
                    .append(itemsAdapter.getItem(i).getDescription())
                    .append("\n\n");
        }
        return builder.toString();
    }

    private void setUpRealm() {
        RealmResults<Item> allItems = getRealm().where(Item.class).findAll();
        Item shoppingArray[] = new Item[allItems.size()];
        List<Item> itemsResult = new ArrayList<Item>(Arrays.asList(allItems.toArray(shoppingArray)));

        itemsAdapter = new ItemAdapter(itemsResult, this);
        RecyclerView recyclerViewItems = (RecyclerView) findViewById(
                R.id.recyclerViewItems);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewItems.setAdapter(itemsAdapter);

        ItemsListTouchHelperCallback touchHelperCallback = new ItemsListTouchHelperCallback(
                itemsAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(
                touchHelperCallback);
        touchHelper.attachToRecyclerView(recyclerViewItems);
    }

    private void deleteItemsDialogue() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }
        builder.setTitle(R.string.txt_delete)
                .setMessage(R.string.txt_delete_warning)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllItems();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showSnackBarMessage(getString(R.string.txt_all_deleted_cancelled));
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteAllItems() {
        getRealm().beginTransaction();
        itemsAdapter.clearData();
        itemsAdapter.notifyDataSetChanged();
        getRealm().deleteAll();
        getRealm().commitTransaction();
        showSnackBarMessage(getString(R.string.txt_all_deleted));
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    private void showAddItemActivity() {
        Intent intentStart = new Intent().setClass(this, AddItemActivity.class);
        startActivityForResult(intentStart, REQUEST_NEW_ITEM);
    }

    public void showEditItemActivity(String placeID, int position) {
        Intent intentStart = new Intent(this,
                AddItemActivity.class);
        itemToEditPosition = position;

        intentStart.putExtra(KEY_EDIT, placeID);
        startActivityForResult(intentStart, REQUEST_EDIT_ITEM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                String itemID  = data.getStringExtra(
                        AddItemActivity.KEY_ITEM);

                Item item = getRealm().where(Item.class)
                        .equalTo(ITEM_ID, itemID)
                        .findFirst();

                if (requestCode == REQUEST_NEW_ITEM) {
                    itemsAdapter.addItem(item);
                    showSnackBarMessage(getString(R.string.txt_item_added));
                } else if (requestCode == REQUEST_EDIT_ITEM) {
                    itemsAdapter.updateItem(itemToEditPosition, item);
                    showSnackBarMessage(getString(R.string.txt_item_edited));
                }
                break;
            case RESULT_CANCELED:
                showSnackBarMessage(getString(R.string.txt_add_cancel));
                break;
        }
    }

    public void deleteItem(Item item) {
        getRealm().beginTransaction();
        item.deleteFromRealm();
        getRealm().commitTransaction();
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(layoutContent,
                message,
                Snackbar.LENGTH_LONG
        ).setAction(R.string.action_hide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MainApplication)getApplication()).closeRealm();
    }
}
