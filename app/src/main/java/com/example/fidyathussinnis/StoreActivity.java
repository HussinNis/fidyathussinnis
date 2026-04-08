package com.example.fidyathussinnis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StoreActivity extends AppCompatActivity {

    private TextView tvWelcomeUser, tvSilverPrice, tvLastUpdate;
    private Button btnBars, btnAccessories, btnCalculator, btnCart, btnLogout;

    private double silverPricePerGram = 4.5;
    private static final String API_KEY = "LNQZBEMNS0MRKLMYUKUH401MYUKUH";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final int REFRESH_INTERVAL = 60000; // 60 ثانية

    private final Runnable priceUpdater = new Runnable() {
        @Override
        public void run() {
            fetchSilverPrice();
            handler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser);
        tvSilverPrice = findViewById(R.id.tvSilverPrice);
        tvLastUpdate = findViewById(R.id.tvLastUpdate);
        btnBars = findViewById(R.id.btnBars);
        btnAccessories = findViewById(R.id.btnAccessories);
        btnCalculator = findViewById(R.id.btnCalculator);
        btnCart = findViewById(R.id.btnCart);
        btnLogout = findViewById(R.id.btnLogout);

        String name = getIntent().getStringExtra("name");

        if (name != null && !name.isEmpty()) {
            tvWelcomeUser.setText("أهلاً بك " + name);
        } else {
            tvWelcomeUser.setText("أهلاً بك");
        }

        tvSilverPrice.setText("جاري تحميل سعر جرام الفضة...");
        tvLastUpdate.setText("آخر تحديث: --");

        fetchSilverPrice();
        updateCartButtonCount();

        btnBars.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, ProductsActivity.class);
            intent.putExtra("type", "bars");
            startActivity(intent);
        });

        btnAccessories.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, ProductsActivity.class);
            intent.putExtra("type", "accessories");
            startActivity(intent);
        });

        btnCalculator.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, CalculatorActivity.class);
            intent.putExtra("silver_price", silverPricePerGram);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(StoreActivity.this, CartActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartButtonCount();
        handler.post(priceUpdater);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(priceUpdater);
    }

    private void fetchSilverPrice() {
        String url = "https://api.metals.dev/v1/latest?api_key=" + API_KEY + "&currency=ILS&unit=g";

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        double liveSilverPrice = response
                                .getJSONObject("metals")
                                .getDouble("silver");

                        silverPricePerGram = liveSilverPrice;
                        tvSilverPrice.setText("سعر جرام الفضة: " + silverPricePerGram + " ₪");
                        tvLastUpdate.setText("آخر تحديث: " + getCurrentTime());
                    } catch (Exception e) {
                        tvSilverPrice.setText("سعر جرام الفضة: " + silverPricePerGram + " ₪");
                        tvLastUpdate.setText("آخر تحديث: " + getCurrentTime());
                    }
                },
                error -> {
                    tvSilverPrice.setText("سعر جرام الفضة: " + silverPricePerGram + " ₪");
                    tvLastUpdate.setText("آخر تحديث: " + getCurrentTime());
                }
        );

        queue.add(request);
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void updateCartButtonCount() {
        int count = CartManager.getTotalItemsCount(this);
        btnCart.setText("🛒 " + count);
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

        Intent intent = new Intent(StoreActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}