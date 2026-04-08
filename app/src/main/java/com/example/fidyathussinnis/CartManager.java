package com.example.fidyathussinnis;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartManager {

    private static final String PREF_NAME = "cart_pref";
    private static ArrayList<CartItem> cartItems = new ArrayList<>();

    private static String getCartKey(Context context) {
        User currentUser = UserManager.getCurrentUser(context);

        if (currentUser != null) {
            return "cart_" + currentUser.getPhone();
        }

        return "cart_guest";
    }

    public static void loadCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(getCartKey(context), null);

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
            cartItems = gson.fromJson(json, type);
        } else {
            cartItems = new ArrayList<>();
        }

        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
    }

    public static void saveCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cartItems);

        editor.putString(getCartKey(context), json);
        editor.apply();
    }

    public static ArrayList<CartItem> getCartItems(Context context) {
        loadCart(context);
        return cartItems;
    }

    public static void addToCart(Context context, CartItem newItem) {
        loadCart(context);

        for (CartItem item : cartItems) {
            if (item.getName().equals(newItem.getName())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                saveCart(context);
                return;
            }
        }

        cartItems.add(newItem);
        saveCart(context);
    }

    public static void removeItem(Context context, int position) {
        loadCart(context);

        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            saveCart(context);
        }
    }

    public static void clearCart(Context context) {
        cartItems.clear();
        saveCart(context);
    }

    public static int getTotalItemsCount(Context context) {
        loadCart(context);

        int totalCount = 0;
        for (CartItem item : cartItems) {
            totalCount += item.getQuantity();
        }
        return totalCount;
    }

    public static void updateCart(Context context) {
        saveCart(context);
    }
}