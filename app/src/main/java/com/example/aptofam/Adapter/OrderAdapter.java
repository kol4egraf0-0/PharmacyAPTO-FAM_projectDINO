package com.example.aptofam.Adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aptofam.Helper.ManagmentOrder;
import com.example.aptofam.R;
import com.example.aptofam.Utility.ToastUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private Context context;
    private List<Map<String, Object>> orderList;

    public OrderAdapter(Context context, List<Map<String, Object>> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> order = orderList.get(position);

        String orderId = "Заказ №" + order.get("orderId");
        holder.orderId.setText(orderId);

        long timestamp = (long) order.get("timestamp");
        String formattedDate = new SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault()).format(new Date(timestamp));
        holder.orderTime.setText(formattedDate);

        holder.totalPrice.setText(order.get("totalPrice") + "₽");

        long timeRemaining = timestamp + 24 * 60 * 60 * 1000 - System.currentTimeMillis();

        if (timeRemaining > 0) {
            new CountDownTimer(timeRemaining, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Разбиваем на часы, минуты и секунды
                    int hours = (int) (millisUntilFinished / (1000 * 60 * 60));
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int seconds = (int) ((millisUntilFinished / 1000) % 60);
                    holder.timer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                }

                @Override
                public void onFinish() {
                    holder.timer.setText("00:00:00");
                    holder.expiredOrderIcon.setVisibility(View.VISIBLE);
                }
            }.start();
        } else {
            holder.timer.setText("00:00:00");
            holder.expiredOrderIcon.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> showOrderDetails(order));

        holder.expiredOrderIcon.setOnClickListener(v -> {
            String orderId2 = (String) order.get("orderId");
            ManagmentOrder managmentOrder = new ManagmentOrder(context);
            managmentOrder.deleteOrderFromFirebase(orderId2, () -> {

                orderList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, orderList.size());
                ToastUtils.showToast((AppCompatActivity) context, "Заказ удален");
            });
        });
    }

    private void showOrderDetails(Map<String, Object> order) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_order_items, null);
        bottomSheetDialog.setContentView(view);

        TextView orderIdView = view.findViewById(R.id.orderId);
        RecyclerView itemsRecyclerView = view.findViewById(R.id.itemsRecyclerView);

        orderIdView.setText("Заказ №" + order.get("orderId"));

        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");

        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        itemsRecyclerView.setAdapter(new OrderItemsAdapter(context, items));

        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderTime, totalPrice, timer;
        ImageView expiredOrderIcon;  // Новый элемент

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.orderId);
            orderTime = itemView.findViewById(R.id.orderTime);
            totalPrice = itemView.findViewById(R.id.totalPrice);
            timer = itemView.findViewById(R.id.timer);
            expiredOrderIcon = itemView.findViewById(R.id.binIcon);
        }
    }
}