package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.aptofam.Adapter.DynamicFilterAdapter;
import com.example.aptofam.Adapter.StaticFilterAdapter;
import com.example.aptofam.R;
import com.example.aptofam.Utility.FilterHandler;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivityFilterItemBinding;

import java.util.HashSet;
import java.util.Set;

public class FilterItemActivity extends BaseActivity {

    private ActivityFilterItemBinding binding;
    private StaticFilterAdapter staticFilterAdapter;
    private DynamicFilterAdapter dynamicFilterAdapter;

    private FilterHandler filterHandler;
    private final Set<Integer> selectedApteks = new HashSet<>();
    private String prescriptionFilter = "";
    private String countryFilter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFilterItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        applySlideUpTransition();

        filterHandler = new FilterHandler(this);

        initStaticFilter();
        initDynamicFilter();
        setVariable();

        loadFilters();

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void setVariable() {
        binding.closeButton.setOnClickListener(v -> {
            finish();
            applySlideDownTransition();
        });

        binding.applyButton.setOnClickListener(v -> validateAndSaveFilters());

        binding.resetButton.setOnClickListener(v -> {
            resetFilters();
            ToastUtils.showToast(FilterItemActivity.this, "Все фильтры сброшены");
            finish();
            applySlideDownTransition();
        });
    }

    private void validateAndSaveFilters() {
        String minPrice = staticFilterAdapter.getMinPrice();
        String maxPrice = staticFilterAdapter.getMaxPrice();
        String minMg = staticFilterAdapter.getMinMg();
        String maxMg = staticFilterAdapter.getMaxMg();
        String minAge = staticFilterAdapter.getMinAge();
        String maxAge = staticFilterAdapter.getMaxAge();
        countryFilter = dynamicFilterAdapter.getCountryFilter();

        boolean isValid = true;

        // Проверка значений цены
        if (!minPrice.isEmpty() && !maxPrice.isEmpty() && Double.parseDouble(minPrice) > Double.parseDouble(maxPrice)) {
            ToastUtils.showToast(this, "Минимальная цена не может быть больше максимальной");
            isValid = false;
        }

        // Проверка значений миллиграммов
        if (!minMg.isEmpty() && !maxMg.isEmpty() && Integer.parseInt(minMg) > Integer.parseInt(maxMg)) {
            ToastUtils.showToast(this, "Минимальное значение мг не может быть больше максимального");
            isValid = false;
        }

        if (!minAge.isEmpty() && !maxAge.isEmpty() && Integer.parseInt(minAge) > Integer.parseInt(maxAge)) {
            ToastUtils.showToast(this, "Минимальный возраст не может быть больше максимального");
            isValid = false;
        }

        if (!isValid) return;

        // Сохранение фильтров
        filterHandler.saveFilterValues("minPrice", minPrice);
        filterHandler.saveFilterValues("maxPrice", maxPrice);
        filterHandler.saveFilterValues("minMg", minMg);
        filterHandler.saveFilterValues("maxMg", maxMg);
        filterHandler.saveFilterValues("minAge", minAge);
        filterHandler.saveFilterValues("maxAge", maxAge);
        filterHandler.saveFilterValues("countryFilter", countryFilter);

        // Передача результата
        Intent resultIntent = new Intent();
        resultIntent.putExtra("prescriptionFilter", prescriptionFilter);
        resultIntent.putExtra("countryFilter", countryFilter);
        setResult(RESULT_OK, resultIntent);

        finish();
        applySlideDownTransition();
    }

    private void loadFilters() {
        String minPrice = filterHandler.getFilterValue("minPrice");
        String maxPrice = filterHandler.getFilterValue("maxPrice");
        String minMg = filterHandler.getFilterValue("minMg");
        String maxMg = filterHandler.getFilterValue("maxMg");
        String minAge = filterHandler.getFilterValue("minAge");
        String maxAge = filterHandler.getFilterValue("maxAge");

        staticFilterAdapter.setFilterValues(
                minPrice != null ? minPrice : "",
                maxPrice != null ? maxPrice : "",
                minMg != null ? minMg : "",
                maxMg != null ? maxMg : "",
                minAge != null ? minAge : "",
                maxAge != null ? maxAge : ""
        );
    }

    private void resetFilters() {
        filterHandler.clearFilters();
        staticFilterAdapter.clearFilterValues();
        prescriptionFilter = "";
        countryFilter = "";
        dynamicFilterAdapter.resetFilters();
        setResult(RESULT_OK);
    }

    private void initStaticFilter() {
        staticFilterAdapter = new StaticFilterAdapter();
        binding.staticFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.staticFilterRecyclerView.setAdapter(staticFilterAdapter);
    }

    private void initDynamicFilter() {
        dynamicFilterAdapter = new DynamicFilterAdapter(
                (prescription, country) -> {
                    prescriptionFilter = prescription;
                    countryFilter = country;
                },
                filterHandler
        );
        binding.dynamicFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.dynamicFilterRecyclerView.setAdapter(dynamicFilterAdapter);
    }

    public void applySlideUpTransition() {
        overridePendingTransition(R.anim.slide_up, R.anim.no_animation);
    }

    public void applySlideDownTransition() {
        overridePendingTransition(R.anim.no_animation, R.anim.slide_down);
    }
}
