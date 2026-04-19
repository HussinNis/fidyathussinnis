package com.example.fidyathussinnis;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerCart;
    private TextView tvTotalPrice;
    private Button btnCheckout, btnClearCart, btnBackFromCart;

    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartList;

    private FirebaseFirestore db;
    private ListenerRegistration cartListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerCart = findViewById(R.id.recyclerCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnClearCart = findViewById(R.id.btnClearCart);
        btnBackFromCart = findViewById(R.id.btnBackFromCart);

        db = FirebaseFirestore.getInstance();

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartList, this::updateTotalPrice);

        recyclerCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerCart.setAdapter(cartAdapter);

        startCartListener();

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

    private void startCartListener() {
        cartListener = CartManager.listenToCart(new CartManager.CartLoadCallback() {
            @Override
            public void onCartLoaded(ArrayList<CartItem> items) {
                cartList.clear();
                cartList.addAll(items);
                cartAdapter.notifyDataSetChanged();
                updateTotalPrice();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(CartActivity.this, "فشل تحميل السلة: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartListener != null) {
            cartListener.remove();
        }
    }

    private void updateTotalPrice() {
        int total = 0;
        for (CartItem item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText("المجموع: " + total + " ₪");
    }

    private int getCartTotal() {
        int total = 0;
        for (CartItem item : cartList) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    private void showCheckoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("تأكيد الطلب")
                .setMessage("هل تريد إتمام الطلب الآن؟")
                .setPositiveButton("نعم", (dialog, which) -> saveOrderToFirestore())
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

    private void saveOrderToFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "لم يتم العثور على المستخدم الحالي", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String fullName = documentSnapshot.getString("fullName");
                    String email = documentSnapshot.getString("email");

                    ArrayList<Map<String, Object>> products = new ArrayList<>();

                    for (CartItem item : cartList) {
                        Map<String, Object> productMap = new HashMap<>();
                        productMap.put("name", item.getName());
                        productMap.put("price", item.getPrice());
                        productMap.put("quantity", item.getQuantity());
                        productMap.put("imageResId", item.getImageResId());
                        productMap.put("itemTotal", item.getPrice() * item.getQuantity());
                        products.add(productMap);
                    }

                    Map<String, Object> order = new HashMap<>();
                    order.put("userId", uid);
                    order.put("userName", fullName != null ? fullName : "User");
                    order.put("userEmail", email != null ? email : currentUser.getEmail());
                    order.put("totalPrice", getCartTotal());
                    order.put("products", products);
                    order.put("createdAt", System.currentTimeMillis());
                    order.put("status", "new");

                    db.collection("orders")
                            .add(order)
                            .addOnSuccessListener(documentReference -> {
                                CartManager.clearCart(new CartManager.CartActionCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(CartActivity.this, "تم حفظ الطلب وتفريغ السلة", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        Toast.makeText(CartActivity.this, "تم حفظ الطلب لكن فشل حذف السلة: " + message, Toast.LENGTH_LONG).show();
                                    }
                                });
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(CartActivity.this, "فشل حفظ الطلب: " + e.getMessage(), Toast.LENGTH_LONG).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(CartActivity.this, "فشل قراءة المستخدم: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void clearCart() {
        CartManager.clearCart(new CartManager.CartActionCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(CartActivity.this, "تم حذف كل المنتجات من السلة", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(CartActivity.this, "فشل حذف السلة: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
}