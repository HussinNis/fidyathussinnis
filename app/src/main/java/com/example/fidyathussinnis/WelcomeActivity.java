package com.example.fidyathussinnis;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etPassword;
    private Button btnTabLogin, btnTabSignup, btnSubmit;
    private TextView tvModeHint;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnTabLogin = findViewById(R.id.btnTabLogin);
        btnTabSignup = findViewById(R.id.btnTabSignup);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvModeHint = findViewById(R.id.tvModeHint);

        checkLoggedInUser();

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
    }

    private void checkLoggedInUser() {
        User currentUser = UserManager.getCurrentUser(this);
        if (currentUser != null) {
            openStore(currentUser.getFullName());
        }
    }

    private void switchToLogin() {
        isLoginMode = true;
        etFullName.setVisibility(View.GONE);
        btnSubmit.setText("Log In");
        tvModeHint.setText("Welcome back");
    }

    private void switchToSignup() {
        isLoginMode = false;
        etFullName.setVisibility(View.VISIBLE);
        btnSubmit.setText("Sign Up");
        tvModeHint.setText("Create your account");
    }

    private void signupUser() {
        String fullName = etFullName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (fullName.isEmpty()) {
            etFullName.setError("Enter full name");
            return;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Enter phone number");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        User newUser = new User(fullName, phone, password);
        boolean success = UserManager.registerUser(this, newUser);

        if (!success) {
            Toast.makeText(this, "This phone number is already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        User loggedInUser = UserManager.loginUser(this, phone, password);
        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();

        if (loggedInUser != null) {
            openStore(loggedInUser.getFullName());
        }
    }

    private void loginUser() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty()) {
            etPhone.setError("Enter phone number");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Enter password");
            return;
        }

        User user = UserManager.loginUser(this, phone, password);

        if (user != null) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
            openStore(user.getFullName());
        } else {
            Toast.makeText(this, "Wrong phone number or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void openStore(String name) {
        Intent intent = new Intent(WelcomeActivity.this, StoreActivity.class);
        intent.putExtra("name", name);
        startActivity(intent);
        finish();
    }
}