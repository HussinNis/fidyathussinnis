package com.example.fidyathussinnis;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class UserManager {

    private static final String PREF_NAME = "user_pref";
    private static final String KEY_USERS = "users";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_PHONE = "current_user_phone";

    public static ArrayList<User> getUsers(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_USERS, null);

        if (json == null) {
            return new ArrayList<>();
        }

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        ArrayList<User> users = gson.fromJson(json, type);

        return users != null ? users : new ArrayList<>();
    }

    public static void saveUsers(Context context, ArrayList<User> users) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(users);

        editor.putString(KEY_USERS, json);
        editor.apply();
    }

    public static boolean registerUser(Context context, User newUser) {
        ArrayList<User> users = getUsers(context);

        for (User user : users) {
            if (user.getPhone().equals(newUser.getPhone())) {
                return false;
            }
        }

        users.add(newUser);
        saveUsers(context, users);
        return true;
    }

    public static User loginUser(Context context, String phone, String password) {
        ArrayList<User> users = getUsers(context);

        for (User user : users) {
            if (user.getPhone().equals(phone) && user.getPassword().equals(password)) {
                SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
                prefs.edit()
                        .putBoolean(KEY_IS_LOGGED_IN, true)
                        .putString(KEY_CURRENT_USER_PHONE, user.getPhone())
                        .apply();
                return user;
            }
        }

        return null;
    }

    public static User getCurrentUser(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false);
        String currentPhone = prefs.getString(KEY_CURRENT_USER_PHONE, "");

        if (!isLoggedIn || currentPhone.isEmpty()) {
            return null;
        }

        ArrayList<User> users = getUsers(context);

        for (User user : users) {
            if (user.getPhone().equals(currentPhone)) {
                return user;
            }
        }

        return null;
    }

    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(KEY_IS_LOGGED_IN, false)
                .remove(KEY_CURRENT_USER_PHONE)
                .apply();
    }
}