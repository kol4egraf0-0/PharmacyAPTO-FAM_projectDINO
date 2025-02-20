package com.example.aptofam.Utility;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.aptofam.Activity.CatalogActivity;
import com.example.aptofam.Activity.EmptyGoToShoppingActivity;
import com.example.aptofam.Activity.FavouriteActivity;
import com.example.aptofam.Activity.MainActivity;
import com.example.aptofam.Activity.CartActivity;
import com.example.aptofam.Activity.ProfileActivity;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.R;

public class NavigationUtility {

    // Переменная для хранения предыдущей активной кнопки
    private static LinearLayout previousActiveButton;

    public static void setupBottomNavigation(Activity activity, View bindingRoot) {
        LinearLayout homeBtn = bindingRoot.findViewById(R.id.homeBtn);
        LinearLayout cartBtn = bindingRoot.findViewById(R.id.cartBtn);
        LinearLayout favoriteBtn = bindingRoot.findViewById(R.id.favoriteBtn);
        LinearLayout catalogBtn = bindingRoot.findViewById(R.id.catalogBtn);
        LinearLayout profileBtn = bindingRoot.findViewById(R.id.profileBtn);

        ImageView backBtn = bindingRoot.findViewById(R.id.backBtn);

        ManagmentCart managmentCart = new ManagmentCart(activity);
        ManagmentFavorite managmentFavorite = new ManagmentFavorite(activity);

        LinearLayout currentButton = null;

        // Определяем текущую активную кнопку
        if (activity instanceof MainActivity) {
            currentButton = homeBtn;
        } else if (activity instanceof CartActivity) {
            currentButton = cartBtn;
        } else if (activity instanceof FavouriteActivity) {
            currentButton = favoriteBtn;
        } else if (activity instanceof CatalogActivity) {
            currentButton = catalogBtn;
        } else if (activity instanceof ProfileActivity) {
            currentButton = profileBtn;
        } else if (activity instanceof EmptyGoToShoppingActivity) {
            Intent intent = activity.getIntent();
            String messageType = intent.getStringExtra("messageType");

            if ("cart".equals(messageType)) {
                currentButton = cartBtn;
            } else if ("favorite".equals(messageType)) {
                currentButton = favoriteBtn;
            } else {
                if (managmentCart.getListCart().isEmpty()) {
                    currentButton = cartBtn;
                } else if (managmentFavorite.getFavoriteList().isEmpty()) {
                    currentButton = favoriteBtn;
                }
            }
        }

        // Применяем анимацию к текущей кнопке
        if (currentButton != null) {
            applyScaleAnimation(currentButton, true);
            previousActiveButton = currentButton;
        }

        // Обработчик кликов для кнопок навигации
        View.OnClickListener listener = v -> {
            LinearLayout clickedButton = (LinearLayout) v;

            if (previousActiveButton != null && previousActiveButton != clickedButton) {
                applyScaleAnimation(previousActiveButton, false);
            }

            applyScaleAnimation(clickedButton, true);
            previousActiveButton = clickedButton;

            if (v == homeBtn && !(activity instanceof MainActivity)) {
                resetAllButtons(bindingRoot); // Сбрасываем все кнопки перед переходом
                activity.startActivity(new Intent(activity, MainActivity.class));
            } else if (v == cartBtn) {
                resetAllButtons(bindingRoot); // Сбрасываем все кнопки перед переходом
                Intent cartIntent = new Intent(activity, EmptyGoToShoppingActivity.class);

                if (!managmentCart.getListCart().isEmpty()) {
                    activity.startActivity(new Intent(activity, CartActivity.class));
                } else {
                    cartIntent.putExtra("messageType", "cart");
                    activity.startActivity(cartIntent);
                }
            } else if (v == favoriteBtn) {
                resetAllButtons(bindingRoot); // Сбрасываем все кнопки перед переходом
                Intent favoriteIntent = new Intent(activity, EmptyGoToShoppingActivity.class);

                if (!managmentFavorite.getFavoriteList().isEmpty()) {
                    activity.startActivity(new Intent(activity, FavouriteActivity.class));
                } else {
                    favoriteIntent.putExtra("messageType", "favorite");
                    activity.startActivity(favoriteIntent);
                }

            } else if (v == catalogBtn && !(activity instanceof CatalogActivity)) {
                resetAllButtons(bindingRoot); // Сбрасываем все кнопки перед переходом
                activity.startActivity(new Intent(activity, CatalogActivity.class));
            } else if (v == profileBtn && !(activity instanceof ProfileActivity)) {
                resetAllButtons(bindingRoot); // Сбрасываем все кнопки перед переходом
                activity.startActivity(new Intent(activity, ProfileActivity.class));
            }
        };

        homeBtn.setOnClickListener(listener);
        cartBtn.setOnClickListener(listener);
        favoriteBtn.setOnClickListener(listener);
        catalogBtn.setOnClickListener(listener);
        profileBtn.setOnClickListener(listener);

        // Логика кнопки "Назад"
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                resetAllButtons(bindingRoot); // Сбрасываем все кнопки

                // Предполагаем, что возвращаемся на Главную
                applyScaleAnimation(homeBtn, true);

                // Завершаем Activity
                activity.finish();
            });
        }
    }



    private static void applyScaleAnimation(LinearLayout button, boolean isActive) {
        ImageView icon = (ImageView) button.getChildAt(0); // Предполагаем, что ImageView первый

        // Масштабирование
        float fromScale = isActive ? 1.0f : 1.2f;
        float toScale = isActive ? 1.2f : 1.0f;

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                fromScale, toScale, // X
                fromScale, toScale, // Y
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f, // Pivot X
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f  // Pivot Y
        );
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);

        icon.startAnimation(scaleAnimation); // Применяем анимацию к иконке
    }
    private static void resetButtonState(LinearLayout button) {
        if (button != null) {
            ImageView icon = (ImageView) button.getChildAt(0); // Предполагаем, что ImageView первый элемент
            if (icon != null) {
                icon.clearAnimation(); // Убираем все анимации
                icon.setScaleX(1.0f);  // Сбрасываем масштаб по X
                icon.setScaleY(1.0f);  // Сбрасываем масштаб по Y
            }
        }
    }
    private static void resetAllButtons(View bindingRoot) {
        // Сбрасываем анимацию для всех кнопок навигации
        LinearLayout[] allButtons = {
                bindingRoot.findViewById(R.id.homeBtn),
                bindingRoot.findViewById(R.id.cartBtn),
                bindingRoot.findViewById(R.id.favoriteBtn),
                bindingRoot.findViewById(R.id.catalogBtn),
                bindingRoot.findViewById(R.id.profileBtn)
        };

        for (LinearLayout button : allButtons) {
            resetButtonState(button);
        }
    }
}