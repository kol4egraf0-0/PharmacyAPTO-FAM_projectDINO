package com.example.aptofam.Helper;

import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.Utility.ToastUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ManagmentCart {
    private static final String TAG = "ManagmentCart";
    private TinyDB tinyDB;
    private Context context;
    private DatabaseReference firebaseReference;
    private DatabaseReference itemsBestSReference;
    private DatabaseReference itemsSaleReference;
    private DatabaseReference othersReference;
    private String userId;
    private boolean isSynced = false; // Флаг синхронизации

    public ManagmentCart(Context context) {
        this.context = context;
        tinyDB = new TinyDB(context);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        } else {
            userId = null;
        }

        if (userId != null) {
            firebaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("cart");
            itemsBestSReference = FirebaseDatabase.getInstance().getReference("ItemsBestS");
            itemsSaleReference = FirebaseDatabase.getInstance().getReference("ItemsSale");
            othersReference = FirebaseDatabase.getInstance().getReference("Others");
            syncCartFromFirebase();
            setupItemsListeners();
        }

    }

    private void setupItemsListeners() {
        ValueEventListener itemUpdateListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ItemsModel item = snapshot.getValue(ItemsModel.class);
                        if (item != null) {
                            updateItemInTinyDB(item); // Обновляем товар в TinyDB
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Ошибка слушателя: " + databaseError.getMessage());
            }
        };

        itemsBestSReference.addValueEventListener(itemUpdateListener);
        itemsSaleReference.addValueEventListener(itemUpdateListener);
        othersReference.addValueEventListener(itemUpdateListener);
    }

    private void updateItemInTinyDB(ItemsModel updatedItem) {
        ArrayList<ItemsModel> cartList = getListCart();
        if (cartList == null) {
            cartList = new ArrayList<>();
        }

        boolean itemFound = false;
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).getId().equals(updatedItem.getId())) {
                cartList.set(i, updatedItem); // Обновляем товар
                itemFound = true;
                break;
            }
        }

        if (itemFound) {
            saveCartListLocally(cartList);
            Log.d(TAG, "Обновлен товар в TinyDB: " + updatedItem.getTitle());
        } else {
            Log.d(TAG, "Товар не найден в корзине: " + updatedItem.getId());
        }
    }
    public void insertItems(ItemsModel item) {
        if (userId == null) {
            ToastUtils.showToast((AppCompatActivity) context, "Пользователь не авторизован");
            return;
        }

        ArrayList<ItemsModel> listItem = getListCart();
        boolean existAlready = false;
        int index = -1;

        for (int i = 0; i < listItem.size(); i++) {
            if (listItem.get(i).getId().equals(item.getId())) {
                existAlready = true;
                index = i;
                break;
            }
        }

        if (existAlready) {
            listItem.get(index).setNumberInCart(item.getNumberInCart());
            ToastUtils.showToast((AppCompatActivity) context, "Товар уже добавлен в корзину");
        } else {
            listItem.add(item);
            ToastUtils.showToast((AppCompatActivity) context, "Добавлено в корзину");
        }

        saveCartListLocally(listItem);
        updateCartInFirebase(listItem);
    }

    public ArrayList<ItemsModel> getListCart() {
        if (userId != null && !isSynced) {
            syncCartFromFirebase();
        }

        ArrayList<ItemsModel> cartItems = tinyDB.getListObject("CartList_" + userId, ItemsModel.class);
        if (cartItems == null || cartItems.isEmpty()) {
            Log.w(TAG, "Локальный список корзины пуст, ждем Firebase...");
        } else {
            Log.d(TAG, "Локальная корзина загружена (" + cartItems.size() + " товаров).");
        }

        return (cartItems != null) ? cartItems : new ArrayList<>();
    }

    private void saveCartListLocally(ArrayList<ItemsModel> cartList) {
        tinyDB.putListObject("CartList_" + userId, cartList);
    }

    private void updateCartInFirebase(ArrayList<ItemsModel> cartList) {
        ArrayList<String> cartIds = new ArrayList<>();
        for (ItemsModel item : cartList) {
            cartIds.add(item.getId());
        }

        firebaseReference.setValue(cartIds)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Корзина успешно обновлена в Firebase"))
                .addOnFailureListener(e -> Log.e(TAG, "Ошибка обновления в Firebase: " + e.getMessage()));
    }

    private void syncCartFromFirebase() {
        firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    ArrayList<String> cartIds = dataSnapshot.getValue(t);
                    ArrayList<ItemsModel> cartList = new ArrayList<>();

                    if (cartIds != null) {
                        for (String id : cartIds) {
                            ItemsModel item = restoreItemById(id);
                            if (item != null) {
                                cartList.add(item);
                            }
                        }
                    }

                    if (cartList.isEmpty()) {
                        Log.w(TAG, "Firebase вернул пустую корзину, локальные данные НЕ обновляем.");
                    } else {
                        saveCartListLocally(cartList);
                        Log.d(TAG, "Корзина успешно загружена из Firebase (" + cartList.size() + " товаров).");
                    }
                } else {
                    Log.w(TAG, "Firebase не содержит корзины, оставляем локальные данные без изменений.");
                }
                isSynced = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Ошибка синхронизации корзины: " + databaseError.getMessage());
                isSynced = false;
            }
        });
    }

    public void minusItem(ArrayList<ItemsModel> listItems, int position, ChangeNumberItemsListener listener) {
        if (listItems.get(position).getNumberInCart() == 1  || listItems.get(position).getNumberInCart() == 0) {
            listItems.remove(position);
        } else {
            listItems.get(position).setNumberInCart(listItems.get(position).getNumberInCart() - 1);
        }
        saveCartListLocally(listItems);
        updateCartInFirebase(listItems);
        listener.onChanged();
    }

    public void plusItem(ArrayList<ItemsModel> listItems, int position, ChangeNumberItemsListener listener) {
        listItems.get(position).setNumberInCart(listItems.get(position).getNumberInCart() + 1);
        saveCartListLocally(listItems);
        updateCartInFirebase(listItems);
        listener.onChanged();
    }
    private ItemsModel restoreItemById(String id) {
        List<ItemsModel> allItems = getAllItems();
        for (ItemsModel item : allItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    private List<ItemsModel> getAllItems() {
        List<ItemsModel> allItems = new ArrayList<>();
        allItems.addAll(getItemsBestS());
        allItems.addAll(getItemsSale());
        allItems.addAll(getOthers());
        return allItems;
    }

    private List<ItemsModel> getItemsBestS() {
        return new ArrayList<>();
    }

    private List<ItemsModel> getItemsSale() {
        return new ArrayList<>();
    }

    private List<ItemsModel> getOthers() {
        return new ArrayList<>();
    }

}
