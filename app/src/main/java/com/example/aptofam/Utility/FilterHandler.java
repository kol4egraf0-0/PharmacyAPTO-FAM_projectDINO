package com.example.aptofam.Utility;
import android.content.Context;
import android.content.SharedPreferences;

//Сохранение фильтров
public class FilterHandler {
    private final SharedPreferences sharedPreferences;
    public FilterHandler(Context context) {
        sharedPreferences = context.getSharedPreferences("FilterPrefs", Context.MODE_PRIVATE);
    }

    public void saveFilterValues(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getFilterValue(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void clearFilters() {
        sharedPreferences.edit().clear().apply();
    }

}
