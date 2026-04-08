package com.example.fidyathussinnis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    private TextView tvProductsTitle;
    private RecyclerView recyclerProducts;
    private Button btnOpenCart, btnBackToStore, btnLogoutFromProducts;

    private ArrayList<Product> productList;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        tvProductsTitle = findViewById(R.id.tvProductsTitle);
        recyclerProducts = findViewById(R.id.recyclerProducts);
        btnOpenCart = findViewById(R.id.btnOpenCart);
        btnBackToStore = findViewById(R.id.btnBackToStore);
        btnLogoutFromProducts = findViewById(R.id.btnLogoutFromProducts);

        String type = getIntent().getStringExtra("type");

        productList = new ArrayList<>();

        if (type != null && type.equals("bars")) {
            tvProductsTitle.setText("السبائك");

            productList.add(new Product("سبيكة 250 غرام", 250.0));
            productList.add(new Product("سبيكة 500 غرام", 500.0));
            productList.add(new Product("سبيكة 1 كيلو", 1000.0));
            productList.add(new Product("أونصة سويسرية", 120.0));
            productList.add(new Product("أونصة إيطالية", 125.0));

        } else if (type != null && type.equals("accessories")) {
            tvProductsTitle.setText("الإكسسوارات");

            productList.add(new Product("خاتم فضة رجالي", 80.0));
            productList.add(new Product("خاتم فضة نسائي", 75.0));
            productList.add(new Product("سوار فضة", 150.0));
            productList.add(new Product("سلسال فضة", 180.0));
            productList.add(new Product("حلق فضة", 60.0));
        }

        adapter = new ProductAdapter(this, productList, this::updateCartButtonCount);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerProducts.setAdapter(adapter);

        updateCartButtonCount();

        btnOpenCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductsActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnBackToStore.setOnClickListener(v -> finish());

        btnLogoutFromProducts.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButtonCount();
    }

    private void updateCartButtonCount() {
        int count = CartManager.getTotalItemsCount(this);
        btnOpenCart.setText("🛒 " + count);
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("تسجيل الخروج")
                .setMessage("هل تريد تسجيل الخروج؟")
                .setPositiveButton("نعم", (dialog, which) -> logoutUser())
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void logoutUser() {
        UserManager.logout(this);

        Intent intent = new Intent(ProductsActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}