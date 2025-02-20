package com.example.aptofam.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.aptofam.Activity.DetailActivity;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.CartButtonHelper;
import com.example.aptofam.Utility.FavoriteButtonHandler; // Импортируем класс обработчика
import com.example.aptofam.databinding.ViewholderSaleBinding;


import java.util.List;

public class SaleItemAdapter extends RecyclerView.Adapter<SaleItemAdapter.ViewHolder> {
    private List<ItemsModel> items;
    private Context context;
    private ManagmentCart managmentCart; // Поле для ManagmentCart
    private ManagmentFavorite managmentFavorite;
    private int numberOrder; // Поле для количества товаров


    public SaleItemAdapter(List<ItemsModel> items, ManagmentCart managmentCart, int numberOrder,ManagmentFavorite managmentFavorite) {
        this.items = items;
        this.managmentCart = managmentCart;
        this.numberOrder = numberOrder;
        this.managmentFavorite = managmentFavorite;
    }

    @NonNull
    @Override
    public SaleItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderSaleBinding binding = ViewholderSaleBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleItemAdapter.ViewHolder holder, int position) {
        ItemsModel item = items.get(position);
        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText(item.getPrice() + "₽");
        holder.binding.priceTxtSale.setText(item.getPriceSaleStrStrike() + "₽");
        holder.binding.priceTxtSale.setPaintFlags(holder.binding.priceTxtSale.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Glide.with(context)
                .load(item.getPicUrl().get(0))
                .apply(new RequestOptions().transform(new CenterCrop()))
                .into(holder.binding.picItem);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", item);
            context.startActivity(intent);
        });
        boolean isFavorite = managmentFavorite.getFavoriteList().stream()
                .anyMatch(favItem -> favItem.getId().equals(item.getId()));
        holder.binding.favBtn1.setImageResource(isFavorite ? R.drawable.btn_addred : R.drawable.btn_addnotred);
        holder.binding.favBtn1.setOnClickListener(v -> {
            managmentFavorite.addOrRemoveFavorite(item);
            notifyItemChanged(position);
        });
        CartButtonHelper.setupCartButton(context, holder.binding.addToCardBtn, item, numberOrder, managmentCart);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderSaleBinding binding;

        public ViewHolder(ViewholderSaleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
