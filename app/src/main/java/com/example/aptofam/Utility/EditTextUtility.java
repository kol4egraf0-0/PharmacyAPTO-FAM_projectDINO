package com.example.aptofam.Utility;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.aptofam.Activity.SearchActivity;
//Для текста в EditText при поиске
public class EditTextUtility {
    public static void setupEditTextListener(Activity activity, EditText editText) {
        editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    intent.putExtra("query", s.toString());
                    activity.startActivity(intent);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
