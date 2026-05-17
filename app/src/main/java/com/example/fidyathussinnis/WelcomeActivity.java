package com.example.fidyathussinnis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword;
    private Button btnTabLogin, btnTabSignup, btnSubmit;
    private TextView tvModeHint;

    private boolean isLoginMode = true;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnTabLogin = findViewById(R.id.btnTabLogin);
        btnTabSignup = findViewById(R.id.btnTabSignup);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvModeHint = findViewById(R.id.tvModeHint);

        btnTabLogin.setOnClickListener(v -> switchToLogin());
        btnTabSignup.setOnClickListener(v -> switchToSignup());

        btnSubmit.setOnClickListener(v -> {
            if (isLoginMode) {
                loginUser();
            } else {
                signupUser();
            }
        });

        switchToLogin();
        checkLoggedInUser();
    }

    private void checkLoggedInUser() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            loadUserNameAndOpenStore(currentUser.getUid());
        }
    }

    private void switchToLogin() {
        isLoginMode = true;

        etFullName.setVisibility(View.GONE);
        btnSubmit.setText("Log In");
        tvModeHint.setText("Welcome back");

        btnTabLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_button_primary));
        btnTabSignup.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_card));

        btnTabLogin.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        btnTabSignup.setTextColor(0xFF111827);
    }

    private void switchToSignup() {
        isLoginMode = false;

        etFullName.setVisibility(View.VISIBLE);
        btnSubmit.setText("Sign Up");
        tvModeHint.setText("Create your account");

        btnTabSignup.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_button_primary));
        btnTabLogin.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_card));

        btnTabSignup.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        btnTabLogin.setTextColor(0xFF111827);
    }

    private void signupUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Enter full name");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    if (firebaseUser == null) {
                        Toast.makeText(this, "User creation failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = firebaseUser.getUid();

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("fullName", fullName);
                    userMap.put("email", email);
                    userMap.put("createdAt", System.currentTimeMillis());

                    db.collection("users")
                            .document(uid)
                            .set(userMap)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                openStore(fullName);
                            })
                            .addOnFailureListener(e -> {
                                firebaseUser.delete()
                                        .addOnCompleteListener(task -> {
                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(
                                                    this,
                                                    "تم إنشاء الحساب لكن فشل حفظ البيانات في Firestore: " + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show();
                                        });
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Sign up failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Enter email");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser currentUser = auth.getCurrentUser();

                    if (currentUser == null) {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                    loadUserNameAndOpenStore(currentUser.getUid());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void loadUserNameAndOpenStore(String uid) {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String fullName = documentSnapshot.getString("fullName");

                    if (fullName != null && !fullName.isEmpty()) {
                        openStore(fullName);
                    } else {
                        FirebaseUser currentUser = auth.getCurrentUser();
                        String fallbackName = currentUser != null && currentUser.getEmail() != null
                                ? currentUser.getEmail()
                                : "User";
                        openStore(fallbackName);
                    }
                })
                .addOnFailureListener(e -> {
                    FirebaseUser currentUser = auth.getCurrentUser();
                    String fallbackName = currentUser != null && currentUser.getEmail() != null
                            ? currentUser.getEmail()
                            : "User";
                    openStore(fallbackName);
                });
    }

    private void openStore(String name) {
        Intent intent = new Intent(WelcomeActivity.this, StoreActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
        finish();
    }
}