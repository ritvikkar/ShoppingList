package com.ritvikkar.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.ritvikkar.shoppinglist.data.Item;

import java.util.UUID;

import io.realm.Realm;

public class AddItemActivity extends AppCompatActivity {

    public static final String KEY_ITEM = "KEY_ITEM";

    private Spinner spinnerItemType;
    private EditText etItemName;
    private EditText etItemCost;
    private EditText etItemDesc;
    private CheckBox cbPurchased;

    private Item itemToEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        setupUI();

        if (getIntent().getSerializableExtra(MainActivity.KEY_EDIT) != null) {
            initEdit();
        } else {
            initCreate();
        }
    }

    private void initCreate() {
        getRealm().beginTransaction();
        itemToEdit = getRealm().createObject(Item.class, UUID.randomUUID().toString());
        getRealm().commitTransaction();
    }

    private void initEdit() {
        String itemID = getIntent().getStringExtra(MainActivity.KEY_EDIT);
        itemToEdit = getRealm().where(Item.class)
                .equalTo(MainActivity.ITEM_ID, itemID)
                .findFirst();

        etItemName.setText(itemToEdit.getName());
        etItemCost.setText(String.valueOf(itemToEdit.getPrice()));
        etItemDesc.setText(itemToEdit.getDescription());
        spinnerItemType.setSelection(itemToEdit.getCategory().getValue());
        cbPurchased.setChecked(itemToEdit.isStatus());
    }

    private void setupUI() {
        etItemName = findViewById(R.id.etItemName);
        etItemCost = findViewById(R.id.etItemCost);

        spinnerItemType = findViewById(R.id.spinnerCategory);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.itemtypes_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItemType.setAdapter(adapter);

        etItemDesc = findViewById(R.id.etItemDesc);
        cbPurchased = findViewById(R.id.cbPurchased);


        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });
    }

    public Realm getRealm() {
        return ((MainApplication)getApplication()).getRealmItems();
    }

    private void saveItem() {
        Intent intentResult = new Intent();

        if(etItemName.getText().toString().isEmpty()) {
            etItemName.setError(getString(R.string.txt_enter_name));
        } else if(etItemCost.getText().toString().isEmpty()){
            etItemCost.setError(getString(R.string.txt_enter_cost));
        } else if(etItemDesc.getText().toString().isEmpty()){
            etItemDesc.setError(getString(R.string.txt_enter_desc));
        } else{
            getRealm().beginTransaction();
            itemToEdit.setName(etItemName.getText().toString());
            itemToEdit.setPrice(Double.parseDouble(etItemCost.getText().toString()));
            itemToEdit.setCategory(spinnerItemType.getSelectedItemPosition());
            itemToEdit.setDescription(etItemDesc.getText().toString());
            itemToEdit.setStatus(cbPurchased.isChecked());
            getRealm().commitTransaction();

            intentResult.putExtra(KEY_ITEM, itemToEdit.getItemID());
            setResult(RESULT_OK, intentResult);
            finish();
        }
    }

}
