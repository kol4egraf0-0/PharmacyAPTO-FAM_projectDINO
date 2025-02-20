package com.example.aptofam.Utility;
import android.content.Context;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.example.aptofam.Helper.OnSortOptionSelectedListener;
import com.example.aptofam.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
public class BottomSheetHelper {
    private Context context;
    private Button showBottomSheetButton;
    private OnSortOptionSelectedListener listener;

    public BottomSheetHelper(Context context, Button showBottomSheetButton, OnSortOptionSelectedListener listener) {
        this.context = context;
        this.showBottomSheetButton = showBottomSheetButton;
        this.listener = listener;
    }
    public void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.CustomBottomSheetDialog);
        bottomSheetDialog.setContentView(R.layout.item_sort_choice);
        bottomSheetDialog.show();

        CheckBox checkAlphabetAya = bottomSheetDialog.findViewById(R.id.check_alphabet_aya);
        CheckBox checkAlphabetYaa = bottomSheetDialog.findViewById(R.id.check_alphabet_yaa);
        CheckBox checkPriceUp = bottomSheetDialog.findViewById(R.id.check_price_up);
        CheckBox checkPriceDown = bottomSheetDialog.findViewById(R.id.check_price_down);

        CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            updateButtonText(bottomSheetDialog);
            // если выбран хотя бы один чекбокс -  передать событие и закрыть диалог
            if (isAnyCheckboxChecked(bottomSheetDialog)) {
                String selectedOption = getSelectedSortOption(bottomSheetDialog);
                if (selectedOption != null) {
                    this.listener.onSortOptionSelected(selectedOption);
                }
                bottomSheetDialog.dismiss();
            }
        };
        // Привязка слушателей
        if (checkAlphabetAya != null) checkAlphabetAya.setOnCheckedChangeListener(listener);
        if (checkAlphabetYaa != null) checkAlphabetYaa.setOnCheckedChangeListener(listener);
        if (checkPriceUp != null) checkPriceUp.setOnCheckedChangeListener(listener);
        if (checkPriceDown != null) checkPriceDown.setOnCheckedChangeListener(listener);

        // Обработчик кнопки закрытия
        ImageButton closeButton = bottomSheetDialog.findViewById(R.id.close_button);
        if (closeButton != null) {
            closeButton.setOnClickListener(view -> {
                resetSorting(bottomSheetDialog); // Сбросить сортировку
                bottomSheetDialog.dismiss();
            });
        }
    }

    private void resetSorting(BottomSheetDialog bottomSheetDialog) {
        CheckBox checkAlphabetAya = bottomSheetDialog.findViewById(R.id.check_alphabet_aya);
        CheckBox checkAlphabetYaa = bottomSheetDialog.findViewById(R.id.check_alphabet_yaa);
        CheckBox checkPriceUp = bottomSheetDialog.findViewById(R.id.check_price_up);
        CheckBox checkPriceDown = bottomSheetDialog.findViewById(R.id.check_price_down);

        if (checkAlphabetAya != null) checkAlphabetAya.setChecked(false);
        if (checkAlphabetYaa != null) checkAlphabetYaa.setChecked(false);
        if (checkPriceUp != null) checkPriceUp.setChecked(false);
        if (checkPriceDown != null) checkPriceDown.setChecked(false);

        // Сброс текста на кнопке
        showBottomSheetButton.setText("Сортировка");

        // Уведомить слушателя о сбросе
        this.listener.onSortOptionSelected(null); // Пустая строка указывает на сброс
    }

    private void updateButtonText(BottomSheetDialog bottomSheetDialog) {
        CheckBox checkAlphabetAya = bottomSheetDialog.findViewById(R.id.check_alphabet_aya);
        CheckBox checkAlphabetYaa = bottomSheetDialog.findViewById(R.id.check_alphabet_yaa);
        CheckBox checkPriceUp = bottomSheetDialog.findViewById(R.id.check_price_up);
        CheckBox checkPriceDown = bottomSheetDialog.findViewById(R.id.check_price_down);

        String buttonText = "";

        if (checkAlphabetAya != null && checkAlphabetAya.isChecked()) {
            buttonText += "По алфавиту (А-Я) ";
        }
        if (checkAlphabetYaa != null && checkAlphabetYaa.isChecked()) {
            buttonText += "По алфавиту (Я-А) ";
        }
        if (checkPriceUp != null && checkPriceUp.isChecked()) {
            buttonText += "По возрастанию цены ";
        }
        if (checkPriceDown != null && checkPriceDown.isChecked()) {
            buttonText += "По убыванию цены ";
        }
            showBottomSheetButton.setText(buttonText.trim());
    }

    private boolean isAnyCheckboxChecked(BottomSheetDialog bottomSheetDialog) {
        CheckBox checkAlphabetAya = bottomSheetDialog.findViewById(R.id.check_alphabet_aya);
        CheckBox checkAlphabetYaa = bottomSheetDialog.findViewById(R.id.check_alphabet_yaa);
        CheckBox checkPriceUp = bottomSheetDialog.findViewById(R.id.check_price_up);
        CheckBox checkPriceDown = bottomSheetDialog.findViewById(R.id.check_price_down);

        return (checkAlphabetAya != null && checkAlphabetAya.isChecked()) ||
                (checkAlphabetYaa != null && checkAlphabetYaa.isChecked()) ||
                (checkPriceUp != null && checkPriceUp.isChecked()) ||
                (checkPriceDown != null && checkPriceDown.isChecked());
    }

    private String getSelectedSortOption(BottomSheetDialog bottomSheetDialog) {
        CheckBox checkAlphabetAya = bottomSheetDialog.findViewById(R.id.check_alphabet_aya);
        CheckBox checkAlphabetYaa = bottomSheetDialog.findViewById(R.id.check_alphabet_yaa);
        CheckBox checkPriceUp = bottomSheetDialog.findViewById(R.id.check_price_up);
        CheckBox checkPriceDown = bottomSheetDialog.findViewById(R.id.check_price_down);

        if (checkAlphabetAya != null && checkAlphabetAya.isChecked()) {
            return "ALPHABET_ASC";
        }
        if (checkAlphabetYaa != null && checkAlphabetYaa.isChecked()) {
            return "ALPHABET_DESC";
        }
        if (checkPriceUp != null && checkPriceUp.isChecked()) {
            return "PRICE_ASC";
        }
        if (checkPriceDown != null && checkPriceDown.isChecked()) {
            return "PRICE_DESC";
        }
        return null;
    }
}
