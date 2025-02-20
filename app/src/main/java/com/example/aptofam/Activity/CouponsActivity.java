package com.example.aptofam.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Adapter.CouponsAdapter;
import com.example.aptofam.Model.CouponModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.databinding.ActivityCouponsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CouponsActivity extends BaseActivity {
    ActivityCouponsBinding binding;
    private DatabaseReference database;
    private CouponsAdapter adapter;
    private List<CouponModel> couponList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCouponsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();

        initRecycler();

        loadCoupons();

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(0);
    }

    private void initRecycler() {
        couponList = new ArrayList<>();
        adapter = new CouponsAdapter(couponList);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.couponsRecyclerView.setLayoutManager(layoutManager);
        binding.couponsRecyclerView.setAdapter(adapter);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void loadCoupons() {
        database = FirebaseDatabase.getInstance().getReference("Coupons");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                couponList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String code = child.child("code").getValue(String.class);
                    String discount = child.child("discount").getValue(String.class);
                    String validUntil = child.child("validUntil").getValue(String.class);
                    String description = child.child("description").getValue(String.class);
                    Integer percentage = child.child("percentage").getValue(Integer.class);
                    Integer minQuantity = child.child("minQuantity").getValue(Integer.class);
                    List<Integer> categoryIds = child.child("categoryIds").getValue(new GenericTypeIndicator<List<Integer>>() {});
                    Integer amountMoney = child.child("amountMoney").getValue(Integer.class);
                    if (code != null && discount != null && validUntil != null && description != null) {
                        couponList.add(new CouponModel(code, discount, validUntil, description, percentage, minQuantity, categoryIds, amountMoney));
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}