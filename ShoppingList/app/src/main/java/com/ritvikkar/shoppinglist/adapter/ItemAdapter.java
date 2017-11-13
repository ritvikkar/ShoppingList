package com.ritvikkar.shoppinglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ritvikkar.shoppinglist.MainActivity;
import com.ritvikkar.shoppinglist.R;
import com.ritvikkar.shoppinglist.data.Item;

import java.util.Collections;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvCost;
        public CheckBox cbPurchased;
        public Button btnDetails;
        public Button btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCost = itemView.findViewById(R.id.tvCost);
            cbPurchased = itemView.findViewById(R.id.cbPurchased);
            btnDetails = itemView.findViewById(R.id.btnDetails);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private List<Item> shoppingList;
    private Context context;
    private int lastPosition = -1;

    public ItemAdapter(List<Item> shoppingList, Context context) {
        this.shoppingList = shoppingList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.ivIcon.setImageResource(shoppingList.get(position).getCategory().getIconId());
        holder.tvTitle.setText(shoppingList.get(position).getName());
        holder.tvCost.setText(String.valueOf(shoppingList.get(position).getPrice()));
        holder.cbPurchased.setChecked(shoppingList.get(position).isStatus());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(holder.getAdapterPosition());
            }
        });

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) context).showEditItemActivity(
                        shoppingList.get(holder.getAdapterPosition()).getItemID(),
                        holder.getAdapterPosition());
            }
        });

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    public void addItem(Item item) {
        shoppingList.add(item);
        notifyDataSetChanged();
    }

    public void updateItem(int index, Item item) {
        shoppingList.set(index, item);
        notifyItemChanged(index);
    }

    public void removeItem(int adapterPosition) {
        ((MainActivity)context).deleteItem(shoppingList.get(adapterPosition));
        shoppingList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public void swapItems(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(shoppingList, i, i + 1);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(shoppingList, i, i - 1);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
    }

    public Item getItem(int i) {
        return shoppingList.get(i);
    }

    public void clearData() {
        shoppingList.clear();
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.startAnimation(animation);
            lastPosition = position;
        }
    }
}
