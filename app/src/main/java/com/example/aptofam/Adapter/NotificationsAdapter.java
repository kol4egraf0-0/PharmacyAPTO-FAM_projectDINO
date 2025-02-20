package com.example.aptofam.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Model.NotificationModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.ToastUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private Context context;
    private List<NotificationModel> notifications;
    private DatabaseReference notificationsRef;

    public NotificationsAdapter(Context context, List<NotificationModel> notifications, DatabaseReference notificationsRef) {
        this.context = context;
        this.notifications = notifications;
        this.notificationsRef = notificationsRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);
        holder.messageTextView.setText(notification.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        holder.timestampTextView.setText(sdf.format(new Date(notification.getTimestamp())));

        // Удаление уведомления при нажатии на иконку корзины
        holder.binIcon.setOnClickListener(v -> {
            deleteNotification(notification.getNotificationId(), position);
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    private void deleteNotification(String notificationId, int position) {
        if (notifications.isEmpty() || position < 0 || position >= notifications.size()) return;

        notificationsRef.child(notificationId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Проверяем, что элемент все еще есть в списке
                    if (position < notifications.size()) {
                        notifications.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, notifications.size());
                    }

                    // Показать Toast после успешного удаления
                    ToastUtils.showToast((AppCompatActivity) context, "Уведомление удалено!");
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Ошибка удаления: " + e.getMessage()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timestampTextView;
        ImageView binIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            timestampTextView = itemView.findViewById(R.id.notificationTimestamp);
            binIcon = itemView.findViewById(R.id.binIcon); // Иконка корзины
        }
    }
}

