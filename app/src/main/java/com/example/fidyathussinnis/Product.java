package com.example.fidyathussinnis;

public class Product {
    private String name;
    private int price;
    private String details;

    public Product(String name, int price, String details) {
        this.name = name;
        this.price = price;
        this.details = details;
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
}