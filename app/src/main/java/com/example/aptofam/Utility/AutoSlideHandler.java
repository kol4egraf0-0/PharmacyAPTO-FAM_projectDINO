package com.example.aptofam.Utility;

import android.os.Handler;
import android.os.SystemClock;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
//Для автоматического перехода на ViewPager
public class AutoSlideHandler {
    private final ViewPager2 viewPager;
    private int currentPage = 0;
    private final Handler handler = new Handler();
    private long lastUserInteractionTime = SystemClock.elapsedRealtime();
    private final int delayMillis;
    private final Runnable autoSlideRunnable;

    public AutoSlideHandler(ViewPager2 viewPager, int delayMillis) {
        this.viewPager = viewPager;
        this.delayMillis = delayMillis;

        autoSlideRunnable = new Runnable() {
            @Override
            public void run() {
                // Проверка, прошло ли больше 5 секунд с последнего взаимодействия
                if (SystemClock.elapsedRealtime() - lastUserInteractionTime >= 3500) {
                    // Переход к следующей странице
                    if (currentPage >= viewPager.getAdapter().getItemCount() - 1) {
                        currentPage = 0; // Переход к первой странице
                    } else {
                        currentPage++;
                    }
                    viewPager.setCurrentItem(currentPage, true);
                }
                // Повторный запуск через задержку
                handler.postDelayed(this, delayMillis);
            }
        };

        viewPager.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Обновляем текущую страницу и время последнего взаимодействия
                currentPage = position;
                lastUserInteractionTime = SystemClock.elapsedRealtime();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    lastUserInteractionTime = SystemClock.elapsedRealtime();
                }
            }
        });
    }

    public void startAutoSlide() {
        handler.postDelayed(autoSlideRunnable, delayMillis);
    }
}
