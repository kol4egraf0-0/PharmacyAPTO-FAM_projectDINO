package com.example.aptofam.Utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
//Напоминания для Заказа 1 варик
public class OrderReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String orderId = intent.getStringExtra("orderId");
        if (orderId != null) {
            Log.d("ReminderReceiver", "Напоминание сработало! Заказ ID: " + orderId);
            NotificationHelper.showOrderReminderNotification(context, orderId);
        } else {
            Log.e("ReminderReceiver", "Ошибка: Получен пустой order_id");
        }
    }
}
