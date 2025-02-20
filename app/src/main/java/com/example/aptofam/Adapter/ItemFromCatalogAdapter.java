package com.example.aptofam.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aptofam.Activity.DetailActivity;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.CartButtonHelper;
import com.example.aptofam.Utility.FavoriteManager;
import com.example.aptofam.databinding.ViewholderCategoryBinding;
import com.example.aptofam.databinding.ViewholderFavouriteBinding;
import com.example.aptofam.databinding.ViewholderItemFromCatalogBinding;

import java.util.ArrayList;
import java.util.List;

public class ItemFromCatalogAdapter extends RecyclerView.Adapter<ItemFromCatalogAdapter.ViewHolder> {

    private ArrayList<ItemsModel> itemsFromCatalog;
    private Context context;
    private ManagmentFavorite managmentFavorite;
    private ManagmentCart managmentCart;
    private int numberOrder;

    public ItemFromCatalogAdapter(ArrayList<ItemsModel> itemsFromCatalog, ManagmentFavorite managmentFavorite, ManagmentCart managmentCart, int numberOrder) {
        this.itemsFromCatalog = itemsFromCatalog != null ? itemsFromCatalog : new ArrayList<>();
        this.managmentFavorite = managmentFavorite;
        this.managmentCart = managmentCart;
        this.numberOrder = numberOrder;
    }

    @NonNull
    @Override
    public ItemFromCatalogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderItemFromCatalogBinding binding = ViewholderItemFromCatalogBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ItemFromCatalogAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemFromCatalogAdapter.ViewHolder holder, int position) {
        ItemsModel item = itemsFromCatalog.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText(String.valueOf(item.getPrice())+"₽");

        if (item.getPriceSaleStrStrike() != null && !item.getPriceSaleStrStrike().isEmpty()) {
            holder.binding.priceTxtSale.setText(item.getPriceSaleStrStrike() + "₽");
            holder.binding.priceTxtSale.setPaintFlags(holder.binding.priceTxtSale.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else {
            holder.binding.priceTxtSale.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.picItemCatalog);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("object", item);
            holder.itemView.getContext().startActivity(intent);
        });

        boolean isFavorite = managmentFavorite.getFavoriteList().stream()
                .anyMatch(favItem -> favItem.getId().equals(item.getId()));
        holder.binding.favBtn1.setImageResource(isFavorite ? R.drawable.btn_addred : R.drawable.btn_addnotred);

        holder.binding.favBtn1.setOnClickListener(v -> {
            managmentFavorite.addOrRemoveFavorite(item);
            notifyItemChanged(position);
        });
        CartButtonHelper.setupCartButton(context, holder.binding.addToCardBtn, item, numberOrder, managmentCart);

        List<Integer> categoryIds = item.getCategoryIds();
        if (categoryIds != null) {
            if (categoryIds.contains(1)) {
                // Если категория 1, то "Хит продаж" с синим фоном
                holder.binding.categoryLabel.setText("Хит продаж");
                holder.binding.categoryLabel.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue));
                holder.binding.categoryLabel.setVisibility(View.VISIBLE);
            } else if (categoryIds.contains(2)) {
                // Если категория 2, то "Акции" с зеленым фоном
                holder.binding.categoryLabel.setText("Акции");
                holder.binding.categoryLabel.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                holder.binding.categoryLabel.setVisibility(View.VISIBLE);
            } else {
                // Если нет нужных категорий, скрываем TextView
                holder.binding.categoryLabel.setVisibility(View.GONE);
            }
        } else {
            // Если categoryIds == null, скрываем TextView
            holder.binding.categoryLabel.setVisibility(View.GONE);
        }
    }
    public void updateData(List<ItemsModel> filteredItems) {
        Log.d("DebugUpdate", "Before updateData: itemsFromCatalog size = " + (this.itemsFromCatalog != null ? this.itemsFromCatalog.size() : "null"));

        if (filteredItems == null) {
            Log.e("DebugUpdate", "Filtered items list is null!");
            return;
        }

        // Создаём новый список
        this.itemsFromCatalog = new ArrayList<>(filteredItems);

        Log.d("DebugUpdate", "After updateData: itemsFromCatalog size = " + this.itemsFromCatalog.size());
        notifyDataSetChanged();
    }

    public List<ItemsModel> getItems() {
        return new ArrayList<>(itemsFromCatalog);
    }

    @Override
    public int getItemCount() {
        Log.d("DebugAdapter", "getItemCount called. itemsFromCatalog size: " + (itemsFromCatalog != null ? itemsFromCatalog.size() : "null"));
        return itemsFromCatalog != null ? itemsFromCatalog.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ViewholderItemFromCatalogBinding binding;
        public ViewHolder(ViewholderItemFromCatalogBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
