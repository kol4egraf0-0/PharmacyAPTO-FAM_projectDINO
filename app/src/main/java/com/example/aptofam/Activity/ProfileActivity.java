package com.example.aptofam.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Adapter.ProfileAccUsfAdapter;
import com.example.aptofam.Model.ProfileButtonModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity {

    private ActivityProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);

        initProfileAccButtons();

        initProfileUsfButtons();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void initProfileAccButtons() {
        RecyclerView recyclerView = findViewById(R.id.profileAccountView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<ProfileButtonModel> buttons = new ArrayList<>();
        buttons.add(new ProfileButtonModel("История заказов", R.drawable.history_icon, HistoryOrderActivity.class));
        buttons.add(new ProfileButtonModel("Уведомления", R.drawable.bellprofile, NotificationsActivity.class));
        buttons.add(new ProfileButtonModel("Промокоды", R.drawable.coupon, CouponsActivity.class));

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if ("GpkrvQpcTfTiajzH6WKSAY6tx1j2".equals(uid)) {
                buttons.add(new ProfileButtonModel("Администрирование товаров", R.drawable.admin, AdminItemsActivity.class));
            }
        }

        ProfileAccUsfAdapter adapter = new ProfileAccUsfAdapter(buttons, this);
        recyclerView.setAdapter(adapter);
    }

    private void initProfileUsfButtons() {
        RecyclerView recyclerView = findViewById(R.id.profileUsefulView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<ProfileButtonModel> buttons = new ArrayList<>();
        buttons.add(new ProfileButtonModel("Контакты и техПоддержка", R.drawable.technical_support, ContactsAndSupportActivity.class));
        buttons.add(new ProfileButtonModel("Вопрос-ответ", R.drawable.customer_service, QuestionAnswerActivity.class));
        buttons.add(new ProfileButtonModel("О приложении/Политика данных", R.drawable.mobile_development, AboutAppActivity.class));

        ProfileAccUsfAdapter adapter = new ProfileAccUsfAdapter(buttons, this);
        recyclerView.setAdapter(adapter);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
        binding.editProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ProfileEditActivity.class);
            startActivity(intent);
        });
        binding.logoutButton.setOnClickListener(v -> {
            logoutUser();
        });
    }

    private void logoutUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        FirebaseAuth.getInstance().signOut();
        ToastUtils.showToast(this, "Вы вышли из аккаунта");

        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onResume() {
        super.onResume();
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
    }
}