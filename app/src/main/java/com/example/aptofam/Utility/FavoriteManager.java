    package com.example.aptofam.Utility;

    import java.util.HashSet;
    import java.util.Set;
//Менеджер для Избранного для отображения картнки
    public class FavoriteManager {
        private static FavoriteManager instance;
        private Set<String> favoriteItems;
        private FavoriteManager() {
            favoriteItems = new HashSet<>();
        }
        public static synchronized FavoriteManager getInstance() {
            if (instance == null) {
                instance = new FavoriteManager();
            }
            return instance;
        }

        public void addToFavorites(String itemId) {
            favoriteItems.add(itemId);
        }

        public void removeFromFavorites(String itemId) {
            favoriteItems.remove(itemId);
        }

        public boolean isFavorite(String itemId) {
            return favoriteItems.contains(itemId);
        }

    }
