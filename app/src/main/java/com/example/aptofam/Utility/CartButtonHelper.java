package com.example.aptofam.Utility;

import android.content.Context;
import android.widget.ImageView;
import com.example.aptofam.Helper.ManagmentCart;
import com.example.aptofam.Model.ItemsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
//Для ViewHolder'ов чтоб пользователь не мог добавлятть в карт если кол-во = 0 в опр аптеке
public class CartButtonHelper {

    public static void setupCartButton(Context context, ImageView addToCartBtn, ItemsModel item, int numberOrder, ManagmentCart managmentCart) {
        getUserAptekaId(aptekaId -> {
            List<Integer> quantityList = item.getQuantity();

            if (quantityList != null && aptekaId >= 1 && aptekaId <= quantityList.size()) {
                int stockForUserApteka = quantityList.get(aptekaId - 1);

                if (stockForUserApteka > 0) {
                    enableAddToCart(context, addToCartBtn, item, numberOrder, managmentCart);
                } else {
                    disableAddToCart(context, addToCartBtn);
                }
            } else {
                disableAddToCart(context, addToCartBtn);
            }
        });
    }

    private static void disableAddToCart(Context context, ImageView imageView) {
        imageView.setEnabled(false);
        imageView.setAlpha(0.5f);
        imageView.setOnClickListener(null);
    }

    private static void enableAddToCart(Context context, ImageView imageView, ItemsModel item, int numberOrder, ManagmentCart managmentCart) {
        imageView.setEnabled(true);
        imageView.setAlpha(1.0f);

        imageView.setOnClickListener(v -> {
            item.setNumberInCart(numberOrder);
            managmentCart.insertItems(item);
        });
    }

    private static void getUserAptekaId(OnAptekaIdReceivedListener listener) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            listener.onReceived(-1);
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("aptekaId");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listener.onReceived(snapshot.getValue(Integer.class));
                } else {
                    listener.onReceived(-1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onReceived(-1);
            }
        });
    }

    private interface OnAptekaIdReceivedListener {
        void onReceived(int aptekaId);
    }
}
