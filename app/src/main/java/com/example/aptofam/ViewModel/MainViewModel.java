package com.example.aptofam.ViewModel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.Model.SliderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private MutableLiveData<List<SliderModel>> _slider = new MutableLiveData<>();
    private MutableLiveData<List<ItemsModel>> _bestSeller = new MutableLiveData<>();
    private MutableLiveData<List<ItemsModel>> _saleItem = new MutableLiveData<>();
    public LiveData<List<SliderModel>> getSlider() {
        return _slider;
    }
    public LiveData<List<ItemsModel>> getBestSeller() {
        return _bestSeller;
    }
    public LiveData<List<ItemsModel>> getSaleItem() {
        return _saleItem;
    }
    public void loadSlider() {
        Log.d("Firebase", "Начало загрузки баннеров");
        DatabaseReference ref = firebaseDatabase.getReference("Banner");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Firebase", "Данные получены из 'Banner': " + snapshot.toString());
                List<SliderModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    Log.d("Firebase", "Обработка узла: " + childSnapshot.toString());
                    SliderModel list = childSnapshot.getValue(SliderModel.class);
                    if (list != null) {
                        lists.add(list);
                    } else {
                        Log.e("Firebase", "Ошибка: модель SliderModel равна null");
                    }
                }
                _slider.setValue(lists);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void loadBestSeller(){
        DatabaseReference ref = firebaseDatabase.getReference("ItemsBestS");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ItemsModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren())
                {
                    ItemsModel list = childSnapshot.getValue(ItemsModel.class);
                    if (list != null){
                        lists.add(list);
                    }
                    _bestSeller.setValue(lists);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void loadSaleItem(){
        DatabaseReference ref = firebaseDatabase.getReference("ItemsSale");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ItemsModel> lists = new ArrayList<>();
                for (DataSnapshot childSnapshot : snapshot.getChildren())
                {
                    ItemsModel list = childSnapshot.getValue(ItemsModel.class);
                    if (list != null){
                        lists.add(list);
                    }
                    _saleItem.setValue(lists);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
