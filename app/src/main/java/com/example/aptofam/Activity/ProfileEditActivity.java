package com.example.aptofam.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.aptofam.Model.ApteksModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivityProfileBinding;
import com.example.aptofam.databinding.ActivityProfileEditBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileEditActivity extends BaseActivity {
    private ActivityProfileEditBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private int selectedAptekaId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        loadUserData();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("selected_apteka_id")) {
            selectedAptekaId = intent.getIntExtra("selected_apteka_id", -1);
            saveAptekaIdToFirebase(selectedAptekaId);
            loadAptekaNameFromFirebase(selectedAptekaId);
        } else {
            loadAptekaIdFromFirebase();
        }
        setVariable();
        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            binding.emailInput.setText(user.getEmail());
            String userId = user.getUid();
            mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        binding.nameInput.setText(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ToastUtils.showToast(ProfileEditActivity.this, "Ошибка при загрузке данных: " + error.getMessage());
                }
            });
        }
    }

    private void setVariable() {
        binding.btnPlacePharmacy.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, MapApteksActivity.class);
            startActivity(intent);
        });
        binding.deleteProfileBtn.setOnClickListener(v -> {
            deleteUserProfile();
        });
        binding.passwordInput.setOnClickListener(v -> {
            changePassword();
        });
    }

    private void changePassword() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                ToastUtils.showToast(ProfileEditActivity.this, "Письмо отправлено на " + email);
                                startActivity(new Intent(ProfileEditActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                ToastUtils.showToast(ProfileEditActivity.this, "Ошибка при отправке письма: " + task.getException().getMessage());
                            }
                        });
            }
        }
    }

    private void deleteUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child("Users").child(userId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        user.delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        ToastUtils.showToast(ProfileEditActivity.this, "Профиль успешно удален");
                                        startActivity(new Intent(ProfileEditActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        ToastUtils.showToast(ProfileEditActivity.this, "Ошибка при удалении профиля: " + task.getException().getMessage());
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        ToastUtils.showToast(ProfileEditActivity.this, "Ошибка при удалении данных: " + e.getMessage());
                    });
        }
    }

    private void saveAptekaIdToFirebase(int aptekaId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.child("aptekaId").setValue(aptekaId)
                    .addOnSuccessListener(aVoid -> {
                        ToastUtils.showToast(ProfileEditActivity.this, "Аптека успешно сохранена");
                    })
                    .addOnFailureListener(e -> {
                        ToastUtils.showToast(ProfileEditActivity.this, "Ошибка при сохранении ID аптеки: " + e.getMessage());
                    });
        }
    }

    private void loadAptekaIdFromFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("aptekaId");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        selectedAptekaId = snapshot.getValue(Integer.class);
                        loadAptekaNameFromFirebase(selectedAptekaId);
                    } else {
                        binding.textPlacePharmacy.setText("Местоположение"); // Значение по умолчанию
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    ToastUtils.showToast(ProfileEditActivity.this, "Ошибка при загрузке аптеки: " + error.getMessage());
                }
            });
        }
    }

    private void loadAptekaNameFromFirebase(int aptekaId) {
        DatabaseReference apteksRef = FirebaseDatabase.getInstance().getReference("Apteks").child(String.valueOf(aptekaId));
        apteksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ApteksModel apteka = snapshot.getValue(ApteksModel.class);
                    if (apteka != null) {
                        binding.textPlacePharmacy.setText(apteka.getAptekaName());
                    }
                } else {
                    binding.textPlacePharmacy.setText("Выберете местоположение");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                ToastUtils.showToast(ProfileEditActivity.this, "Ошибка при загрузке названия аптеки: " + error.getMessage());
            }
        });
    }

    private void saveSelectedAptekaId(int aptekaId) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfilePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedAptekaId", aptekaId);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileEditActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        if (selectedAptekaId != -1) {
            saveSelectedAptekaId(selectedAptekaId);
        }
    }
}