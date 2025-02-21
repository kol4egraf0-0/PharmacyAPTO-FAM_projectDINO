package com.example.aptofam.Utility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.example.aptofam.Activity.HistoryOrderActivity;
import com.example.aptofam.R;
//Для увед локальных
public class NotificationHelper {

    private static final String TAG = "NotificationHelper";
    private static final String CHANNEL_ID = "order_notification_channel";
    private static int notificationId = 1;

    /**
     * Показывает уведомление о создании заказа с возможностью открытия Activity
     *
     * @param context Контекст приложения
     * @param orderId Номер заказа для передачи в Activity
     */
    public static void showOrderCreatedNotification(Context context, String orderId) {
        if (context == null) {
            Log.e(TAG, "Контекст не может быть null!");
            return;
        }

        Log.d(TAG, "Попытка показать уведомление...");

        createNotificationChannel(context);

        Intent intent = new Intent(context, HistoryOrderActivity.class); // Замените на вашу Activity
        intent.putExtra("order_id", orderId); // Передаем номер заказа
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.imageaptodrawable)
                .setContentTitle("Заказ создан!")
                .setContentText("Номер заказа: " + orderId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.imageaptodrawable));

        Log.d(TAG, "Уведомление создано. Попытка отправки...");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            Log.e(TAG, "NotificationManager не инициализирован!");
            return;
        }

        try {
            manager.notify(notificationId++, builder.build());
            Log.d(TAG, "Уведомление успешно отправлено!");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при отправке уведомления: " + e.getMessage(), e);
        }
    }

    /**
     * Создает канал уведомлений для устройств с Android 8.0+ (API 26+)
     *
     * @param context Контекст приложения
     */
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.d(TAG, "Канал уведомлений не требуется (API < 26)");
            return;
        }

        Log.d(TAG, "Создание канала уведомлений...");

        CharSequence name = "Заказы";
        String description = "Уведомления о создании заказов";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        channel.setShowBadge(true);
        channel.enableLights(true);
        channel.enableVibration(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            Log.e(TAG, "NotificationManager не инициализирован при создании канала!");
            return;
        }

        try {
            manager.createNotificationChannel(channel);
            Log.d(TAG, "Канал уведомлений успешно создан!");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании канала уведомлений: " + e.getMessage(), e);
        }
    }
    public static void showOrderReminderNotification(Context context, String orderId) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, HistoryOrderActivity.class);
        intent.putExtra("order_id", orderId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.imageaptodrawable)
                .setContentTitle("Напоминание о заказе!")
                .setContentText("Заказ " + orderId + " скоро будет аннулирован. Заберите его!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.imageaptodrawable));

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            Log.e(TAG, "Ошибка: NotificationManager не инициализирован!");
            return;
        }

        try {
            manager.notify(notificationId++, builder.build());
            Log.d(TAG, "Уведомление напоминания успешно отправлено!");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при отправке уведомления: " + e.getMessage(), e);
        }
    }

}