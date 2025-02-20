package com.example.aptofam.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.R;
import com.example.aptofam.Utility.FilterHandler;

import java.util.Arrays;
import java.util.List;

public class DynamicFilterAdapter extends RecyclerView.Adapter<DynamicFilterAdapter.ViewHolder> {

    private String prescriptionFilter = ""; // "Да" или "Нет"
    private String countryFilter = ""; // Страна-производитель
    private final FilterCallback filterCallback;
    private final FilterHandler filterHandler;

    public interface FilterCallback {
        void onFilterUpdated(String prescriptionFilter, String countryFilter);
    }

    public DynamicFilterAdapter(FilterCallback filterCallback, FilterHandler filterHandler) {
        this.filterCallback = filterCallback;
        this.filterHandler = filterHandler; // Инициализируем filterHandler
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_item_prescription_filter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return 1; // Только один элемент — фильтр
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioGroup radioGroup;
        RadioButton yesButton, noButton;
        Spinner spinnerCountries;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioGroup = itemView.findViewById(R.id.radioGroupPrescription);
            yesButton = itemView.findViewById(R.id.radioYes);
            noButton = itemView.findViewById(R.id.radioNo);
            spinnerCountries = itemView.findViewById(R.id.spinnerCountries);
        }

        void bind() {
            // --- Настройка RadioGroup ---
            prescriptionFilter = filterHandler.getFilterValue("prescriptionFilter");
            if ("Да".equalsIgnoreCase(prescriptionFilter)) {
                yesButton.setChecked(true);
            } else if ("Нет".equalsIgnoreCase(prescriptionFilter)) {
                noButton.setChecked(true);
            } else {
                radioGroup.clearCheck();
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.radioYes) {
                    prescriptionFilter = "Да";
                } else if (checkedId == R.id.radioNo) {
                    prescriptionFilter = "Нет";
                } else {
                    prescriptionFilter = "";
                }

                filterHandler.saveFilterValues("prescriptionFilter", prescriptionFilter);
                filterCallback.onFilterUpdated(prescriptionFilter, countryFilter);
            });

            // --- Настройка Spinner ---
            List<String> countries = Arrays.asList("Все страны",
                    "Франция", "Германия", "Россия", "США", "Индия", "Китай", "Япония"
            );

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    itemView.getContext(),
                    android.R.layout.simple_spinner_item,
                    countries
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCountries.setAdapter(adapter);

            // Восстанавливаем выбранное значение
            countryFilter = filterHandler.getFilterValue("countryFilter");
            if (countryFilter.isEmpty()) {
                spinnerCountries.setSelection(0); // Сбрасываем Spinner на первый элемент (по умолчанию)
            } else {
                int selectedIndex = countries.indexOf(countryFilter);
                if (selectedIndex != -1) {
                    spinnerCountries.setSelection(selectedIndex);
                }
            }


            spinnerCountries.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    countryFilter = countries.get(position);
                    if (position == 0) {
                        countryFilter = ""; // Если выбрано "Выберите страну", сбрасываем значение
                    }
                    filterHandler.saveFilterValues("countryFilter", countryFilter);
                    filterCallback.onFilterUpdated(prescriptionFilter, countryFilter);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Ничего не выбрано
                }
            });
        }
    }
    public String getCountryFilter() {
        return countryFilter;
    }
    public void resetFilters() {
        prescriptionFilter = ""; // Сбрасываем фильтр по рецепту
        countryFilter = ""; // Сбрасываем фильтр по стране
        notifyDataSetChanged(); // Обновляем UI
    }
}
