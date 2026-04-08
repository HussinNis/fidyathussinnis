package com.example.fidyathussinnis;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private TextView tvTotalPrice;
    private Button btnCheckout, btnClearCart, btnBackFromCart;

    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recyclerCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnBackFromCart = findViewById(R.id.btnBackFromCart);

        cartList = CartManager.getCartItems(this);

        cartAdapter = new CartAdapter(this, cartList, this::updateTotalPrice);
        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(cartAdapter);

        updateTotalPrice();

        btnCheckout.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "السلة فارغة", Toast.LENGTH_SHORT).show();
            } else {
                showCheckoutDialog();
            }
        });

        btnClearCart.setOnClickListener(v -> {
            if (cartList.isEmpty()) {
                Toast.makeText(this, "السلة فارغة", Toast.LENGTH_SHORT).show();
            } else {
                showClearCartDialog();
            }
        });

        btnBackFromCart.setOnClickListener(v -> finish());
    }

    private void updateTotalPrice() {
        double total = 0;

        for (CartItem item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }

        tvTotalPrice.setText("المجموع: " + total + " ₪");
    }

    private void showCheckoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الطلب")
                .setMessage("هل تريد إتمام الطلب الآن؟")
                .setPositiveButton("نعم", (dialog, which) -> completeOrder())
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void showClearCartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("حذف كل المنتجات")
                .setMessage("هل أنت متأكد أنك تريد حذف كل المنتجات من السلة؟")
                .setPositiveButton("نعم", (dialog, which) -> clearCart())
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void completeOrder() {
        CartManager.clearCart(this);
        cartList.clear();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
        Toast.makeText(this, "تم إتمام الطلب بنجاح", Toast.LENGTH_LONG).show();
    }

    private void clearCart() {
        CartManager.clearCart(this);
        cartList.clear();
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();
        Toast.makeText(this, "تم حذف كل المنتجات من السلة", Toast.LENGTH_SHORT).show();
    }
}