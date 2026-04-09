package com.example.fidyathussinnis;

import android.content.Intent;
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

    private double silverPricePerGram = 4.5;
    private final double profitPercent = 0.10;

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
        silverPricePerGram = getIntent().getDoubleExtra("silver_price", 4.5);

        productList = new ArrayList<>();

        if (type != null && type.equals("bars")) {
            tvProductsTitle.setText("السبائك");

            addBarProduct("سبيكة 250 غرام", 250);
            addBarProduct("سبيكة 500 غرام", 500);
            addBarProduct("سبيكة 1 كيلو", 1000);
            addBarProduct("أونصة سويسرية", 31.1035);
            addBarProduct("أونصة إيطالية", 31.1035);

        } else if (type != null && type.equals("accessories")) {
            tvProductsTitle.setText("الإكسسوارات");

            productList.add(new Product("خاتم فضة رجالي", 80, "خاتم فضة جاهز بسعر ثابت"));
            productList.add(new Product("خاتم فضة نسائي", 75, "خاتم فضة نسائي بسعر ثابت"));
            productList.add(new Product("سوار فضة", 150, "سوار فضة أنيق بسعر ثابت"));
            productList.add(new Product("سلسال فضة", 180, "سلسال فضة بسعر ثابت"));
            productList.add(new Product("حلق فضة", 60, "حلق فضة بسعر ثابت"));
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

    private void addBarProduct(String name, double grams) {
        double basePrice = grams * silverPricePerGram;
        double profit = basePrice * profitPercent;
        int finalPrice = (int) Math.round(basePrice + profit);

        String details = "الوزن: " + formatNumber(grams) + " غرام"
                + "\nالسعر الخام: " + (int) Math.round(basePrice) + " ₪"
                + "\nالهامش: " + (int) Math.round(profit) + " ₪"
                + "\nالسعر النهائي: " + finalPrice + " ₪";

        productList.add(new Product(name, finalPrice, details));
    }

    private String formatNumber(double value) {
        return String.valueOf((int) Math.round(value));
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