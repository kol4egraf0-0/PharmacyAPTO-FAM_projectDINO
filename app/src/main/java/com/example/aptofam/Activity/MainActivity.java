package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import com.example.aptofam.Adapter.BestSellerAdapter;
import com.example.aptofam.Adapter.SaleItemAdapter;
import com.example.aptofam.Adapter.SliderAdapter;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.SliderModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.AutoSlideHandler;
import com.example.aptofam.Utility.EditTextUtility;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.ViewModel.MainViewModel;
import com.example.aptofam.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private AutoSlideHandler autoSlideHandler;
    private MainViewModel viewModel = new MainViewModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBanners();

        initBestSeller();

        initSaleItem();

        setVariable();

        updateNotificationBadge();

        EditText editText = findViewById(R.id.editTextText);
        EditTextUtility.setupEditTextListener(this, editText);

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);
        
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void setVariable() {
        binding.notificationView3.setOnClickListener(v -> {
          Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
          startActivity(intent);
        });
    }


    private void initBestSeller() {
        ManagmentCart managmentCart = new ManagmentCart(this);
        ManagmentFavorite managmentFavorite = new ManagmentFavorite(this);
        int numberOrder = 1;

        binding.progressBarBestSeller.setVisibility(View.VISIBLE);

        viewModel.getBestSeller().observe(this, items -> {
            //binding.recyclerBestSeller.setLayoutManager(new GridLayoutManager(this,2)); варик для 2 в низ =]
            binding.recyclerBestSeller.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerBestSeller.setAdapter(new BestSellerAdapter(items, managmentCart, numberOrder, managmentFavorite));
            binding.progressBarBestSeller.setVisibility(View.GONE);

            TextView goToBs = findViewById(R.id.goToBestSeller);
            goToBs.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ItemFromCatalogActivity.class);
                intent.putExtra("categoryName", "Хиты Продаж");
                intent.putExtra("categoryId", 1);
                intent.putExtra("items", new ArrayList<>(items)); // Передаем список товаров
                startActivity(intent);
            });
        });
        viewModel.loadBestSeller();
    }


    private void initSaleItem() {
        ManagmentCart managmentCart = new ManagmentCart(this);
        ManagmentFavorite managmentFavorite = new ManagmentFavorite(this);
        int numberOrder = 1;

        binding.progressBarSale.setVisibility(View.VISIBLE);

        // Наблюдатель за данными
        viewModel.getSaleItem().observe(this, items -> {
            binding.recyclerSale.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.recyclerSale.setAdapter(new SaleItemAdapter(items, managmentCart, numberOrder, managmentFavorite));
            binding.progressBarSale.setVisibility(View.GONE);

            TextView goToSale = findViewById(R.id.goToSale);
            goToSale.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ItemFromCatalogActivity.class);
                // Передаем категорию и товары
                intent.putExtra("categoryName", "Акции");
                intent.putExtra("categoryId", 2);
                intent.putExtra("items", new ArrayList<>(items));
                startActivity(intent);
            });
        });
        // Загружаем данные товаров
        viewModel.loadSaleItem();
    }


    private void initBanners() {
          binding.progressBar.setVisibility(View.VISIBLE);
          viewModel.getSlider().observe(this,banners->{
          setupBanners(banners);
          binding.progressBar.setVisibility(View.GONE);
          startAutoSlide();
         });
          viewModel.loadSlider();
         }
    private void setupBanners(List<SliderModel> images)
    {
        binding.viewPager2.setAdapter(new SliderAdapter(images,binding.viewPager2));
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPager2.setPageTransformer(transformer);
        if(images.size()>1)
        {
            binding.dotIndicator.setVisibility(View.VISIBLE);
            binding.dotIndicator.attachTo(binding.viewPager2);
        }
    }
    private void startAutoSlide() {
        autoSlideHandler = new AutoSlideHandler(binding.viewPager2, 1500);
        autoSlideHandler.startAutoSlide();
    }
    private void updateNotificationBadge() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) return;

        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId)
                .child("notifications");

        notificationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int notificationCount = (int) snapshot.getChildrenCount();

                if (notificationCount > 0) {
                    if (notificationCount > 99) {
                        binding.notificationBadge.setText("99");
                    } else {
                        binding.notificationBadge.setText(String.valueOf(notificationCount));
                    }
                    binding.notificationBadge.setVisibility(View.VISIBLE);
                } else {
                    binding.notificationBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (binding.recyclerBestSeller.getAdapter() != null) {
            binding.recyclerBestSeller.getAdapter().notifyDataSetChanged();
        }
        if (binding.recyclerSale.getAdapter() != null) {
            binding.recyclerSale.getAdapter().notifyDataSetChanged();
        }
        EditText editText = findViewById(R.id.editTextText);
        editText.setText("");

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());

        updateNotificationBadge();
    }
}
