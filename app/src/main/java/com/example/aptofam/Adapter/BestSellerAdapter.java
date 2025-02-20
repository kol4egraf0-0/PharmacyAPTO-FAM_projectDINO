package com.example.aptofam.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
import com.example.aptofam.Utility.FavoriteButtonHandler;
import com.example.aptofam.databinding.ViewholderBestSellerBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class BestSellerAdapter extends RecyclerView.Adapter<BestSellerAdapter.ViewHolder> {
    private List<ItemsModel> items;
    private Context context;
    private ManagmentCart managmentCart;
    private int numberOrder;
    private ManagmentFavorite managmentFavorite;

    public BestSellerAdapter(List<ItemsModel> items, ManagmentCart managmentCart, int numberOrder, ManagmentFavorite managmentFavorite) {
        this.items = items;
        this.managmentCart = managmentCart;
        this.numberOrder = numberOrder;
        this.managmentFavorite = managmentFavorite;
    }

    @NonNull
    @Override
    public BestSellerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewholderBestSellerBinding binding = ViewholderBestSellerBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BestSellerAdapter.ViewHolder holder, int position) {
        ItemsModel item = items.get(position);
        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.priceTxt.setText(item.getPrice() + "₽");

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderBestSellerBinding binding;

        public ViewHolder(ViewholderBestSellerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
