package com.example.aptofam.Utility;

import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
public class NavigationScrollvUpDown {
    public static void setupScrollViewScrollListener(ScrollView scrollView, View bottomNav) {
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            private boolean isBottomNavVisible = true;

            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY(); // Получаем вертикальную прокрутку
                int scrollRange = scrollView.getChildAt(0).getHeight() - scrollView.getHeight();

                // Прокрутка вниз
                if (scrollY > 0 && isBottomNavVisible) {
                    hideBottomNavigation(bottomNav);
                    isBottomNavVisible = false;
                }
                // Прокрутка вверх
                else if (scrollY == 0 && !isBottomNavVisible) {
                    showBottomNavigation(bottomNav);
                    isBottomNavVisible = true;
                }
            }
        });
    }

    private static void hideBottomNavigation(View bottomNav) {
        bottomNav.animate()
                .translationY(bottomNav.getHeight())
                .setDuration(300)
                .withEndAction(() -> bottomNav.setVisibility(View.GONE))
                .start();
    }

    private static void showBottomNavigation(View bottomNav) {
        bottomNav.animate()
                .translationY(0)
                .setDuration(300)
                .start();
        bottomNav.setVisibility(View.VISIBLE);
    }
}
