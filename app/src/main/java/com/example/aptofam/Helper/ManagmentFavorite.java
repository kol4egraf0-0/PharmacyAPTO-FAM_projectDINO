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

public class ManagmentFavorite {
    private TinyDB tinyDB;
    private Context context;
    private DatabaseReference firebaseReference;
    private DatabaseReference itemsBestSReference;
    private DatabaseReference itemsSaleReference;
    private DatabaseReference othersReference;
    private String userId;
    private boolean isSynced = false; // Флаг синхронизации

    public ManagmentFavorite(Context context) {
        this.context = context;
        tinyDB = new TinyDB(context);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        } else {
            userId = null;
        }

        if (userId != null) {
            firebaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("favorite");
            itemsBestSReference = FirebaseDatabase.getInstance().getReference("ItemsBestS");
            itemsSaleReference = FirebaseDatabase.getInstance().getReference("ItemsSale");
            othersReference = FirebaseDatabase.getInstance().getReference("Others");
            syncFavoritesFromFirebase();
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
                System.out.println("Не робит");
            }
        };

        itemsBestSReference.addValueEventListener(itemUpdateListener);
        itemsSaleReference.addValueEventListener(itemUpdateListener);
        othersReference.addValueEventListener(itemUpdateListener);
    }
    private void updateItemInTinyDB(ItemsModel updatedItem) {
        // Получаем текущий список избранных товаров из TinyDB
        ArrayList<ItemsModel> favoriteList = tinyDB.getListObject("FavoriteList_" + userId, ItemsModel.class);
        if (favoriteList == null) {
            favoriteList = new ArrayList<>();
        } else {
            System.out.println("Текущий список избранного: ");
        }

        // Ищем товар по ID в списке избранного и обновляем его
        boolean itemFound = false;
        for (int i = 0; i < favoriteList.size(); i++) {
            if (favoriteList.get(i).getId().equals(updatedItem.getId())) {
                favoriteList.set(i, updatedItem); // Обновляем товар
                itemFound = true;
                break;
            }
        }

        // Если товар не найден в избранном, логируем это
        if (!itemFound) {
            System.out.println("Товар с ID " + updatedItem.getId() + " не найден в избранном.");
        }

        // Сохраняем обновленный список избранного в TinyDB
        if (itemFound) {
            tinyDB.putListObject("FavoriteList_" + userId, favoriteList);


            // Проверяем, что данные сохранились
            ArrayList<ItemsModel> updatedFavoriteList = tinyDB.getListObject("FavoriteList_" + userId, ItemsModel.class);
            if (updatedFavoriteList != null) {
                System.out.println("Проверка сохраненных данных: " + updatedFavoriteList.size() + " товаров.");
                for (ItemsModel item : updatedFavoriteList) {
                    if (item.getId().equals(updatedItem.getId())) {
                        break;
                    }
                }
            } else {
                System.out.println("Ошибка: список избранного пуст после сохранения.");
            }
        }
    }

    //Сердечко появилось/ушло
    public void addOrRemoveFavorite(ItemsModel item) {
        if (userId == null) {
            ToastUtils.showToast((AppCompatActivity) context, "Пользователь не авторизован");
            return;
        }

        ArrayList<ItemsModel> favoriteList = getFavoriteList();
        boolean existAlready = false;
        int index = -1;

        for (int i = 0; i < favoriteList.size(); i++) {
            if (favoriteList.get(i).getId().equals(item.getId())) {
                existAlready = true;
                index = i;
                break;
            }
        }

        if (existAlready) {
            favoriteList.remove(index);
            ToastUtils.showToast((AppCompatActivity) context, "Товар удалён из избранного");
            if (favoriteList.isEmpty()) {
                firebaseReference.removeValue()
                        .addOnSuccessListener(aVoid -> System.out.println("Избранное удалено из Firebase!"))
                        .addOnFailureListener(e -> System.err.println("Ошибка удаления из Firebase: " + e.getMessage()));
            } else {
                updateFavoritesInFirebase(favoriteList);
            }
        } else {
            if (item.getTitle() == null || item.getPicUrl() == null || item.getPrice() == 0.0) {
                item = restoreItemById(item.getId());
            }
            if (item != null) {
                favoriteList.add(item);
                ToastUtils.showToast((AppCompatActivity) context, "Товар добавлен в избранное");
                updateFavoritesInFirebase(favoriteList);
            } else {
                ToastUtils.showToast((AppCompatActivity) context, "Ошибка: товар не найден");
            }
        }

        saveFavoriteListLocally(favoriteList);
    }

    public ArrayList<ItemsModel> getFavoriteList() {
        if (userId != null && !isSynced) {
            syncFavoritesFromFirebase();
        }

        ArrayList<ItemsModel> favorites = tinyDB.getListObject("FavoriteList_" + userId, ItemsModel.class);
        if (favorites == null || favorites.isEmpty()) {
            System.out.println("ВНИМАНИЕ: Локальный список избранного пуст, ждем Firebase...");
        } else {
            System.out.println("Локальное избранное загружено (" + favorites.size() + " товаров).");
        }

        return (favorites != null) ? favorites : new ArrayList<>();
    }

    private void saveFavoriteListLocally(ArrayList<ItemsModel> favoriteList) {
        tinyDB.putListObject("FavoriteList_" + userId, favoriteList);
    }

    private void updateFavoritesInFirebase(ArrayList<ItemsModel> favoriteList) {
        ArrayList<String> favoriteIds = new ArrayList<>();
        for (ItemsModel item : favoriteList) {
            favoriteIds.add(item.getId());
        }

        firebaseReference.setValue(favoriteIds)
                .addOnSuccessListener(aVoid -> System.out.println("Избранное успешно обновлено в Firebase!"))
                .addOnFailureListener(e -> System.err.println("Ошибка обновления в Firebase: " + e.getMessage()));
    }

    private void syncFavoritesFromFirebase() {
        firebaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>() {};
                    ArrayList<String> favoriteIds = dataSnapshot.getValue(t);
                    ArrayList<ItemsModel> favoriteList = new ArrayList<>();

                    if (favoriteIds != null) {
                        for (String id : favoriteIds) {
                            ItemsModel item = restoreItemById(id);
                            if (item != null) {
                                favoriteList.add(item);
                            }
                        }
                    }

                    if (favoriteList.isEmpty()) {
                        System.out.println("ВНИМАНИЕ: Firebase вернул пустой список, локальные данные НЕ обновляем.");
                    } else {
                        saveFavoriteListLocally(favoriteList);
                        System.out.println("Избранное успешно загружено из Firebase (" + favoriteList.size() + " товаров).");
                    }
                } else {
                    System.out.println("Firebase не содержит избранного, оставляем локальные данные без изменений.");
                }
                isSynced = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Ошибка синхронизации избранного: " + databaseError.getMessage());
                isSynced = false;
            }
        });
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
