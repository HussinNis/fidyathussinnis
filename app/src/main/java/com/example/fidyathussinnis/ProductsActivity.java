package com.example.fidyathussinnis;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

    private TextView tvProductsTitle;
    private RecyclerView recyclerProducts;
    private Button btnOpenCart, btnBackToStore, btnLogoutFromProducts;

    private ArrayList<Product> productList;
    private ProductAdapter adapter;

    private double silverPricePerGram = 4.5;
    private final double profitPercent = 0.10;
    private ListenerRegistration cartCountListener;

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

            addBarProduct("سبيكة 250 غرام", 250, R.drawable.silver_bar250);
            addBarProduct("سبيكة 500 غرام", 500, R.drawable.silver_bar500);
            addBarProduct("سبيكة 1 كيلو", 1000, R.drawable.silver_bar1000);
            addBarProduct("أونصة سويسرية", 31.1035, R.drawable.swisons);
            addBarProduct("أونصة إيطالية", 31.1035, R.drawable.italons);

        } else if (type != null && type.equals("accessories")) {
            tvProductsTitle.setText("الإكسسوارات");

            productList.add(new Product("خاتم فضة رجالي", 80, "خاتم فضة جاهز بسعر ثابت", R.drawable.silver_ringman2));
            productList.add(new Product("خاتم فضة نسائي", 75, "خاتم فضة نسائي بسعر ثابت", R.drawable.silver_ring));
            productList.add(new Product("سوار فضة", 150, "سوار فضة أنيق بسعر ثابت", R.drawable.silver_chain));
            productList.add(new Product("سلسال فضة", 180, "سلسال فضة بسعر ثابت", R.drawable.silver_slsal));
            productList.add(new Product("حلق فضة", 60, "حلق فضة بسعر ثابت", R.drawable.silver_bracelet));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cartCountListener != null) {
            cartCountListener.remove();
        }
    }

    private void addBarProduct(String name, double grams, int imageResId) {
        double basePrice = grams * silverPricePerGram;
        double profit = basePrice * profitPercent;
        int finalPrice = (int) Math.round(basePrice + profit);

        String details = "الوزن: " + (int) Math.round(grams) + " غرام"
                + "\nالسعر الخام: " + (int) Math.round(basePrice) + " ₪"
                + "\nالهامش: " + (int) Math.round(profit) + " ₪"
                + "\nالسعر النهائي: " + finalPrice + " ₪";

        productList.add(new Product(name, finalPrice, details, imageResId));
    }

    private void updateCartButtonCount() {
        if (cartCountListener != null) {
            cartCountListener.remove();
        }

        cartCountListener = CartManager.listenToCartCount(new CartManager.CartCountListenerCallback() {
            @Override
            public void onCountChanged(int count) {
                btnOpenCart.setText("🛒 " + count);
            }

            @Override
            public void onFailure(String message) {
                btnOpenCart.setText("🛒 0");
            }
        });
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
        FirebaseUserManager.logout();

        Intent intent = new Intent(ProductsActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}