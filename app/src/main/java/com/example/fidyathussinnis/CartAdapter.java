package com.example.fidyathussinnis;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartUpdatedListener {
        void onCartUpdated();
    }

    private Context context;
    private ArrayList<CartItem> cartList;
    private OnCartUpdatedListener listener;

    public CartAdapter(Context context, ArrayList<CartItem> cartList, OnCartUpdatedListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        int itemTotal = item.getPrice() * item.getQuantity();

        holder.imgCartItem.setImageResource(item.getImageResId());
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText("سعر الوحدة: " + item.getPrice() + " ₪");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvTotal.setText("إجمالي المنتج: " + itemTotal + " ₪");

        holder.btnPlus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;

            CartManager.updateQuantity(item.getName(), newQuantity, new CartManager.CartActionCallback() {
                @Override
                public void onSuccess() {
                    item.setQuantity(newQuantity);
                    notifyItemChanged(position);

                    if (listener != null) {
                        listener.onCartUpdated();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(context, "فشل تحديث الكمية: " + message, Toast.LENGTH_LONG).show();
                }
            });
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;

                CartManager.updateQuantity(item.getName(), newQuantity, new CartManager.CartActionCallback() {
                    @Override
                    public void onSuccess() {
                        item.setQuantity(newQuantity);
                        notifyItemChanged(position);

                        if (listener != null) {
                            listener.onCartUpdated();
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Toast.makeText(context, "فشل تحديث الكمية: " + message, Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("حذف المنتج")
                        .setMessage("هل تريد حذف المنتج من السلة؟")
                        .setPositiveButton("نعم", (dialog, which) -> {
                            CartManager.removeItem(item.getName(), new CartManager.CartActionCallback() {
                                @Override
                                public void onSuccess() {
                                    cartList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, cartList.size());

                                    if (listener != null) {
                                        listener.onCartUpdated();
                                    }
                                }

                                @Override
                                public void onFailure(String message) {
                                    Toast.makeText(context, "فشل حذف المنتج: " + message, Toast.LENGTH_LONG).show();
                                }
                            });
                        })
                        .setNegativeButton("إلغاء", null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCartItem;
        TextView tvName, tvPrice, tvQuantity, tvTotal;
        ImageButton btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCartItem = itemView.findViewById(R.id.imgCartItem);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            tvTotal = itemView.findViewById(R.id.tvCartItemTotal);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}