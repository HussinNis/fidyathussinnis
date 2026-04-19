package com.example.fidyathussinnis;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class CartManager {

    public interface CartLoadCallback {
        void onCartLoaded(ArrayList<CartItem> cartItems);
        void onFailure(String message);
    }

    public interface CartActionCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public interface CartCountCallback {
        void onCountLoaded(int count);
        void onFailure(String message);
    }

    public interface CartCountListenerCallback {
        void onCountChanged(int count);
        void onFailure(String message);
    }

    private static CollectionReference getCartCollection() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            return null;
        }

        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.getUid())
                .collection("cartItems");
    }

    public static void addToCart(CartItem newItem, CartActionCallback callback) {
        CollectionReference cartCollection = getCartCollection();

        if (cartCollection == null) {
            callback.onFailure("لا يوجد مستخدم مسجل الدخول");
            return;
        }

        cartCollection.document(newItem.getName())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        CartItem oldItem = documentSnapshot.toObject(CartItem.class);

                        int oldQuantity = oldItem != null ? oldItem.getQuantity() : 0;
                        int newQuantity = oldQuantity + newItem.getQuantity();

                        cartCollection.document(newItem.getName())
                                .update("quantity", newQuantity)
                                .addOnSuccessListener(unused -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    } else {
                        cartCollection.document(newItem.getName())
                                .set(newItem)
                                .addOnSuccessListener(unused -> callback.onSuccess())
                                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static ListenerRegistration listenToCart(CartLoadCallback callback) {
        CollectionReference cartCollection = getCartCollection();

        if (cartCollection == null) {
            callback.onFailure("لا يوجد مستخدم مسجل الدخول");
            return null;
        }

        return cartCollection.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }

            ArrayList<CartItem> items = new ArrayList<>();

            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    CartItem item = doc.toObject(CartItem.class);
                    items.add(item);
                }
            }

            callback.onCartLoaded(items);
        });
    }

    public static ListenerRegistration listenToCartCount(CartCountListenerCallback callback) {
        CollectionReference cartCollection = getCartCollection();

        if (cartCollection == null) {
            callback.onCountChanged(0);
            return null;
        }

        return cartCollection.addSnapshotListener((snapshots, error) -> {
            if (error != null) {
                callback.onFailure(error.getMessage());
                return;
            }

            int totalCount = 0;

            if (snapshots != null) {
                for (QueryDocumentSnapshot doc : snapshots) {
                    CartItem item = doc.toObject(CartItem.class);
                    totalCount += item.getQuantity();
                }
            }

            callback.onCountChanged(totalCount);
        });
    }

    public static void updateQuantity(String itemName, int newQuantity, CartActionCallback callback) {
        CollectionReference cartCollection = getCartCollection();

        if (cartCollection == null) {
            callback.onFailure("لا يوجد مستخدم مسجل الدخول");
            return;
        }

        cartCollection.document(itemName)
                .update("quantity", newQuantity)
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void removeItem(String itemName, CartActionCallback callback) {
        CollectionReference cartCollection = getCartCollection();

        if (cartCollection == null) {
            callback.onFailure("لا يوجد مستخدم مسجل الدخول");
            return;
        }

        cartCollection.document(itemName)
                .delete()
                .addOnSuccessListener(unused -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void clearCart(CartActionCallback callback) {
        CollectionReference cartCollection = getCartCollection();

        if (cartCollection == null) {
            callback.onFailure("لا يوجد مستخدم مسجل الدخول");
            return;
        }

        cartCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = FirebaseFirestore.getInstance().batch();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(unused -> callback.onSuccess())
                            .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void getTotalItemsCount(CartCountCallback callback) {
        CollectionReference cartCollection = getCartCollection();

        if (cartCollection == null) {
            callback.onCountLoaded(0);
            return;
        }

        cartCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int total = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        CartItem item = doc.toObject(CartItem.class);
                        total += item.getQuantity();
                    }

                    callback.onCountLoaded(total);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}