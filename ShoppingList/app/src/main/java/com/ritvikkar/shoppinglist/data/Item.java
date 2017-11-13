package com.ritvikkar.shoppinglist.data;

import com.ritvikkar.shoppinglist.R;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Item extends RealmObject {

    public enum ItemType {
        FOOD(0, R.drawable.food),
        ELECTRONICS(1, R.drawable.electronics),
        SCHOOL(2, R.drawable.school),
        HOUSEHOLD(3, R.drawable.home),
        WORK(4, R.drawable.work);

        private int value;
        private int iconId;

        private ItemType(int value, int iconId) {
            this.value = value;
            this.iconId = iconId;
        }

        public int getValue() {
            return value;
        }

        public int getIconId() {
            return iconId;
        }

        public static ItemType fromInt(int value) {
            for (ItemType p : ItemType.values()) {
                if (p.value == value) {
                    return p;
                }
            }
            return FOOD;
        }
    }

    @PrimaryKey
    private String itemID;

    public String getItemID() {
        return itemID;
    }

    private String name;
    private double price;
    private int category;
    private String description;
    private boolean status;

    public Item() {

    }

    public Item(String name, double price, int category, String description, boolean status) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public ItemType getCategory() {
        return ItemType.fromInt(category);
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
