package com.example.aptofam.Utility;

import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aptofam.R;
//Могло быть мной использовано для устанвоки кнопки, потом нет
public class FavoriteButtonHandler {

    public void setupFavoriteButton(ImageView favBtn, AppCompatActivity activity, String itemId) {
        FavoriteManager favoriteManager = FavoriteManager.getInstance();
        updateButtonIcon(favBtn, favoriteManager.isFavorite(itemId));
        favBtn.setOnClickListener(v -> {
            if (favoriteManager.isFavorite(itemId)) {
                favoriteManager.removeFromFavorites(itemId);
                updateButtonIcon(favBtn, false);
                ToastUtils.showToast(activity, "Товар удалён из избранного");
            } else {
                favoriteManager.addToFavorites(itemId);
                updateButtonIcon(favBtn, true);
                ToastUtils.showToast(activity, "Товар добавлен в избранное");
            }
        });
    }
    private void updateButtonIcon(ImageView favBtn, boolean isFavorite) {
        if (isFavorite) {
            favBtn.setImageResource(R.drawable.btn_addred);
        } else {
            favBtn.setImageResource(R.drawable.btn_addnotred);
        }
    }


}
