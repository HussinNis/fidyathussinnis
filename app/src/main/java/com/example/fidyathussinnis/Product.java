package com.example.fidyathussinnis;

public class Product {
    private String name;
    private int price;
    private String details;
    private int imageResId;

    public Product(String name, int price, String details, int imageResId) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getDetails() {
        return details;
    }

    public int getImageResId() {
        return imageResId;
    }
}