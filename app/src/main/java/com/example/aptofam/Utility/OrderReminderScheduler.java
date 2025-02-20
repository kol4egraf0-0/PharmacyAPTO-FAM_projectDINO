package com.example.aptofam.Utility;

import android.content.Context;
import android.util.Log;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OrderReminderScheduler {

    public static void scheduleOrderReminder(Context context, String orderId, long pickupDeadlineMillis) {
        long delay = pickupDeadlineMillis - System.currentTimeMillis();
        if (delay < 0) delay = 0;

        saveNotificationToFirebase(orderId, "Ваш заказ " + orderId + " создан!", pickupDeadlineMillis);

        Data data = new Data.Builder()
                .putString("orderId", orderId)
                .build();

        WorkRequest reminderWork = new OneTimeWorkRequest.Builder(OrderReminderWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        WorkManager.getInstance(context).enqueue(reminderWork);
    }

    private static void saveNotificationToFirebase(String orderId, String message, long timestamp) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            Log.e("OrderReminderScheduler", "Ошибка: пользователь не авторизован");
            return;
        }

        DatabaseReference notificationsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("notifications");

        String notificationId = notificationsRef.push().getKey();

        if (notificationId != null) {
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("notificationId", notificationId);
            notificationData.put("message", message);
            notificationData.put("timestamp", timestamp);

            notificationsRef.child(notificationId).setValue(notificationData)
                    .addOnSuccessListener(aVoid -> Log.d("OrderReminderScheduler", "Уведомление добавлено в Firebase"))
                    .addOnFailureListener(e -> Log.e("OrderReminderScheduler", "Ошибка при добавлении уведомления: " + e.getMessage()));
        }
    }
}
