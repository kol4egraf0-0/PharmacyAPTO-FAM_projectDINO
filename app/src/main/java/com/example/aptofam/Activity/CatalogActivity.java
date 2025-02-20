package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.aptofam.Adapter.CategoryAdapter;
import com.example.aptofam.Model.CategoryModel;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.EditTextUtility;
import com.example.aptofam.Utility.NavigationRecyclerUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.databinding.ActivityCatalogBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CatalogActivity extends BaseActivity {
    private ActivityCatalogBinding binding;
    private CategoryAdapter categoryAdapter;
    private ArrayList<CategoryModel> listCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCatalogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        listCategory = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, listCategory);

        binding.recyclerCategory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerCategory.setAdapter(categoryAdapter);

        EditText editText = findViewById(R.id.editTextText);
        EditTextUtility.setupEditTextListener(this, editText);

        NavigationRecyclerUpDown.setupRecyclerViewScrollListener(binding.recyclerCategory, binding.bottomNav);
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());

        loadCategories();
    }
    private void loadCategories() {
        // Подключение к Firebase Database
        FirebaseDatabase.getInstance().getReference("Categories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        listCategory.clear(); // Очистка списка перед обновлением
                        for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                            CategoryModel category = categorySnapshot.getValue(CategoryModel.class);
                            if (category != null) {
                                listCategory.add(category);
                            }
                        }
                        categoryAdapter.notifyDataSetChanged(); // Уведомление адаптера об изменениях
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        ;
                    }
                });
    }

    public void onCategorySelected(CategoryModel category) {
        // Список узлов с товарами
        String[] itemNodes = {"ItemsBestS", "ItemsSale", "Others"};
        ArrayList<ItemsModel> itemsList = new ArrayList<>();

        for (String node : itemNodes) {
            FirebaseDatabase.getInstance().getReference(node)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                ItemsModel item = itemSnapshot.getValue(ItemsModel.class);
                                if (item != null && item.getCategoryIds() != null) {
                                    // Проверяем, содержит ли товар нужную категорию
                                    if (item.getCategoryIds().contains(category.getCategoryId())) {
                                        itemsList.add(item);
                                    }
                                }
                            }
                            // Если это последний узел, передаем данные в intent
                            if (node.equals(itemNodes[itemNodes.length - 1])) {
                                Intent intent = new Intent(CatalogActivity.this, ItemFromCatalogActivity.class);
                                intent.putExtra("items", itemsList);
                                intent.putExtra("categoryName", category.getCategoryName());
                                startActivity(intent);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Обработка ошибок
                        }
                    });
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        EditText editText = findViewById(R.id.editTextText);
        editText.setText("");
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
    }
}
