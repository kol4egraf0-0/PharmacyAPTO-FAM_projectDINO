package com.example.aptofam.Utility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.example.aptofam.R;

public class SetVisibleParol extends AppCompatEditText {

    private Drawable eyeVisibleIcon; // Иконка "глаз открыт"
    private Drawable eyeHiddenIcon;  // Иконка "глаз закрыт"
    private boolean isPasswordVisible = false; // Состояние видимости пароля
    private Drawable startIcon;

    public SetVisibleParol(Context context) {
        super(context);
        init();
    }

    public SetVisibleParol(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SetVisibleParol(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        startIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.password, null);
        eyeVisibleIcon =  ResourcesCompat.getDrawable(getResources(), R.drawable.eye_view, null); // Иконка "глаз открыт"
        eyeHiddenIcon =  ResourcesCompat.getDrawable(getResources(), R.drawable.eye_hide, null);  // Иконка "глаз закрыт"

        // Устанавливаем начальную иконку (глаз закрыт)
        setCompoundDrawablesWithIntrinsicBounds(startIcon, null, eyeHiddenIcon, null);

        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (getRight() - getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    performClick();
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Скрываем пароль
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(startIcon, null, eyeHiddenIcon, null);
        } else {
            // Показываем пароль
            setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            setCompoundDrawablesWithIntrinsicBounds(startIcon, null, eyeVisibleIcon, null);
        }
        setSelection(getText().length());
        isPasswordVisible = !isPasswordVisible;
    }
}