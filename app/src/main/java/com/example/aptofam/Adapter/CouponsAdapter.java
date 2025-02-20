package com.example.aptofam.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Model.CouponModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.ToastUtils;

import java.util.List;

public class CouponsAdapter extends RecyclerView.Adapter<CouponsAdapter.ViewHolder> {
    private List<CouponModel> couponsList;

    public CouponsAdapter(List<CouponModel> couponsList) {
        this.couponsList = couponsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CouponModel coupon = couponsList.get(position);
        holder.couponCode.setText(coupon.getCode());
        holder.couponDiscount.setText(coupon.getDiscount());
        holder.couponValidUntil.setText(coupon.getValidUntil());
        holder.couponDescription.setText(coupon.getDescription());

        holder.itemView.setOnClickListener(v -> {
            String code = coupon.getCode();
            ClipboardManager clipboard = (ClipboardManager) holder.itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Promo Code", code);
            clipboard.setPrimaryClip(clip);
            ToastUtils.showToast((AppCompatActivity) holder.itemView.getContext(), "Промокод скопирован: " + code);
        });
    }

    @Override
    public int getItemCount() {
        return couponsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView couponCode;
        TextView couponDiscount;
        TextView couponValidUntil;
        TextView couponDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            couponCode = itemView.findViewById(R.id.couponCode);
            couponDiscount = itemView.findViewById(R.id.couponDiscount);
            couponValidUntil = itemView.findViewById(R.id.couponValidUntil);
            couponDescription = itemView.findViewById(R.id.couponDescription);
        }
    }
}