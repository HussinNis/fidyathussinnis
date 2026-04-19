package com.example.fidyathussinnis;

import java.util.List;
import java.util.Map;

public class Order {
    private String userId;
    private String userName;
    private String userEmail;
    private int totalPrice;
    private String status;
    private long createdAt;
    private List<Map<String, Object>> products;

    public Order() {
    }

    public Order(String userId, String userName, String userEmail, int totalPrice, String status, long createdAt, List<Map<String, Object>> products) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.products = products;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public List<Map<String, Object>> getProducts() {
        return products;
    }

    public int getProductsCount() {
        return products != null ? products.size() : 0;
    }
}