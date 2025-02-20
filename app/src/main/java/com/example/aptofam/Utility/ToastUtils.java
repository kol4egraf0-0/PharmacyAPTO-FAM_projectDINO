package com.example.aptofam.Utility;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.aptofam.R;

public class ToastUtils {

    // Статический метод для показа кастомного Toast
    public static void showToast(AppCompatActivity activity, String message) {
        // Создание кастомного Toast
        Toast toast = new Toast(activity);
        // Инфлейт кастомного layout
        View customView = activity.getLayoutInflater().inflate(R.layout.custom_toast_black, null);
        TextView textView = customView.findViewById(R.id.toast_message);
        textView.setText(message);
        // Установить кастомный view и длительность
        toast.setView(customView);
        toast.setDuration(Toast.LENGTH_SHORT);
        // Позиция Toast, по центру экрана
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}