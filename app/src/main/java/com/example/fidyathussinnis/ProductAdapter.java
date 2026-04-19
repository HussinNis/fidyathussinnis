package com.example.fidyathussinnis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    private Context context;
    private ArrayList<Product> productList;
    private OnCartChangedListener listener;

    public ProductAdapter(Context context, ArrayList<Product> productList, OnCartChangedListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.imgProduct.setImageResource(product.getImageResId());
        holder.tvProductName.setText(product.getName());
        holder.tvProductDetails.setText(product.getDetails());
        holder.tvProductPrice.setText("السعر: " + product.getPrice() + " ₪");

        holder.btnAddToCart.setOnClickListener(v -> {
            CartItem cartItem = new CartItem(
                    product.getName(),
                    product.getPrice(),
                    1,
                    product.getImageResId()
            );

            CartManager.addToCart(cartItem, new CartManager.CartActionCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(context, "تمت إضافة المنتج إلى السلة", Toast.LENGTH_SHORT).show();
                    if (listener != null) {
                        listener.onCartChanged();
                    }
                }

                @Override
                public void onFailure(String message) {
                    Toast.makeText(context, "فشل الإضافة: " + message, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvProductDetails, tvProductPrice;
        Button btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductDetails = itemView.findViewById(R.id.tvProductDetails);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}