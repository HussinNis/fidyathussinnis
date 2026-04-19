package com.example.fidyathussinnis;

public class CartItem {
    private String name;
    private int price;
    private int quantity;
    private int imageResId;

    public CartItem() {
        // مطلوب لـ Firestore
    }

    public CartItem(String name, int price, int quantity, int imageResId) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}