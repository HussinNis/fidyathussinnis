package com.example.fidyathussinnis;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(item.getPrice() + " ₪");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            CartManager.updateCart(context);
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                CartManager.updateCart(context);
                notifyItemChanged(position);
                listener.onCartUpdated();
            } else {
                new AlertDialog.Builder(context)
                        .setTitle("حذف المنتج")
                        .setMessage("هل تريد حذف المنتج من السلة؟")
                        .setPositiveButton("نعم", (dialog, which) -> {
                            CartManager.removeItem(context, position);
                            cartList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, cartList.size());
                            listener.onCartUpdated();
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
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnPlus, btnMinus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}