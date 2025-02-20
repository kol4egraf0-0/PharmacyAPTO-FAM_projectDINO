package com.example.aptofam.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aptofam.Activity.CartActivity;
import com.example.aptofam.Activity.DetailActivity;
import com.example.aptofam.Helper.ChangeNumberItemsListener;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.ApteksModel;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ViewholderCartBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<ItemsModel> listItemSelected;
    private ManagmentCart managmentCart;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private CartActivity cartActivity;
    private ManagmentFavorite managmentFavorite;
    private Map<Integer, ApteksModel> aptekaList;
    private int userAptekaId = -1;
    public CartAdapter(ArrayList<ItemsModel> listItemSelected, Context context, ChangeNumberItemsListener changeNumberItemsListener, CartActivity cartActivity,  ManagmentFavorite managmentFavorite,  Map<Integer, ApteksModel> aptekaList) {
        this.listItemSelected = listItemSelected;
        this.changeNumberItemsListener = changeNumberItemsListener;
        this.managmentCart = new ManagmentCart(context);
        this.cartActivity = cartActivity;
        this.managmentFavorite = managmentFavorite;
        this.aptekaList = aptekaList;

    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCartBinding binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        ItemsModel item =listItemSelected.get(position);

        holder.binding.titleTxt.setText(item.getTitle());
        holder.binding.feeEachItem.setText(item.getPrice()+"₽");
        holder.binding.totalEachItem.setText(String.format("%.2f₽", item.getNumberInCart() * item.getPrice()));
        holder.binding.numberItemTxt.setText(String.valueOf(item.getNumberInCart()));



        Glide.with(holder.itemView.getContext())
                .load(item.getPicUrl().get(0))
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.picCart);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailActivity.class);
            intent.putExtra("object", item);
            holder.itemView.getContext().startActivity(intent);
        });

        holder.binding.plusCartBtn.setOnClickListener(v -> {
            if (!holder.binding.plusCartBtn.isEnabled()) {
                ToastUtils.showToast((AppCompatActivity) holder.itemView.getContext(),
                        "Достигнут лимит в аптеке!");
            }
        });

        if (item.getNumberInCart() >= 10) {
            ToastUtils.showToast((AppCompatActivity) holder.itemView.getContext(), "Чтобы заказать больше 10 товаров в 1 заказе, обратитесь напрямую в аптеку!");
            holder.binding.plusCartBtn.setEnabled(false);
        } else {
            holder.binding.plusCartBtn.setEnabled(true);
        }

        holder.binding.minusCartBtn.setOnClickListener(v -> {
            notifyDataSetChanged();
            if (listItemSelected.get(position).getNumberInCart() == 1 || listItemSelected.get(position).getNumberInCart() == 0) {
                cartActivity.showDeleteDialog(position, () -> {
                    if (changeNumberItemsListener != null) {
                        changeNumberItemsListener.onChanged();
                    }

                    cartActivity.resetCouponSilently();
                });
            } else {
                managmentCart.minusItem(listItemSelected, position, () -> {
                    if (changeNumberItemsListener != null) {
                        changeNumberItemsListener.onChanged();
                    }
                    cartActivity.resetCouponSilently();
                });
            }
        });
        boolean isFavorite = managmentFavorite.getFavoriteList().stream()
                .anyMatch(favItem -> favItem.getId().equals(item.getId()));
        holder.binding.favBtn1.setImageResource(isFavorite ? R.drawable.btn_addred : R.drawable.btn_addnotred);
        holder.binding.favBtn1.setOnClickListener(v -> {
            managmentFavorite.addOrRemoveFavorite(item);
            notifyItemChanged(position);
        });

        List<Integer> categoryIds = item.getCategoryIds();
        if (categoryIds != null) {
            if (categoryIds.contains(1)) {
                holder.binding.categoryLabel.setText("Хит продаж");
                holder.binding.categoryLabel.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue));
                holder.binding.categoryLabel.setVisibility(View.VISIBLE);
            } else if (categoryIds.contains(2)) {
                holder.binding.categoryLabel.setText("Акции");
                holder.binding.categoryLabel.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                holder.binding.categoryLabel.setVisibility(View.VISIBLE);
            } else {
                holder.binding.categoryLabel.setVisibility(View.GONE);
            }
        } else {
            holder.binding.categoryLabel.setVisibility(View.GONE);
        }
        List<Integer> aptekaIds = item.getAptekaIds();
        if (aptekaIds != null && !aptekaIds.isEmpty()) {
            StringBuilder addresses = new StringBuilder();
            for (Integer id : aptekaIds) {
                if (aptekaList.containsKey(id)) {
                    ApteksModel apteka = aptekaList.get(id);
                    addresses.append(apteka.getAptekaName()).append("\n");
                }
            }
            holder.binding.aptekaAddressTxt.setText(addresses.toString().trim());
            holder.binding.aptekaAddressTxt.setVisibility(View.VISIBLE);
        } else {
            holder.binding.aptekaAddressTxt.setVisibility(View.GONE);
        }

        getUserAptekaId(holder, item, position);
    }

    private void getUserAptekaId(@NonNull CartAdapter.ViewHolder holder, ItemsModel item, int position) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            Log.e("Firebase", "Пользователь не авторизован");
            return;
        }

        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("aptekaId");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int aptekaId = snapshot.getValue(Integer.class);
                    Log.d("Firebase", "Найден aptekaId: " + aptekaId);
                    updateCartButton(holder, item, position, aptekaId);
                } else {
                    Log.e("Firebase", "aptekaId не найден");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Ошибка запроса: " + error.getMessage());
            }
        });
    }

    private void updateCartButton(@NonNull CartAdapter.ViewHolder holder, ItemsModel item, int position, int aptekaId) {
        List<Integer> quantityList = item.getQuantity();

        // Проверяем, есть ли данные
        if (quantityList == null || quantityList.isEmpty() || aptekaId < 1 || aptekaId > quantityList.size()) {
            disablePlusButton(holder);
            return;
        }

        int availableStock = quantityList.get(aptekaId - 1); // Берём по индексу

        // Если товара нет вообще
        if (availableStock == 0) {
            disablePlusButton(holder);
            return;
        }

        // Если в корзине уже максимум возможного
        if (item.getNumberInCart() >= availableStock  || item.getNumberInCart() >= 10) {
            disablePlusButton(holder);
        } else {
            enablePlusButton(holder);
        }

        holder.binding.plusCartBtn.setOnClickListener(v -> {
            if (item.getNumberInCart() < availableStock) {
                managmentCart.plusItem(listItemSelected, position, () -> {
                    notifyDataSetChanged();
                    if (changeNumberItemsListener != null) {
                        changeNumberItemsListener.onChanged();
                    }
                });
            } else {
                ToastUtils.showToast((AppCompatActivity) holder.itemView.getContext(),
                        "Достигнут максимум в аптеке: " + availableStock + " шт.");
            }
        });
    }

    private void disablePlusButton(@NonNull CartAdapter.ViewHolder holder) {
        holder.binding.plusCartBtn.setEnabled(false);
        holder.binding.plusCartBtn.setAlpha(1.0f);
    }

    private void enablePlusButton(@NonNull CartAdapter.ViewHolder holder) {
        holder.binding.plusCartBtn.setEnabled(true);
        holder.binding.plusCartBtn.setAlpha(1.0f);
    }


    @Override
    public int getItemCount() {
        return listItemSelected.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ViewholderCartBinding binding;
        public ViewHolder(ViewholderCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
