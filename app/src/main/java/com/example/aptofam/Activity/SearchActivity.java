package com.example.aptofam.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.aptofam.Adapter.SearchAdapter;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.BottomSheetHelper;
import com.example.aptofam.Utility.DotButtonUtility;
import com.example.aptofam.Utility.FilterDataProvider;
import com.example.aptofam.Utility.FilterHandler;
import com.example.aptofam.Utility.NavigationRecyclerUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivitySearchBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class SearchActivity extends BaseActivity {
    private ActivitySearchBinding binding;
    private ArrayList<ItemsModel> itemsSearchList;
    private SearchAdapter searchAdapter;
    private ArrayList<ItemsModel> originalItemsList = new ArrayList<>();
    private boolean isFirstLaunch = true;
    private String prescriptionFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button sortButton = findViewById(R.id.sortBtn);
        Button filerButton = findViewById(R.id.filterBtn);

        itemsSearchList = new ArrayList<>();

        String query = getIntent().getStringExtra("query");
        if (query != null) {
            binding.editTextSearch.setText(query);
            searchItems(query);
        }

        setupRecyclerView();

        setupSearchField();

        setVariable();

        FilterHandler filterHandler = new FilterHandler(this);
        filterHandler.clearFilters();

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationRecyclerUpDown.setupRecyclerViewScrollListener(binding.recyclerSearchItems, binding.bottomNav);

        BottomSheetHelper helper = new BottomSheetHelper(this, sortButton, this::sortItems);
        sortButton.setOnClickListener(v -> helper.showBottomSheetDialog());
        filerButton.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, FilterItemActivity.class);
            startActivity(intent);
        });

    }

    private void sortItems(String sortOption) {
        if (sortOption == null || sortOption.isEmpty()) {
            // Сортировка сброшена — возвращаем исходный порядок
            Collections.sort(itemsSearchList, Comparator.comparing(ItemsModel::getId)); // Например, по ID
            searchAdapter.notifyDataSetChanged();
            return;
        }
        switch (sortOption) {
            case "ALPHABET_ASC":
                // Сортировка по алфавиту (А-Я)
                Collections.sort(itemsSearchList, Comparator.comparing(ItemsModel::getTitle));
                break;
            case "ALPHABET_DESC":
                // Сортировка по алфавиту (Я-А)
                Collections.sort(itemsSearchList, (a, b) -> b.getTitle().compareTo(a.getTitle()));
                break;
            case "PRICE_ASC":
                // Сортировка по возрастанию цены
                Collections.sort(itemsSearchList, Comparator.comparingDouble(ItemsModel::getPrice));
                break;
            case "PRICE_DESC":
                // Сортировка по убыванию цены
                Collections.sort(itemsSearchList, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
        }

        // Обновление RecyclerView
        searchAdapter.notifyDataSetChanged();
    }

    private void setupSearchField() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchItems(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    private void searchItems(String query) {
        if (query.isEmpty()) {
            itemsSearchList.clear();
            itemsSearchList.addAll(originalItemsList); // Показываем все элементы
            searchAdapter.notifyDataSetChanged();

            binding.recyclerSearchItems.setVisibility(View.GONE);
            binding.emptyStateText.setVisibility(View.VISIBLE);
            return;
        }

        binding.recyclerSearchItems.setVisibility(View.VISIBLE);
        binding.emptyStateText.setVisibility(View.GONE);

        itemsSearchList.clear(); // Очистить временный список
        String normalizedQuery = normalizeInput(query); // Нормализуем запрос
        String[] itemNodes = {"ItemsBestS", "ItemsSale", "Others"}; // Узлы Firebase

        // Отслеживаем, когда все запросы завершатся
        final int[] pendingRequests = {itemNodes.length};

        for (String node : itemNodes) {
            DatabaseReference nodeReference = FirebaseDatabase.getInstance().getReference(node);
            nodeReference.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        ItemsModel item = snapshot.getValue(ItemsModel.class);
                        if (item != null) {
                            if (!containsItem(originalItemsList, item)) { // Добавляем в оригинальный список
                                originalItemsList.add(item);
                            }

                            if (matchesQuery(normalizedQuery, item)) { // Добавляем в фильтрованный список
                                itemsSearchList.add(item);
                            }
                        }
                    }
                }

                // Уменьшаем счётчик ожидающих запросов
                pendingRequests[0]--;

                // Обновляем адаптер, только когда все запросы завершены
                if (pendingRequests[0] == 0) {
                    searchAdapter.notifyDataSetChanged();
                }
            });
        }
    }
    // Утилитарный метод для проверки, содержит ли список элемент
    private boolean containsItem(List<ItemsModel> list, ItemsModel item) {
        for (ItemsModel existingItem : list) {
            if (existingItem.getId().equals(item.getId())) {
                return true;
            }
        }
        return false;
    }


    // Метод для проверки соответствия запроса названию или ключевым словам
    private boolean matchesQuery(String query, ItemsModel item) {
        String normalizedTitle = normalizeInput(item.getTitle());
        if (normalizedTitle.startsWith(query)) {
            return true;
        }
        if (item.getKeywords() != null) {
            for (String keyword : item.getKeywords()) {
                String normalizedKeyword = normalizeInput(keyword);
                if (normalizedKeyword.startsWith(query)) {
                    return true;
                }
            }
        }
        return false;
    }
    // Нормализация ввода
    private String normalizeInput(String input) {
        return input.trim().toLowerCase().replaceAll("[-\\s]+", " ");
    }

    private void setupRecyclerView() {
        ManagmentFavorite managmentFavorite = new ManagmentFavorite(this);
        ManagmentCart managmentCart = new ManagmentCart(this);
        int numberOrder = 1;
        searchAdapter = new SearchAdapter(itemsSearchList, managmentFavorite, managmentCart, numberOrder);
        binding.recyclerSearchItems.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerSearchItems.setAdapter(searchAdapter);
    }
    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }
    private List<ItemsModel> filterByQuery(String query, List<ItemsModel> items) {
        if (query.isEmpty()) {
            return items; // Если запрос пустой, возвращаем все элементы
        }

        List<ItemsModel> filteredList = new ArrayList<>();
        for (ItemsModel item : items) {
            String normalizedTitle = normalizeInput(item.getTitle());
            if (normalizedTitle.startsWith(query)) { // Проверяем соответствие заголовка запросу
                filteredList.add(item);
            }
        }
        return filteredList;
    }
    @Override
    protected void onResume() {
        super.onResume();

        FilterHandler filterHandler = new FilterHandler(this);
        String savedPrescriptionFilter = filterHandler.getFilterValue("prescriptionFilter");
        String savedCountryFilter = filterHandler.getFilterValue("countryFilter");

        if (savedPrescriptionFilter != null && !savedPrescriptionFilter.isEmpty()) {
            prescriptionFilter = savedPrescriptionFilter;
        }

        if (binding.recyclerSearchItems.getAdapter() != null) {
            binding.recyclerSearchItems.getAdapter().notifyDataSetChanged();
        }

        boolean filtersCleared = filterHandler.getFilterValue("minPrice").isEmpty()
                && filterHandler.getFilterValue("maxPrice").isEmpty()
                && filterHandler.getFilterValue("minMg").isEmpty()
                && filterHandler.getFilterValue("maxMg").isEmpty()
                && filterHandler.getFilterValue("minAge").isEmpty()
                && filterHandler.getFilterValue("maxAge").isEmpty()
                && filterHandler.getFilterValue("countryFilter").isEmpty();

        String query = binding.editTextSearch.getText().toString().trim().toLowerCase();

        List<ItemsModel> searchResults = filterByQuery(query, originalItemsList);

        List<ItemsModel> itemsToDisplay = filtersCleared
                ? searchResults
                : FilterDataProvider.filterItems(searchResults,
                filterHandler.getFilterValue("minPrice"),
                filterHandler.getFilterValue("maxPrice"),
                filterHandler.getFilterValue("minMg"),
                filterHandler.getFilterValue("maxMg"),
                filterHandler.getFilterValue("minAge"),
                filterHandler.getFilterValue("maxAge"),
                prescriptionFilter,savedCountryFilter);

        if (!isFirstLaunch && !filtersCleared && itemsToDisplay.isEmpty()) {
            binding.recyclerSearchItems.setVisibility(View.GONE);
            binding.emptyStateText.setVisibility(View.VISIBLE);
            binding.emptyStateText.setText("По вашему фильтру ничего не найдено");
        } else {
            binding.recyclerSearchItems.setVisibility(View.VISIBLE);
            binding.emptyStateText.setVisibility(View.GONE);
            searchAdapter.updateData(itemsToDisplay);
        }
        isFirstLaunch = false;
    }


}