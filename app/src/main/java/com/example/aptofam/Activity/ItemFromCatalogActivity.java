package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.aptofam.Adapter.ItemFromCatalogAdapter;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.BottomSheetHelper;
import com.example.aptofam.Utility.EditTextUtility;
import com.example.aptofam.Utility.FilterDataProvider;
import com.example.aptofam.Utility.FilterHandler;
import com.example.aptofam.Utility.NavigationRecyclerUpDown;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.databinding.ActivityItemFromCatalogBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ItemFromCatalogActivity extends BaseActivity {
    private ActivityItemFromCatalogBinding binding;
    private ArrayList<ItemsModel> itemsList;
    private String categoryName;
    private ItemFromCatalogAdapter itemFromCatalogAdapter;
    private boolean isFirstLaunch = true;
    private String prescriptionFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemFromCatalogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemsList = (ArrayList<ItemsModel>) getIntent().getSerializableExtra("items");
        categoryName = getIntent().getStringExtra("categoryName");

        Button sortButton = findViewById(R.id.sortBtn);
        Button filterButton = findViewById(R.id.filterBtn);

        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());

        EditText editText = findViewById(R.id.editTextText);
        EditTextUtility.setupEditTextListener(this, editText);

        FilterHandler filterHandler = new FilterHandler(this);
        filterHandler.clearFilters();

        setupRecyclerView();

        setVariable();

        BottomSheetHelper helper = new BottomSheetHelper(this, sortButton, this::sortItems);
        sortButton.setOnClickListener(v -> helper.showBottomSheetDialog());
        filterButton.setOnClickListener(v -> {
            Intent intent = new Intent(ItemFromCatalogActivity.this, FilterItemActivity.class);
            startActivity(intent);
        });
    }

    private void sortItems(String sortOption) {
        List<ItemsModel> currentItems = new ArrayList<>(itemFromCatalogAdapter.getItems());

        if (sortOption == null || sortOption.isEmpty()) {
            // Сортировка сброшена — возвращаем исходный порядок
            Collections.sort(currentItems, Comparator.comparing(ItemsModel::getId));
        } else {
            switch (sortOption) {
                case "ALPHABET_ASC":
                    Collections.sort(currentItems, Comparator.comparing(ItemsModel::getTitle));
                    break;
                case "ALPHABET_DESC":
                    Collections.sort(currentItems, (a, b) -> b.getTitle().compareTo(a.getTitle()));
                    break;
                case "PRICE_ASC":
                    Collections.sort(currentItems, Comparator.comparingDouble(ItemsModel::getPrice));
                    break;
                case "PRICE_DESC":
                    Collections.sort(currentItems, (a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                    break;
            }
        }

        itemFromCatalogAdapter.updateData(currentItems);
    }

    private void setupRecyclerView() {
        if (itemsList == null || itemsList.isEmpty()) {
            Log.e("DebugInit", "Cannot initialize RecyclerView with an empty or null itemsList.");
            return;
        }
        ManagmentFavorite managmentFavorite = new ManagmentFavorite(this);
        ManagmentCart managmentCart = new ManagmentCart(this);
        int numberOrder = 1;

        itemFromCatalogAdapter = new ItemFromCatalogAdapter(itemsList, managmentFavorite, managmentCart, numberOrder);
        binding.recyclerItemCategory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerItemCategory.setAdapter(itemFromCatalogAdapter);
        Log.d("DebugInit", "RecyclerView setup completed with itemsList size: " + itemsList.size());
    }

    private void setVariable() {
        binding.textCatalog.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }
    @Override
    protected void onResume() {
        super.onResume();
        EditText editText = findViewById(R.id.editTextText);
        editText.setText("");

        // Получаем сохраненные значения фильтров
        FilterHandler filterHandler = new FilterHandler(this);
        String savedPrescriptionFilter = filterHandler.getFilterValue("prescriptionFilter");
        String savedCountryFilter = filterHandler.getFilterValue("countryFilter");

        if (savedPrescriptionFilter != null && !savedPrescriptionFilter.isEmpty()) {
            prescriptionFilter = savedPrescriptionFilter;
        }

        // Применяем фильтр для отображения
        if (binding.recyclerItemCategory.getAdapter() != null) {
            binding.recyclerItemCategory.getAdapter().notifyDataSetChanged();
        }

        boolean filtersCleared = filterHandler.getFilterValue("minPrice").isEmpty()
                && filterHandler.getFilterValue("maxPrice").isEmpty()
                && filterHandler.getFilterValue("minMg").isEmpty()
                && filterHandler.getFilterValue("maxMg").isEmpty()
                && filterHandler.getFilterValue("minAge").isEmpty()
                && filterHandler.getFilterValue("maxAge").isEmpty()
                && filterHandler.getFilterValue("countryFilter").isEmpty();

        // Применяем фильтрацию
        List<ItemsModel> itemsToDisplay = filtersCleared
                ? itemsList
                : FilterDataProvider.filterItems(itemsList,
                filterHandler.getFilterValue("minPrice"),
                filterHandler.getFilterValue("maxPrice"),
                filterHandler.getFilterValue("minMg"),
                filterHandler.getFilterValue("maxMg"),
                filterHandler.getFilterValue("minAge"),
                filterHandler.getFilterValue("maxAge"),
                prescriptionFilter,savedCountryFilter);

        Log.d("DebugFilter", "Items to display size: " + itemsToDisplay.size());

        if (itemsToDisplay.isEmpty()) {
            Log.d("DebugFilter", "No items match the filter criteria.");
            binding.recyclerItemCategory.setVisibility(View.GONE);
            binding.emptyStateText.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerItemCategory.setVisibility(View.VISIBLE);
            binding.emptyStateText.setVisibility(View.GONE);
            itemFromCatalogAdapter.updateData(itemsToDisplay);
        }

        isFirstLaunch = false;
    }

}