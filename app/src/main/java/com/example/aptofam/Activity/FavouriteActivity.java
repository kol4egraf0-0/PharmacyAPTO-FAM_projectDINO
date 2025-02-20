        package com.example.aptofam.Activity;

        import android.os.Bundle;
        import android.util.Log;

        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.example.aptofam.Adapter.FavoriteAdapter;
        import com.example.aptofam.Helper.ManagmentCart;
        import com.example.aptofam.Helper.ManagmentFavorite;
        import com.example.aptofam.Model.ItemsModel;
        import com.example.aptofam.Utility.FavoriteManager;
        import com.example.aptofam.Utility.NavigationRecyclerUpDown;
        import com.example.aptofam.Utility.NavigationUtility;
        import com.example.aptofam.databinding.ActivityFavouriteBinding;

        import java.util.ArrayList;
        import java.util.List;

        public class FavouriteActivity extends BaseActivity {
            private ActivityFavouriteBinding binding;
            private RecyclerView recyclerView;
            private FavoriteAdapter favoriteAdapter;
            private ArrayList<ItemsModel> favoriteItemsList;
            private FavoriteManager favoriteManager;
            private ManagmentFavorite managmentFavorite;


            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                binding = ActivityFavouriteBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());

                managmentFavorite = new ManagmentFavorite(this);

                setVariable();

                initFavoriteList();


                NavigationUtility.setupBottomNavigation(this, binding.getRoot());


            }
            private void initFavoriteList() {
                ManagmentCart managmentCart = new ManagmentCart(this);
                int numberOrder = 1;

                // Получаем список избранного
                favoriteItemsList = managmentFavorite.getFavoriteList();
                if (favoriteItemsList == null) {
                    favoriteItemsList = new ArrayList<>(); // Инициализируем пустым списком, если null
                }

                binding.favouriteView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                favoriteAdapter = new FavoriteAdapter(favoriteItemsList, this, favoriteManager, managmentFavorite, managmentCart, numberOrder);
                binding.favouriteView.setAdapter(favoriteAdapter);
            }



            private void setVariable() {
                binding.backBtn.setOnClickListener(v -> {
                    finish();
                });
            }
            @Override
            protected void onResume() {
                super.onResume();


                if (binding.favouriteView.getAdapter() != null) {
                    binding.favouriteView.getAdapter().notifyDataSetChanged();
                }
                NavigationUtility.setupBottomNavigation(this, binding.getRoot());
            }


        }