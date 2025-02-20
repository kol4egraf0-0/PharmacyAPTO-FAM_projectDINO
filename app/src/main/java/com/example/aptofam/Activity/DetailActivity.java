package com.example.aptofam.Activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aptofam.Adapter.AptekaAdapter;
import com.example.aptofam.Adapter.ExpandableListAdapter;
import com.example.aptofam.Adapter.PicListAdapter;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Helper.ManagmentFavorite;
import com.example.aptofam.Model.ApteksModel;
import com.example.aptofam.Model.ChildModel;
import com.example.aptofam.Model.ItemsModel;
import com.example.aptofam.Model.ParentModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.databinding.ActivityDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DetailActivity extends BaseActivity {
    private ActivityDetailBinding binding;
    private ItemsModel item;
    private int numberOrder=1;
    private ManagmentCart managmentCart;
    private ManagmentFavorite managmentFavorite;
    private LinkedHashMap<String, ParentModel> parentItem = new LinkedHashMap<String,ParentModel>();
    private ArrayList<ParentModel> itemList = new ArrayList<ParentModel>();
    private ExpandableListAdapter expandableListAdapter;
    private ExpandableListView expandableListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        managmentFavorite = new ManagmentFavorite(this);
        managmentCart = new ManagmentCart(this);

        getBundleExtra();
        initLists();
        addData();

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());

        expandableListView = findViewById(R.id.characteristicsList);
        expandableListAdapter = new ExpandableListAdapter(DetailActivity.this,itemList);
        expandableListView.setAdapter(expandableListAdapter);
        expandAllGroup();
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            ParentModel parentInfo = itemList.get(groupPosition);
            ChildModel childInfo = parentInfo.getItemList().get(childPosition);
            return false;

        });
        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            ParentModel parentInfo = itemList.get(groupPosition);
            return false;
        });

       setupFavoriteButton();
    }

    private void setupFavoriteButton() {
        boolean isFavorite = managmentFavorite.getFavoriteList().stream()
                .anyMatch(favItem -> favItem.getId().equals(item.getId()));

        // Устанавливаем иконку для кнопки
        binding.favBtn1.setImageResource(isFavorite ? R.drawable.btn_addred : R.drawable.btn_addnotred);
        // Устанавливаем обработчик нажатия
        binding.favBtn1.setOnClickListener(v -> {
            managmentFavorite.addOrRemoveFavorite(item); // Добавляем/удаляем товар из избранного
            boolean updatedIsFavorite = managmentFavorite.getFavoriteList().stream()
                    .anyMatch(favItem -> favItem.getId().equals(item.getId()));
            binding.favBtn1.setImageResource(updatedIsFavorite ? R.drawable.btn_addred : R.drawable.btn_addnotred);
        });
    }


    private void initLists() {
        ArrayList<String> picList = new ArrayList<>();
        for(String imageUrl:item.getPicUrl()){
            picList.add(imageUrl);
        }

        Glide.with(this)
                .load(picList.get(0))
                .into(binding.picMain);

        binding.picList.setAdapter(new PicListAdapter(picList,binding.picMain));
        binding.picList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
    }

    private void getBundleExtra() {
        item = (ItemsModel) getIntent().getSerializableExtra("object");

        binding.titleTxt.setText(item.getTitle());
        binding.priceTxt.setText(item.getPrice()+"₽");

        if (item.getPriceSaleStrStrike() != null && !item.getPriceSaleStrStrike().isEmpty()) {
            binding.priceTxtSale.setText(item.getPriceSaleStrStrike() + "₽");
            binding.priceTxtSale.setPaintFlags(binding.priceTxtSale.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.priceTxt.setTextColor(ContextCompat.getColor(this, R.color.red));
        } else {
            binding.priceTxtSale.setVisibility(View.GONE); // скрываем элемент, если нет значения
        }

        List<Integer> categoryIds = item.getCategoryIds();
        if (categoryIds != null) {
            if (categoryIds.contains(1)) {
                // Если категория 1, то "Хит продаж" с синим фоном
                binding.hitSalesTextView.setText("Хит продаж");
                binding.hitSalesTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                binding.hitSalesTextView.setVisibility(View.VISIBLE);
            } else if (categoryIds.contains(2)) {
                // Если категория 2, то "Акции" с зеленым фоном
                binding.hitSalesTextView.setText("По Акции");
                binding.hitSalesTextView.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                binding.hitSalesTextView.setVisibility(View.VISIBLE);
            } else {
                // Если нет нужных категорий, скрываем TextView
                binding.hitSalesTextView.setVisibility(View.GONE);
            }
        } else {
            // Если categoryIds == null, скрываем TextView
            binding.hitSalesTextView.setVisibility(View.GONE);
        }

        binding.addToCardBtn.setOnClickListener(v -> {
            item.setNumberInCart(numberOrder);
            managmentCart.insertItems(item);
        });

        binding.backBtn.setOnClickListener(v -> finish());

        binding.locationAptekaTxt.setOnClickListener(v -> {
            List<Integer> aptekaIds = item.getAptekaIds();

            List<ApteksModel> aptekaList = getAptekaListByIds(aptekaIds);

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_apteka_list, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            RecyclerView aptekaRecyclerView = bottomSheetView.findViewById(R.id.aptekaRecyclerView);
            aptekaRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            AptekaAdapter aptekaAdapter = new AptekaAdapter(aptekaList);
            aptekaRecyclerView.setAdapter(aptekaAdapter);

            bottomSheetDialog.show();
        });

        getUserAptekaId();
    }

    private void updateStockInfo(int userAptekaId) {
        List<Integer> quantityList = item.getQuantity(); // Получаем список количеств товара
        LinearLayout stockInfoLayout = findViewById(R.id.stockInfoLayout);
        TextView stockInfoTxt = findViewById(R.id.stockInfoTxt);

        // Получаем кнопку "Добавить в корзину"
        Button addToCartBtn = binding.addToCardBtn;

        if (quantityList == null || quantityList.isEmpty() || userAptekaId < 1 || userAptekaId > quantityList.size()) {
            stockInfoLayout.setVisibility(View.GONE);
            disableAddToCart(addToCartBtn);
            return;
        }

        int stockForUserApteka = quantityList.get(userAptekaId - 1); // Берём по индексу

        if (stockForUserApteka > 10) {
            stockInfoLayout.setVisibility(View.GONE);
            enableAddToCart(addToCartBtn); // Если товара много, активируем кнопку
        } else if (stockForUserApteka > 0) {
            stockInfoTxt.setText("В наличии: " + stockForUserApteka + " шт.");
            stockInfoLayout.setVisibility(View.VISIBLE);
            enableAddToCart(addToCartBtn); // Если есть в наличии, активируем кнопку
        } else {
            stockInfoTxt.setText("Нет в наличии");
            stockInfoLayout.setVisibility(View.VISIBLE);
            disableAddToCart(addToCartBtn); // Если товара нет, блокируем кнопку
        }
    }
    private void disableAddToCart(Button button) {
        button.setEnabled(false);
        button.setAlpha(0.5f); // Полупрозрачная кнопка
    }

    private void enableAddToCart(Button button) {
        button.setEnabled(true);
        button.setAlpha(1.0f); // Полная яркость
    }


    private void getUserAptekaId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            updateStockInfo(-1); // Передаём -1, чтобы скрыть блок
            return;
        }

        String userId = user.getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("aptekaId");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int aptekaId = snapshot.getValue(Integer.class);
                    updateStockInfo(aptekaId);
                } else {
                    updateStockInfo(-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                updateStockInfo(-1);
            }
        });
    }

    private List<ApteksModel> getAptekaListByIds(List<Integer> aptekaIds) {
        List<ApteksModel> aptekaList = new ArrayList<>();

        Map<Integer, ApteksModel> aptekaMap = new HashMap<>();
        aptekaMap.put(1, new ApteksModel(1, "п. Новосиньково, д.36", 56.374686, 37.328045));
        aptekaMap.put(2, new ApteksModel(2, "п.Горшково, д.62", 56.37671, 37.4099));

        for (Integer id : aptekaIds) {
            if (aptekaMap.containsKey(id)) {
                aptekaList.add(aptekaMap.get(id));
            }
        }
        return aptekaList;
    }

    private void expandAllGroup() {
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            expandableListView.collapseGroup(i);
        }
    }

    private void addData() {
        if (item.getCharacteristics() != null) {
            Map<String, String> characteristics = new LinkedHashMap<>(item.getCharacteristics()); // Сохраняем порядок
            for (Map.Entry<String, String> entry : characteristics.entrySet()) {
                addItem("Характеристики", entry.getKey(), entry.getValue());
            }
        }
    }


    private int addItem(String parentItemList, String subItemList, String subItemDescription) {
        int groupPosition = 0;

        ParentModel parentInfo = parentItem.get(parentItemList);

        if (parentInfo == null) {
            parentInfo = new ParentModel();
            parentInfo.setName(parentItemList);
            parentItem.put(parentItemList, parentInfo);
            itemList.add(parentInfo);
        }

        ArrayList<ChildModel> childItemList = parentInfo.getItemList();
        ChildModel childInfo;

        if (childItemList.isEmpty()) {
            childInfo = new ChildModel();
            childInfo.setNumber(String.valueOf(childItemList.size() + 1));
            childItemList.add(childInfo);
        } else {
            childInfo = childItemList.get(childItemList.size() - 1);
        }

        // Добавляем новую пару "ключ-значение"
        childInfo.addName(subItemList);
        childInfo.addDescription(subItemDescription);

        parentInfo.setItemList(childItemList);
        groupPosition = childItemList.indexOf(parentInfo);

        return groupPosition;
    }
}
