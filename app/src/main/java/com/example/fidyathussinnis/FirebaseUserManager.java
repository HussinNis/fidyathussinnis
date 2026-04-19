package com.example.fidyathussinnis;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserManager {

    private static final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static FirebaseAuth getAuth() {
        return auth;
    }

    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public static void logout() {
        auth.signOut();
    }
}