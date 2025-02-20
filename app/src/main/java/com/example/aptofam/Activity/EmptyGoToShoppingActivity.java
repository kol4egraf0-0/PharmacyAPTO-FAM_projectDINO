package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.databinding.ActivityEmptyGoToShoppingBinding;

public class EmptyGoToShoppingActivity extends BaseActivity {
    private ActivityEmptyGoToShoppingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmptyGoToShoppingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String messageType = getIntent().getStringExtra("messageType");
        // Изменяем текст в зависимости от типа
        if ("cart".equals(messageType)) {
            binding.tvEmptyMessageTitle.setText("Ваша корзина пуста");
            binding.tvEmptyMessageSubtitle.setText("Добавляйте товары в корзину и возвращайтесь для оформления заказа.");
        } else if ("favorite".equals(messageType)) {
            binding.tvEmptyMessageTitle.setText("Список избранных товаров пуст");
            binding.tvEmptyMessageSubtitle.setText("Добавляйте товары в избранное — выбирайте товары и жмите на «сердечко».");
        }

        binding.btnGoShopping.setOnClickListener(v -> {
            Intent intent = new Intent(EmptyGoToShoppingActivity.this, MainActivity.class);
            startActivity(intent);
        });

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
    }
}
