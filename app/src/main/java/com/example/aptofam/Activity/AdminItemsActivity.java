package com.example.aptofam.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivityAdminItemsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminItemsActivity extends BaseActivity {

    private ActivityAdminItemsBinding binding;
    private Spinner spinnerSection;
    private EditText editTextId, editTextTitle, editTextPrice, editTextPriceSale, editTextAgeUsage,
            editTextCategoryIds, editTextAptekaIds, editTextCharacteristics, editTextKeywords, editTextPicUrl;
    private Button buttonAddItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Инициализация элементов
        editTextId = findViewById(R.id.editTextId);
        spinnerSection = findViewById(R.id.spinnerSection);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextPriceSale = findViewById(R.id.editTextPriceSale);
        editTextAgeUsage = findViewById(R.id.editTextAgeUsage);
        editTextCategoryIds = findViewById(R.id.editTextCategoryIds);
        editTextAptekaIds = findViewById(R.id.editTextAptekaIds);
        editTextCharacteristics = findViewById(R.id.editTextCharacteristics);
        editTextKeywords = findViewById(R.id.editTextKeywords);
        editTextPicUrl = findViewById(R.id.editTextPicUrl);
        buttonAddItem = findViewById(R.id.buttonAddItem);

        // Настройка Spinner программно
        setupSpinner();

        // Обработка нажатия на кнопку "Добавить товар"
        buttonAddItem.setOnClickListener(v -> addItemToSection());

        // Настройка навигации и других элементов
        setVariable();
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);

        // Настройка окна
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void setupSpinner() {
        // Создаем список разделов
        List<String> sections = new ArrayList<>();
        sections.add("ItemsBestS");
        sections.add("ItemsSale");
        sections.add("Others");

        // Создаем адаптер для Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sections);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Назначаем адаптер Spinner
        spinnerSection.setAdapter(adapter);
    }

    private void addItemToSection() {
        // Получаем данные из полей
        String id = editTextId.getText().toString().trim(); // Ручной ID (например, item8)
        String title = editTextTitle.getText().toString().trim();
        String priceStr = editTextPrice.getText().toString().trim();
        String priceSaleStr = editTextPriceSale.getText().toString().trim();
        String ageUsageStr = editTextAgeUsage.getText().toString().trim();
        String categoryIdsStr = editTextCategoryIds.getText().toString().trim();
        String aptekaIdsStr = editTextAptekaIds.getText().toString().trim();
        String characteristics = editTextCharacteristics.getText().toString().trim();
        String keywordsStr = editTextKeywords.getText().toString().trim();
        String picUrlStr = editTextPicUrl.getText().toString().trim();
        String selectedSection = spinnerSection.getSelectedItem().toString();

        // Проверка на обязательные поля
        if (id.isEmpty() || title.isEmpty() || priceStr.isEmpty() || ageUsageStr.isEmpty() || categoryIdsStr.isEmpty() ||
                aptekaIdsStr.isEmpty() || characteristics.isEmpty() || keywordsStr.isEmpty()) {
            ToastUtils.showToast(this, "Заполните все обязательные поля");
            return;
        }

        // Преобразование данных
        double price = Double.parseDouble(priceStr);
        double priceSale = priceSaleStr.isEmpty() ? 0 : Double.parseDouble(priceSaleStr);
        int ageUsage = Integer.parseInt(ageUsageStr);
        List<Integer> categoryIds = parseStringToIntList(categoryIdsStr);
        List<Integer> aptekaIds = parseStringToIntList(aptekaIdsStr);
        List<String> keywords = parseStringToStringList(keywordsStr);
        List<String> picUrl = picUrlStr.isEmpty() ? new ArrayList<>() : parseStringToStringList(picUrlStr);

        // Преобразуем характеристики в JSON-объект
        Map<String, String> characteristicsMap = parseCharacteristics(characteristics);

        // Создаем объект товара
        Map<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("title", title);
        item.put("price", price);
        item.put("priceSaleStrStrike", priceSaleStr.isEmpty() ? "" : String.valueOf(priceSale));
        item.put("ageUsage", ageUsage);
        item.put("categoryIds", categoryIds);
        item.put("aptekaIds", aptekaIds);
        item.put("characteristics", characteristicsMap);
        item.put("keywords", keywords);
        item.put("picUrl", picUrl);

        // Добавляем товар в выбранный раздел Firebase
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(selectedSection);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long nextKey = snapshot.getChildrenCount();
                databaseRef.child(String.valueOf(nextKey)).setValue(item)
                        .addOnSuccessListener(aVoid -> {
                            ToastUtils.showToast(AdminItemsActivity.this, "Товар добавлен!");
                            clearFields();
                        })
                        .addOnFailureListener(e -> ToastUtils.showToast(AdminItemsActivity.this, "Ошибка: " + e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ToastUtils.showToast(AdminItemsActivity.this, "Ошибка: " + error.getMessage());
            }
        });
    }

    private List<Integer> parseStringToIntList(String input) {
        List<Integer> list = new ArrayList<>();
        String[] parts = input.split(",");
        for (String part : parts) {
            try {
                list.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private List<String> parseStringToStringList(String input) {
        List<String> list = new ArrayList<>();
        String[] parts = input.split(",");
        for (String part : parts) {
            list.add(part.trim());
        }
        return list;
    }

    private Map<String, String> parseCharacteristics(String input) {
        Map<String, String> characteristics = new HashMap<>();
        String[] pairs = input.split("\n");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replace("\"", "");
                characteristics.put(key, value);
            }
        }
        return characteristics;
    }

    private void clearFields() {
        editTextId.setText("");
        editTextTitle.setText("");
        editTextPrice.setText("");
        editTextPriceSale.setText("");
        editTextAgeUsage.setText("");
        editTextCategoryIds.setText("");
        editTextAptekaIds.setText("");
        editTextCharacteristics.setText("");
        editTextKeywords.setText("");
        editTextPicUrl.setText("");
    }
}
