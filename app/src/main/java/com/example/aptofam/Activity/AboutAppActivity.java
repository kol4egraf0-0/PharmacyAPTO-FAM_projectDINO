package com.example.aptofam.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.databinding.ActivityAboutAppBinding;
import com.example.aptofam.databinding.ActivityCartBinding;

public class AboutAppActivity extends BaseActivity {
private ActivityAboutAppBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAboutAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
        finish();
    });
    }
}