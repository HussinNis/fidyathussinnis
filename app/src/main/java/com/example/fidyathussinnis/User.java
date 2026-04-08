package com.example.fidyathussinnis;

public class User {
    private String fullName;
    private String phone;
    private String password;

    public User(String fullName, String phone, String password) {
        this.fullName = fullName;
        this.phone = phone;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }
}