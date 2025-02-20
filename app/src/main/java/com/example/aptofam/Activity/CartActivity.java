    package com.example.aptofam.Activity;

    import android.content.Intent;
    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.text.Editable;
    import java.util.concurrent.atomic.AtomicInteger;
    import java.util.concurrent.atomic.AtomicBoolean;
    import android.text.TextWatcher;
    import android.util.Log;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.Button;
    import android.widget.EditText;

    import androidx.annotation.NonNull;
    import androidx.appcompat.widget.AppCompatButton;
    import androidx.recyclerview.widget.LinearLayoutManager;

    import com.example.aptofam.Adapter.CartAdapter;
    import com.example.aptofam.Helper.ChangeNumberItemsListener;
    import com.example.aptofam.Helper.ManagmentCart;
    import com.example.aptofam.Helper.ManagmentFavorite;
    import com.example.aptofam.Helper.ManagmentOrder;
    import com.example.aptofam.Model.ApteksModel;
    import com.example.aptofam.Model.ItemsModel;
    import com.example.aptofam.R;
    import com.example.aptofam.Utility.NavigationScrollvUpDown;
    import com.example.aptofam.Utility.NavigationUtility;
    import com.example.aptofam.Utility.ToastUtils;
    import com.example.aptofam.databinding.ActivityCartBinding;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class CartActivity extends BaseActivity {
        private ActivityCartBinding binding;
        private ManagmentCart managmentCart;

        private EditText editTextCoupon;
        private Button btnApplyCoupon;

        private String appliedCouponCode = null;
        private Integer appliedCouponPercentage = null;
        private boolean isCouponActivated = false;
        private boolean orderPossible = true;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            binding = ActivityCartBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            managmentCart=new ManagmentCart(this);
            setVariable();
            calculatorCart();
            initCartList();

            NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);
            NavigationUtility.setupBottomNavigation(this, binding.getRoot());

            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(0);
        }
        public void showDeleteDialog(int position, ChangeNumberItemsListener listener) {
            android.app.Dialog dialog = new android.app.Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.item_minus_choice);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            AppCompatButton btnDelete = dialog.findViewById(R.id.btn_delete_item);
            AppCompatButton btnClose = dialog.findViewById(R.id.btn_close);

            if (managmentCart.getListCart().get(position).getNumberInCart() <= 1) {
                dialog.show();
            } else {
                managmentCart.minusItem(managmentCart.getListCart(), position, listener);
            }

            btnDelete.setOnClickListener(v -> {managmentCart.minusItem(managmentCart.getListCart(), position, listener);
                dialog.dismiss();
                recreate();
            });
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }

        private Map<Integer, ApteksModel> getAptekaMapFromFirebase() {
            Map<Integer, ApteksModel> aptekaList = new HashMap<>();
            aptekaList.put(1, new ApteksModel(1, "п. Новосиньково, д.36", 56.374686, 37.328045));
            aptekaList.put(2, new ApteksModel(2, "п.Горшково, д.62", 56.37671, 37.4099));
            return aptekaList;
        }
        private int getSelectedAptekaId() {
            SharedPreferences sharedPreferences = getSharedPreferences("UserProfilePrefs", MODE_PRIVATE);
            int selectedAptekaId = sharedPreferences.getInt("selectedAptekaId", -1); // По умолчанию -1, если аптека не выбрана
            Log.d("CartActivity", "Аптека: " + selectedAptekaId);
            return selectedAptekaId;
        }
        private boolean isItemAvailableInApteka(ItemsModel item, int selectedAptekaId, Map<Integer, ApteksModel> aptekaMap) {
            if (item.getNumberInCart() == 0) {
                return false;
            }

            for (Integer aptekaId : item.getAptekaIds()) {
                if (aptekaId.equals(selectedAptekaId)) {
                    return true;
                }
            }
            return false;
        }

        private void initCartList() {
            ManagmentFavorite managmentFavorite = new ManagmentFavorite(this);
            Map<Integer, ApteksModel> aptekaList = getAptekaMapFromFirebase();
            int selectedAptekaId = getSelectedAptekaId();

            List<ItemsModel> cartItems = managmentCart.getListCart();
            boolean allItemsAvailable = true;

            for (ItemsModel item : cartItems) {
                if (!isItemAvailableInApteka(item, selectedAptekaId, aptekaList)) {
                    allItemsAvailable = false;
                    break;
                }
            }

            binding.cartView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), this, this::calculatorCart, this, managmentFavorite,aptekaList));

            if (!allItemsAvailable && selectedAptekaId != -1) {
                ToastUtils.showToast(this, "Возможно, в выбранной вами аптеке нет некоторых товаров или их недостаточно. Измените местоположение получения заказа (местоположение аптеки в профиле) или удалите товары.");
            }
        }
        private double calculateExistingSale() {
            double sale = 0.0;

            ArrayList<ItemsModel> cartItems = managmentCart.getListCart();
            for (ItemsModel item : cartItems) {
                String priceSaleStrStrike = item.getPriceSaleStrStrike();

                if (priceSaleStrStrike != null && !priceSaleStrStrike.isEmpty()) {
                    try {
                        double originalPrice = Double.parseDouble(priceSaleStrStrike);
                        double currentPrice = item.getPrice();
                        sale += Math.max(0, (originalPrice - currentPrice) * item.getNumberInCart());
                    } catch (NumberFormatException e) {
                        sale += 0.0;
                    }
                }
            }
            return Math.round(sale * 100) / 100.0; // Округление до двух знаков
        }
        private void applyDiscount(int percentage, double existingSale) {
            double discountedTotal = 0.0;

            ArrayList<ItemsModel> cartItems = managmentCart.getListCart();
            for (ItemsModel item : cartItems) {
                discountedTotal += item.getPrice() * item.getNumberInCart();
            }

            double couponDiscount = discountedTotal * percentage / 100;
            double totalSale = existingSale + couponDiscount; // Общая скидка
            double total = discountedTotal - couponDiscount; // Итоговая сумма

            long roundedTotal = Math.round(total);


            binding.totalFeeTxt.setText(String.format("%.2f₽", discountedTotal + existingSale));
            binding.totalSaleTxt.setText(String.format("%.2f₽", totalSale));
            binding.totalTxt.setText(roundedTotal + "₽");
        }

        private void applyCoupon() {
            String couponCode = editTextCoupon.getText().toString().trim();

            if (couponCode.isEmpty()) {
                calculatorCart();
                return;
            }

            if (isCouponActivated && couponCode.equals(appliedCouponCode)) {
                ToastUtils.showToast(CartActivity.this, "Промокод уже применен");
                return;
            }

            if (isCouponActivated && !couponCode.equals(appliedCouponCode)) {
                isCouponActivated = false;
                appliedCouponCode = null;
                appliedCouponPercentage = null;
            }

            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Coupons");
            database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean couponFound = false;

                    for (DataSnapshot couponSnapshot : snapshot.getChildren()) {
                        String code = couponSnapshot.child("code").getValue(String.class);

                        if (code != null && code.equals(couponCode)) {
                            couponFound = true;

                            Integer percentage = couponSnapshot.child("percentage").getValue(Integer.class);
                            Integer minQuantity = couponSnapshot.child("minQuantity").getValue(Integer.class);
                            List<Integer> couponCategoryIds = (List<Integer>) couponSnapshot.child("categoryIds").getValue();
                            Integer amountMoney = couponSnapshot.child("amountMoney").getValue(Integer.class);

                            if (percentage == null) {
                                ToastUtils.showToast(CartActivity.this, "Неверный промокод");
                                return;
                            }

                            ArrayList<ItemsModel> cartItems = managmentCart.getListCart();

                            if (minQuantity != null && cartItems.size() < minQuantity) {
                                ToastUtils.showToast(CartActivity.this, "Недостаточно товаров для применения промокода");
                                return;
                            }

                            if (couponCategoryIds != null && !couponCategoryIds.isEmpty()) {
                                for (ItemsModel item : cartItems) {
                                    List<Integer> itemCategoryIds = item.getCategoryIds();
                                    if (itemCategoryIds == null || !containsAny(itemCategoryIds, couponCategoryIds)) {
                                        ToastUtils.showToast(CartActivity.this, "Промокод не применим к одному или нескольким товарам");
                                        return;
                                    }
                                }
                            }

                            if (amountMoney != null) {
                                double totalAmountWithoutDiscount = 0.0;

                                for (ItemsModel item : cartItems) {
                                    totalAmountWithoutDiscount += item.getPrice() * item.getNumberInCart();
                                }

                                Log.d("CartDebug", "Исходная сумма заказа: " + totalAmountWithoutDiscount);
                                Log.d("CartDebug", "Минимальная сумма для промокода: " + amountMoney);

                                if (totalAmountWithoutDiscount < amountMoney) {
                                    amountMoney+=1;
                                    ToastUtils.showToast(CartActivity.this, "Сумма заказа должна быть от " + amountMoney +"₽");
                                    return;
                                }
                            }

                            appliedCouponCode = couponCode;
                            appliedCouponPercentage = percentage;
                            isCouponActivated = true;

                            saveCouponState(couponCode, percentage);

                            double existingSale = calculateExistingSale();
                            applyDiscount(percentage, existingSale);
                            ToastUtils.showToast(CartActivity.this, "Промокод применен");
                            break;
                        }
                    }

                    if (!couponFound) {
                        ToastUtils.showToast(CartActivity.this, "Промокод не найден");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ToastUtils.showToast(CartActivity.this, "Ошибка при проверке промокода");
                }
            });
        }
        private void calculatorCart() {
            double totalWithoutDiscount = 0.0;
            double existingSale = calculateExistingSale();

            ArrayList<ItemsModel> cartItems = managmentCart.getListCart();
            for (ItemsModel item : cartItems) {
                totalWithoutDiscount += item.getPrice() * item.getNumberInCart();
            }


            if (isCouponActivated && appliedCouponPercentage != null) {
                double couponDiscount = totalWithoutDiscount * appliedCouponPercentage / 100;
                existingSale += couponDiscount; // Добавляем скидку от промокода к общей скидке
                totalWithoutDiscount -= couponDiscount; // Вычитаем скидку от промокода из итоговой суммы
            }

            long roundedTotal = Math.round(totalWithoutDiscount);

            binding.totalFeeTxt.setText(String.format("%.2f₽", totalWithoutDiscount + existingSale)); // Исходная сумма без скидок
            binding.totalSaleTxt.setText(String.format("%.2f₽", existingSale)); // Текущая скидка
            binding.totalTxt.setText(roundedTotal + "₽"); // Итоговая сумма
        }
        private boolean containsAny(List<Integer> itemCategoryIds, List<Integer> couponCategoryIds) {
            for (Integer categoryId : itemCategoryIds) {
                if (couponCategoryIds.contains(categoryId)) {
                    return true;
                }
            }
            return false;
        }

        private void saveCouponState(String couponCode, int percentage) {
            SharedPreferences sharedPreferences = getSharedPreferences("CouponPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("couponCode", couponCode);
            editor.putInt("couponPercentage", percentage);
            editor.putBoolean("isCouponActivated", true);
            editor.apply();
        }

        private void setVariable() {
            binding.backBtn.setOnClickListener(v -> {
                finish();
            });

            editTextCoupon = binding.editTextCoupon;
            btnApplyCoupon = binding.btnStartCoupon;
            btnApplyCoupon.setOnClickListener(v -> applyCoupon());
            AppCompatButton clearCouponButton = findViewById(R.id.clearCouponButton);
            clearCouponButton.setOnClickListener(v -> clearCoupon());

            binding.orderBtn.setOnClickListener(v -> {
                if (managmentCart.getListCart().isEmpty()) {
                    ToastUtils.showToast(this, "Корзина пуста");
                    return;
                }

                int selectedAptekaId = getSelectedAptekaId();
                Map<Integer, ApteksModel> aptekaMap = getAptekaMapFromFirebase();
                boolean allItemsAvailable = true;

                for (ItemsModel item : managmentCart.getListCart()) {
                    if (!isItemAvailableInApteka(item, selectedAptekaId, aptekaMap)) {
                        allItemsAvailable = false;
                        break;
                    }
                }

                if (!allItemsAvailable && selectedAptekaId != -1) {
                    ToastUtils.showToast(this, "В выбранной вами аптеке пока нет некоторых товаров, подождите или удалите товары.");
                    return;
                }

                // Уменьшаем количество товара в JSON
                updateItemQuantitiesInFirebase(managmentCart.getListCart(), selectedAptekaId);

                ManagmentOrder managmentOrder = new ManagmentOrder(this);

                String totalStr = binding.totalTxt.getText().toString().replace("₽", "").trim();
                double totalPrice;

                try {
                    totalPrice = Double.parseDouble(totalStr);
                } catch (NumberFormatException e) {
                    return;
                }

                ArrayList<ItemsModel> cartItems = new ArrayList<>(managmentCart.getListCart());
                managmentOrder.createOrder(cartItems, totalPrice);
                ToastUtils.showToast(this, "Заказ оформлен!");
                startActivity(new Intent(CartActivity.this, HistoryOrderActivity.class));
                finish();
            });
        }
        private void updateItemQuantitiesInFirebase(List<ItemsModel> cartItems, int selectedAptekaId) {
            String[] itemNodes = {"ItemsBestS", "ItemsSale", "Others"};

            for (String node : itemNodes) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(node);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.e("FirebaseUpdate", "Ошибка: Узел " + node + " не найден!");
                            return;
                        }

                        for (ItemsModel item : cartItems) {
                            boolean found = false;

                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                String id = itemSnapshot.child("id").getValue(String.class);

                                if (id != null && id.equals(item.getId())) {
                                    found = true;

                                    // Получаем текущий список количеств
                                    Object rawQuantity = itemSnapshot.child("quantity").getValue();
                                    List<Integer> quantities = new ArrayList<>();

                                    if (rawQuantity instanceof List) {
                                        // Преобразуем List<?> в List<Integer>
                                        for (Object obj : (List<?>) rawQuantity) {
                                            if (obj instanceof Long) {
                                                quantities.add(((Long) obj).intValue());
                                            }
                                        }
                                    }

                                    Log.d("FirebaseUpdate", "Узел: " + node + ", Товар ID: " + item.getId() + ", Аптека ID: " + selectedAptekaId);
                                    Log.d("FirebaseUpdate", "Текущий список quantities: " + quantities);

                                    if (!quantities.isEmpty() && selectedAptekaId >= 1 && selectedAptekaId <= quantities.size()) {
                                        int index = selectedAptekaId - 1;
                                        int currentQuantity = quantities.get(index);
                                        int newQuantity = Math.max(0, currentQuantity - item.getNumberInCart());

                                        // Обновляем количество на указанном индексе
                                        quantities.set(index, newQuantity);

                                        // Обновляем данные в Firebase
                                        itemSnapshot.getRef().child("quantity").setValue(quantities)
                                                .addOnSuccessListener(aVoid -> Log.d("FirebaseUpdate", "Обновлено: " + node + " -> " + item.getId() + " -> " + quantities))
                                                .addOnFailureListener(e -> Log.e("FirebaseUpdate", "Ошибка обновления", e));
                                    } else {
                                        Log.e("FirebaseUpdate", "Ошибка: индекс " + (selectedAptekaId - 1) + " вне диапазона. Размер списка: " + quantities.size());
                                    }

                                    break; // Найден товар, выходим из цикла
                                }
                            }

                            if (!found) {
                                Log.e("FirebaseUpdate", "Ошибка: Товар " + item.getId() + " не найден в узле " + node + "!");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseUpdate", "Ошибка Firebase для узла " + node + ": " + error.getMessage());
                    }
                });
            }
        }
        private void clearCoupon() {

            appliedCouponCode = null;
            appliedCouponPercentage = null;
            isCouponActivated = false;


            editTextCoupon.setText("");


            SharedPreferences sharedPreferences = getSharedPreferences("CouponPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("couponCode");
            editor.remove("couponPercentage");
            editor.remove("isCouponActivated");
            editor.apply();


            calculatorCart();


            ToastUtils.showToast(CartActivity.this, "Промокод очищен");
        }
        public void resetCouponSilently() {

            appliedCouponCode = null;
            appliedCouponPercentage = null;
            isCouponActivated = false;

            SharedPreferences sharedPreferences = getSharedPreferences("CouponPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("couponCode");
            editor.remove("couponPercentage");
            editor.remove("isCouponActivated");
            editor.apply();

            calculatorCart();
        }

        @Override
        protected void onResume() {
            super.onResume();

            SharedPreferences sharedPreferences = getSharedPreferences("CouponPrefs", MODE_PRIVATE);
            appliedCouponCode = sharedPreferences.getString("couponCode", null);
            appliedCouponPercentage = sharedPreferences.getInt("couponPercentage", 0);
            isCouponActivated = sharedPreferences.getBoolean("isCouponActivated", false);


            if (binding.cartView.getAdapter() != null) {
                binding.cartView.getAdapter().notifyDataSetChanged();
            }

            if (isCouponActivated && appliedCouponCode != null) {
                Log.d("CouponState", "Промокод восстановлен: " + appliedCouponCode);

                editTextCoupon.setText(appliedCouponCode);

                double existingSale = calculateExistingSale();
                applyDiscount(appliedCouponPercentage, existingSale);
            } else {
                Log.d("CouponState", "Промокод не применен или сброшен");

                calculatorCart();
            }

            NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        }
    }