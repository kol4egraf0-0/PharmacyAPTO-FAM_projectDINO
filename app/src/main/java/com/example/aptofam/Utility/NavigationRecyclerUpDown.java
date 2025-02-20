package com.example.aptofam.Utility;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NavigationRecyclerUpDown {

    public static void setupRecyclerViewScrollListener(RecyclerView recyclerView, View bottomNav) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean isBottomNavVisible = true;

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && isBottomNavVisible) {
                    hideBottomNavigation(bottomNav);
                    isBottomNavVisible = false;
                } else if (dy < 0 && !isBottomNavVisible) {
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
