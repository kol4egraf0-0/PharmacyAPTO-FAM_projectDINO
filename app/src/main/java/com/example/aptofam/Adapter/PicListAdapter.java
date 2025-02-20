package com.example.aptofam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.Model.SliderModel;
import com.example.aptofam.R;
import com.example.aptofam.databinding.ViewholderBestSellerBinding;
import com.example.aptofam.databinding.ViewholderPiclistBinding;

import java.util.List;

public class PicListAdapter extends RecyclerView.Adapter<PicListAdapter.ViewHolder>{
    private List<String> items;
    private Context context;
    private ImageView picMain;

    public PicListAdapter(List<String> items, ImageView picMain) {
        this.items = items;
        this.picMain = picMain;
    }

    @NonNull
    @Override
    public PicListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        ViewholderPiclistBinding binding = ViewholderPiclistBinding.inflate(LayoutInflater.from(context),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PicListAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(items.get(position))
                .into(holder.binding.picList);
        holder.binding.getRoot().setOnClickListener(v -> Glide.with(holder.itemView.getContext())
                .load(items.get(position))
                .into(picMain));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderPiclistBinding binding;
        public ViewHolder(ViewholderPiclistBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
