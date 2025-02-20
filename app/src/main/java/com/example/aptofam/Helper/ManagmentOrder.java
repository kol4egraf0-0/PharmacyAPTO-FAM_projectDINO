package com.example.aptofam.Helper;

import android.content.Context;
import android.util.Log;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.Utility.NotificationHelper;
import com.example.aptofam.Utility.OrderReminderScheduler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ManagmentOrder {
    private static final String TAG = "ManagmentOrder";
    private Context context;
    private DatabaseReference firebaseReference;
    private String userId;

    public ManagmentOrder(Context context) {
        this.context = context;
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
            firebaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("orders");
        } else {
            userId = null;
        }
    }

    public void createOrder(ArrayList<ItemsModel> orderItems, double totalPrice) {
        String orderId = String.valueOf(1000 + new Random().nextInt(9000));
        long timestamp = System.currentTimeMillis();
        long pickupDeadline = timestamp + 60 * 1000; // Напоминание через 1 минуту

        // Сохраняем заказ в Firebase
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", orderId);
        orderData.put("totalPrice", totalPrice);
        orderData.put("timestamp", timestamp);
        orderData.put("pickupDeadline", pickupDeadline);
        orderData.put("items", orderItems);

        firebaseReference.child(orderId).setValue(orderData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Заказ успешно создан!");
                    OrderReminderScheduler.scheduleOrderReminder(context, orderId, pickupDeadline); // Запускаем WorkManager
                })
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка при создании заказа: " + e.getMessage()));

        NotificationHelper.showOrderCreatedNotification(context.getApplicationContext(), orderId);
    }

    public void deleteOrderFromFirebase(String orderId, Runnable onDeleted) {
        if (userId == null) {
            return;
        }
        firebaseReference.child(orderId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Заказ " + orderId + " удален!");
                    firebaseReference.child(orderId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                Log.d(TAG, "Проверка: заказ с ID " + orderId + " действительно удален из Firebase!");
                                onDeleted.run();
                            } else {
                                Log.e(TAG, "Заказ с ID " + orderId + " все еще существует в Firebase!");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "Ошибка при проверке удаления заказа: " + databaseError.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка при удалении заказа: " + e.getMessage()));
    }

    public void getOrders(OrderLoadListener listener) {

        firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Map<String, Object>> orders = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> order = (Map<String, Object>) snapshot.getValue();
                    if (order != null) {
                        orders.add(order);
                    }
                }

                listener.onOrdersLoaded(orders);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onError(databaseError.getMessage());
            }
        });
    }

    public interface OrderLoadListener {
        void onOrdersLoaded(List<Map<String, Object>> orders);
        void onError(String error);
    }
}
