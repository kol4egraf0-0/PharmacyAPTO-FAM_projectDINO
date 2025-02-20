package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aptofam.Adapter.ItemsFromSliderAdapter;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.CategoryModel;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationRecyclerUpDown;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.databinding.ActivityItemsFromSliderBinding;
import com.example.aptofam.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ItemsFromSliderActivity extends BaseActivity {
private ActivityItemsFromSliderBinding binding;
    private ImageView sliderImageView;
    private TextView textDescriptionSlider;
    private List<ItemsModel> itemsList;
    private ItemsFromSliderAdapter adapter;
    private RecyclerView recyclerItemsFromSlider;
    private Map<Integer, Integer> categoryOrderMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemsFromSliderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        Intent intent = getIntent();
        int sliderId = intent.getIntExtra("sliderId", -1);
        String sliderUrl = intent.getStringExtra("sliderUrl");
        String description = intent.getStringExtra("sliderDescription");

        // Устанавливаем изображение
        Glide.with(this).load(sliderUrl).into(sliderImageView);

        // Устанавливаем описание
        textDescriptionSlider.setText(description);

        setupRecyclerView();

        loadItemsByCategory(sliderId);

        setVariable();

        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.ScrollView2, binding.bottomNav);
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }

    private void initViews() {
        sliderImageView = findViewById(R.id.sliderImageView);
        textDescriptionSlider = findViewById(R.id.textDescriptionSlider);
        recyclerItemsFromSlider = findViewById(R.id.recyclerItemsFromSlider);
    }
    private void setupRecyclerView() {
        ManagmentFavorite managmentFavorite = new ManagmentFavorite(this);
        ManagmentCart managmentCart = new ManagmentCart(this);
        int numberOrder = 1;
        recyclerItemsFromSlider.setLayoutManager(new LinearLayoutManager(this));
        itemsList = new ArrayList<>();
        adapter = new ItemsFromSliderAdapter((ArrayList<ItemsModel>) itemsList, managmentFavorite, managmentCart, numberOrder);
        recyclerItemsFromSlider.setAdapter(adapter);
    }
    private void loadItemsByCategory(int sliderId) {
        String[] itemNodes = {"ItemsBestS", "ItemsSale", "Others"};
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Categories");

        // Шаг 1: Загружаем категории и сохраняем их порядок
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Integer> orderedCategoryIds = new ArrayList<>();

                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    CategoryModel category = categorySnapshot.getValue(CategoryModel.class);

                    if (category != null) {
                        int categoryId = category.getCategoryId();
                        int order = category.getSliderOrder();

                        if (order != 0) { // Проверяем, что порядок задан
                            while (orderedCategoryIds.size() <= order - 1) {
                                orderedCategoryIds.add(null);
                            }
                            orderedCategoryIds.set(order - 1, categoryId);
                        } else {
                            orderedCategoryIds.add(categoryId);
                        }
                    }
                }

                orderedCategoryIds.removeIf(Objects::isNull);

                // Найти категории, связанные с текущим sliderId
                List<Integer> sliderCategories = new ArrayList<>();
                if (sliderId - 1 < orderedCategoryIds.size()) {
                    sliderCategories.add(orderedCategoryIds.get(sliderId - 1));
                }

                // Шаг 2: Загружаем товары
                itemsList.clear();

                for (String node : itemNodes) {
                    databaseReference.child(node).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                ItemsModel item = itemSnapshot.getValue(ItemsModel.class);

                                if (item != null && item.getCategoryIds() != null) {

                                    // Проверяем пересечение categoryIds товара и sliderCategories
                                    boolean matchesSlider = false;
                                    for (Integer categoryId : item.getCategoryIds()) {
                                        if (sliderCategories.contains(categoryId)) {
                                            matchesSlider = true;
                                            break;
                                        }
                                    }
                                    if (matchesSlider) {
                                        itemsList.add(item);
                                    } else {
                                        Log.d("ItemDebug", "Item skipped: " + item.getTitle());
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}