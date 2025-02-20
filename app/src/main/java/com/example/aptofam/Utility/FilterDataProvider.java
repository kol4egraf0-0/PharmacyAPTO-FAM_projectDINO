package com.example.aptofam.Utility;
import android.util.Log;

import com.example.aptofam.Model.ItemsModel;
import java.util.ArrayList;
import java.util.List;
public class FilterDataProvider {

    public static List<ItemsModel> filterItems(List<ItemsModel> items, String minPrice, String maxPrice, String minMg, String maxMg, String minAge, String maxAge, String prescriptionFilter, String countryFilter) {
        List<ItemsModel> filteredItems = new ArrayList<>();

        for (ItemsModel item : items) {
            boolean matches = true;

            // Проверяем цену
            if (!minPrice.isEmpty()) {
                matches &= item.getPrice() >= Double.parseDouble(minPrice);
            }
            if (!maxPrice.isEmpty()) {
                matches &= item.getPrice() <= Double.parseDouble(maxPrice);
            }

            // Проверяем миллиграммы
            if (!minMg.isEmpty() && item.getMilligrams() != null) {
                matches &= item.getMilligrams() >= Integer.parseInt(minMg);
            }
            if (!maxMg.isEmpty() && item.getMilligrams() != null) {
                matches &= item.getMilligrams() <= Integer.parseInt(maxMg);
            }

            // Проверяем возраст
            if (!minAge.isEmpty() && item.getAgeUsage() != null) {
                matches &= item.getAgeUsage() >= Integer.parseInt(minAge);
            }
            if (!maxAge.isEmpty() && item.getAgeUsage() != null) {
                matches &= item.getAgeUsage() <= Integer.parseInt(maxAge);
            }

            // Проверяем рецепт
            if (!prescriptionFilter.isEmpty()) {
                String prescriptionRequired = item.getCharacteristics() != null ? item.getCharacteristics().get("Нужен рецепт") : "";
                matches &= prescriptionRequired.equalsIgnoreCase(prescriptionFilter);
            }

            // Проверяем страну производства
            if (!countryFilter.isEmpty()) {
                String itemCountry = item.getCharacteristics() != null ? item.getCharacteristics().get("Страна производитель") : "";
                matches &= countryFilter.equalsIgnoreCase(itemCountry);
            }

            // Если все условия совпадают, добавляем в результаты
            if (matches) {
                filteredItems.add(item);
            }
        }

        // Если ничего не найдено, возвращаем пустой список
        return filteredItems;
    }
}

