package com.example.fidyathussinnis;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private Button btnBackFromOrders;

    private ArrayList<Order> orderList;
    private OrderAdapter orderAdapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        btnBackFromOrders = findViewById(R.id.btnBackFromOrders);

        db = FirebaseFirestore.getInstance();

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerOrders.setAdapter(orderAdapter);

        btnBackFromOrders.setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "لم يتم العثور على المستخدم الحالي", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("orders")
                .whereEqualTo("userId", currentUser.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Order order = document.toObject(Order.class);
                        orderList.add(order);
                    }

                    Collections.sort(orderList, (o1, o2) ->
                            Long.compare(o2.getCreatedAt(), o1.getCreatedAt())
                    );

                    orderAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "فشل تحميل الطلبات: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}