package com.example.aptofam.Utility;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class OrderReminderWorker extends Worker {

    public OrderReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String orderId = getInputData().getString("orderId");

        if (orderId != null) {
            Log.d("OrderReminderWorker", "Напоминание сработало! Заказ ID: " + orderId);
            NotificationHelper.showOrderReminderNotification(getApplicationContext(), orderId);
            saveNotificationToFirebase(orderId);
            return Result.success();
        } else {
            Log.e("OrderReminderWorker", "Ошибка: Получен пустой order_id");
            return Result.failure();
        }
    }

    private void saveNotificationToFirebase(String orderId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Log.e("OrderReminderWorker", "Ошибка: пользователь не авторизован!");
            return;
        }

        DatabaseReference notificationsRef = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId).child("notifications");

        String notificationId = String.valueOf(System.currentTimeMillis());

        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("notificationId", notificationId);
        notificationData.put("message", "Напоминание о заказе " + orderId);
        notificationData.put("timestamp", System.currentTimeMillis());

        notificationsRef.child(notificationId).setValue(notificationData)
                .addOnSuccessListener(aVoid -> Log.d("OrderReminderWorker", "Уведомление сохранено в Firebase"))
                .addOnFailureListener(e -> Log.e("OrderReminderWorker", "Ошибка сохранения уведомления: " + e.getMessage()));
    }


}
