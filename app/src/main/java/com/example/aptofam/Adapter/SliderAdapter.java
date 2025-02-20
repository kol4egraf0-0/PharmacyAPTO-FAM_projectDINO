package com.example.aptofam.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.bumptech.glide.request.RequestOptions;
import com.example.aptofam.Activity.ItemsFromSliderActivity;
import com.example.aptofam.Model.SliderModel;
import com.example.aptofam.R;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewholder> {
    private List<SliderModel> sliderModels;
    private ViewPager2 viewPager2;
    private Context context;

    public SliderAdapter(List<SliderModel> sliderModels, ViewPager2 viewPager2) {
        this.sliderModels = sliderModels;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public SliderAdapter.SliderViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.slider_image_container,parent,false);
        return new SliderViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderAdapter.SliderViewholder holder, int position) {
holder.setImage(sliderModels.get(position),context);
        holder.itemView.setOnClickListener(v -> {
            SliderModel clickedSlider = sliderModels.get(position);

            Intent intent = new Intent(context, ItemsFromSliderActivity.class);
            intent.putExtra("sliderId", clickedSlider.getSliderId());
            intent.putExtra("sliderUrl", clickedSlider.getUrl());
            intent.putExtra("sliderDescription", clickedSlider.getSliderDescription());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return sliderModels.size();
    }

    public class SliderViewholder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public SliderViewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        public void setImage(SliderModel sliderModel, Context context)
        {
            // Логируем URL, который загружается
            RequestOptions requestOptions = new RequestOptions().transform(new CenterInside());
            // Логируем начало загрузки
            Glide.with(context)
                    .load(sliderModel.getUrl())
                    .apply(requestOptions)
                    .into(imageView);
        }
    }
}
