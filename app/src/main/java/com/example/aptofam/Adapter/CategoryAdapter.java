package com.example.aptofam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Activity.CatalogActivity;
import com.example.aptofam.Model.CategoryModel;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.databinding.ViewholderCartBinding;
import com.example.aptofam.databinding.ViewholderCategoryBinding;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
    private ArrayList<CategoryModel> listCategory;
    private Context context;

    public CategoryAdapter(Context context, ArrayList<CategoryModel> listCategory) {
        this.context = context;
        this.listCategory=listCategory;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CategoryAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryModel category = listCategory.get(position);

        holder.binding.titleCategory.setText(category.getCategoryName());

        // Сбрасываем иконку для предотвращения артефактов
        holder.binding.iconCategory.setImageDrawable(null);

        // Устанавливаем иконки в зависимости от категории
        switch (category.getCategoryId()) {
            case 1: // Хиты продаж
                holder.binding.iconCategory.setImageResource(R.drawable.fire_item);
                break;
            case 2: // Акции
                holder.binding.iconCategory.setImageResource(R.drawable.sale_tag);
                break;
            default:
                // Для других категорий иконку не устанавливаем или устанавливаем стандартную
                holder.binding.iconCategory.setImageDrawable(null);
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (context instanceof CatalogActivity) {
                ((CatalogActivity) context).onCategorySelected(category);
            }
        });
    }


    @Override
    public int getItemCount() {
        return listCategory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCategoryBinding binding;
        public ViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
