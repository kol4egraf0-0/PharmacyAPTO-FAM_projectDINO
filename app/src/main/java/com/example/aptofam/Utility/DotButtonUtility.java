package com.example.aptofam.Utility;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;

import androidx.annotation.ColorRes;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
public class DotButtonUtility {
    public static void addDotToButton(AppCompatButton button, Context context, @ColorRes int dotColor) {
        button.post(() -> {
            // Получаем фон кнопки
            Drawable background = button.getBackground();

            // Размер точки
            int dotSize = 32; // Увеличенный размер
            ShapeDrawable dot = new ShapeDrawable(new OvalShape());
            dot.setIntrinsicWidth(dotSize);
            dot.setIntrinsicHeight(dotSize);
            dot.getPaint().setColor(ContextCompat.getColor(context, dotColor));

            // Создаём слой, где точка будет последним элементом (наверху)
            LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{dot, background});

            // Позиционируем точку
            int offsetX = 20; // Смещение по горизонтали
            int offsetY = 8;  // Смещение по вертикали
            layerDrawable.setLayerInset(0, // Индекс точки в слоях
                    button.getWidth() - dotSize - offsetX, // Слева
                    offsetY, // Сверху
                    offsetX, // Справа
                    button.getHeight() - dotSize - offsetY); // Снизу

            // Устанавливаем слой с точкой и фоном
            button.setBackground(layerDrawable);

            Log.d("DotButton", "Dot added to button: " + button.getText() + ", Width: " + button.getWidth() + ", Height: " + button.getHeight());
        });
    }




    public static void removeDotFromButton(AppCompatButton button) {
        if (button.getBackground() instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) button.getBackground();
            if (layerDrawable.getNumberOfLayers() > 1) {
                button.setBackground(layerDrawable.getDrawable(0));
            }
        }
    }
}
