package com.example.aptofam.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.annotation.NonNull;
import com.example.aptofam.Adapter.NotificationsAdapter;
import com.example.aptofam.Model.NotificationModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivityNotificationsBinding;
import com.example.aptofam.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends BaseActivity {
    private ActivityNotificationsBinding binding;
    private NotificationsAdapter adapter;
    private List<NotificationModel> notificationsList = new ArrayList<>();
    private DatabaseReference notificationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);

        setVariable();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);

        // Firebase Database
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId != null) {
            notificationsRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(userId)
                    .child("notifications");
        }

        // Инициализация RecyclerView
        binding.recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter(this, notificationsList, notificationsRef);
        binding.recyclerNotifications.setAdapter(adapter);

        loadNotifications();

        binding.textViewRemoveAll.setOnClickListener(v -> clearAllNotifications());
    }

    private void clearAllNotifications() {
        if (notificationsRef == null || notificationsList.isEmpty()) return;

        notificationsRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    notificationsList.clear();
                    adapter.notifyDataSetChanged();
                    ToastUtils.showToast(this, "Все уведомления удалены!");
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Ошибка удаления всех уведомлений: " + e.getMessage()));
    }

    private void loadNotifications() {
        if (notificationsRef == null) return;

        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationsList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    NotificationModel notification = data.getValue(NotificationModel.class);
                    if (notification != null) {
                        notification.setNotificationId(data.getKey());
                        notificationsList.add(notification);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Ошибка загрузки уведомлений: " + error.getMessage());
            }
        });
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
    }
}
