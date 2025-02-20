package com.example.aptofam.Adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.databinding.ViewholderFilterStaticBinding;

public class StaticFilterAdapter extends RecyclerView.Adapter<StaticFilterAdapter.ViewHolder> {
    private String[] filters = {"По цене", "По миллиграммам", "По возрасту"};
    private String minPrice, maxPrice, minMg, maxMg, minAge, maxAge;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderFilterStaticBinding binding = ViewholderFilterStaticBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.staticItemText.setText(filters[position]);

        // Слушатели для изменения текста
        if (position == 0) {
            holder.binding.minValue.setHint("От");
            holder.binding.maxValue.setHint("До");

            holder.binding.minValue.setText(minPrice);
            holder.binding.maxValue.setText(maxPrice);

            holder.binding.minValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    minPrice = charSequence.toString();
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });

            holder.binding.maxValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    maxPrice = charSequence.toString();
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        } else if (position == 1) {
            holder.binding.minValue.setHint("От");
            holder.binding.maxValue.setHint("До");

            holder.binding.minValue.setText(minMg);
            holder.binding.maxValue.setText(maxMg);

            holder.binding.textRubleMax.setText("");
            holder.binding.textRubleMin.setText("");

            holder.binding.minValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    minMg = charSequence.toString();
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });

            holder.binding.maxValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    maxMg = charSequence.toString();
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        } else if (position == 2) {
            holder.binding.minValue.setHint("От");
            holder.binding.maxValue.setHint("До");

            holder.binding.minValue.setText(minAge);
            holder.binding.maxValue.setText(maxAge);

            holder.binding.textRubleMax.setText("");
            holder.binding.textRubleMin.setText("");

            holder.binding.minValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    minAge = charSequence.toString();
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });

            holder.binding.maxValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                    maxAge = charSequence.toString();
                }
                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getMinMg() {
        return minMg;
    }

    public String getMaxMg() {
        return maxMg;
    }

    public String getMinAge(){
        return minAge;
    }
    public String getMaxAge(){
        return maxAge;
    }


    @Override
    public int getItemCount() {
        return filters.length;
    }
    public void setFilterValues(String minPrice, String maxPrice, String minMg, String maxMg, String minAge, String maxAge) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minMg = minMg;
        this.maxMg = maxMg;
        this.minAge = minAge;
        this.maxAge = maxAge;
        notifyDataSetChanged();
    }
    public void clearFilterValues() {
        minPrice = null;
        maxPrice = null;
        minMg = null;
        maxMg = null;
        minAge = null;
        maxAge = null;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderFilterStaticBinding binding;

        public ViewHolder(ViewholderFilterStaticBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

