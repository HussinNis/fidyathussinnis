package com.example.fidyathussinnis;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private ArrayList<Order> orderList;

    public OrderAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderUserName.setText("الاسم: " + order.getUserName());
        holder.tvOrderEmail.setText("الإيميل: " + order.getUserEmail());
        holder.tvOrderTotal.setText("المجموع: " + order.getTotalPrice() + " ₪");
        holder.tvOrderProductsCount.setText("عدد المنتجات: " + order.getProductsCount());
        holder.tvOrderStatus.setText("الحالة: " + order.getStatus());
        holder.tvOrderDate.setText("الوقت: " + formatDate(order.getCreatedAt()));

        holder.itemView.setOnClickListener(v -> showOrderDetails(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    private String formatDate(long timeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timeMillis));
    }

    private void showOrderDetails(Order order) {
        StringBuilder details = new StringBuilder();

        List<Map<String, Object>> products = order.getProducts();

        if (products != null && !products.isEmpty()) {
            for (Map<String, Object> product : products) {
                String name = String.valueOf(product.get("name"));
                Object priceObj = product.get("price");
                Object quantityObj = product.get("quantity");
                Object totalObj = product.get("itemTotal");

                details.append("المنتج: ").append(name).append("\n")
                        .append("سعر الوحدة: ").append(priceObj).append(" ₪\n")
                        .append("الكمية: ").append(quantityObj).append("\n")
                        .append("الإجمالي: ").append(totalObj).append(" ₪\n\n");
            }
        } else {
            details.append("لا توجد تفاصيل للمنتجات");
        }

        new AlertDialog.Builder(context)
                .setTitle("تفاصيل الطلب")
                .setMessage(details.toString())
                .setPositiveButton("إغلاق", null)
                .show();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderUserName, tvOrderEmail, tvOrderTotal, tvOrderProductsCount, tvOrderStatus, tvOrderDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderUserName = itemView.findViewById(R.id.tvOrderUserName);
            tvOrderEmail = itemView.findViewById(R.id.tvOrderEmail);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            tvOrderProductsCount = itemView.findViewById(R.id.tvOrderProductsCount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
        }
    }
}