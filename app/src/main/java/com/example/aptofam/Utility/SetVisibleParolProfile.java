package com.example.aptofam.Utility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.example.aptofam.R;

public class SetVisibleParolProfile extends AppCompatEditText {

    private Drawable eyeVisibleIcon; // Иконка "глаз открыт"
    private Drawable eyeHiddenIcon;  // Иконка "глаз закрыт"
    private boolean isPasswordVisible = false; // Состояние видимости пароля

    public SetVisibleParolProfile(@NonNull Context context) {
        super(context);
        init();
    }

    public SetVisibleParolProfile(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetVisibleParolProfile(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        // Загружаем иконки из ресурсов с использованием ResourcesCompat
        eyeVisibleIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.eye_view, null); // Иконка "глаз открыт"
        eyeHiddenIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.eye_hide, null);  // Иконка "глаз закрыт"

        // Устанавливаем начальную иконку (глаз закрыт)
        setCompoundDrawablesWithIntrinsicBounds(null, null, eyeHiddenIcon, null);

        // Обработка нажатия на иконку глаза
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Проверяем, было ли нажатие на иконку справа
                if (event.getRawX() >= (getRight() - getCompoundDrawables()[2].getBounds().width())) {
                    // Переключаем видимость пароля
                    togglePasswordVisibility();
                    // Вызываем performClick для поддержки accessibility
                    performClick();
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public boolean performClick() {
        // Вызываем родительский метод для поддержки accessibility
        super.performClick();
        // Здесь можно добавить логику, если нужно
        return true;
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Скрываем пароль
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(null, null, eyeHiddenIcon, null);
        } else {
            // Показываем пароль
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(null, null, eyeVisibleIcon, null);
        }
        // Перемещаем курсор в конец текста
        setSelection(getText().length());
        // Меняем состояние
        isPasswordVisible = !isPasswordVisible;
    }
}
