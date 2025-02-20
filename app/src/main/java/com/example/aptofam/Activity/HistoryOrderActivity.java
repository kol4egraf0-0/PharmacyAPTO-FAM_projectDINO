package com.example.aptofam.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.aptofam.Adapter.OrderAdapter;
import com.example.aptofam.Helper.ManagmentOrder;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivityHistoryOrderBinding;
import com.example.aptofam.databinding.ActivityProfileEditBinding;

import java.util.List;
import java.util.Map;

public class HistoryOrderActivity extends BaseActivity {
    private ActivityHistoryOrderBinding binding;
    private OrderAdapter adapter;
    private ManagmentOrder managmentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setVariable();
        loadOrders();

        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void loadOrders() {
        managmentOrder = new ManagmentOrder(this);
        managmentOrder.getOrders(new ManagmentOrder.OrderLoadListener() {
            @Override
            public void onOrdersLoaded(List<Map<String, Object>> orders) {
                if (adapter == null) {
                    adapter = new OrderAdapter(HistoryOrderActivity.this, orders);
                    binding.historyItemOrderView.setLayoutManager(new LinearLayoutManager(HistoryOrderActivity.this));
                    binding.historyItemOrderView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onError(String error) {
                ToastUtils.showToast(HistoryOrderActivity.this, "Ошибка загрузки");
            }
        });
    }
}