package com.example.aptofam.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private int failedLoginAttempts = 0;
    private static final int MAX_FAILED_ATTEMPTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Проверяем, сохранены ли данные для автоматического входа
        if (!isTaskRoot()) {
            // Если это не корневая активность (например, переход из другой активности), пропускаем автоматический вход
            setVariable();
            return;
        }

        // Проверяем, сохранены ли данные для автоматического входа
        checkSavedCredentials();
        setVariable();
    }

    private void checkSavedCredentials() {
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        if (rememberMe) {
            String email = sharedPreferences.getString(KEY_EMAIL, "");
            String password = sharedPreferences.getString(KEY_PASSWORD, "");

            if (!email.isEmpty() && !password.isEmpty()) {

                loginWithEmail(email, password);
            }
        }
    }

    private void setVariable() {
        binding.goToNext.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextParol.getText().toString().trim();

            if (validateInputs(email, password)) {
                // Сохраняем данные, если CheckBox отмечен
                saveCredentials(email, password, binding.rememberMeCheckBox.isChecked());

                // Выполняем вход
                loginWithEmail(email, password);
            }
        });

        binding.goToSignUp.setOnClickListener(v -> {
            Intent intentToSignUp = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intentToSignUp);
        });

        binding.passowrdAttempts.setOnClickListener(v -> {
            String email = binding.editTextEmail.getText().toString().trim();
            if (email.isEmpty()) {
                ToastUtils.showToast(this, "Введите почту");
            } else {
                sendPasswordResetEmail(email);
            }
        });

    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ToastUtils.showToast(this, "Письмо для восстановления пароля отправлено на " + email);
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else {
                        ToastUtils.showToast(this, "Ошибка при отправке письма: " + task.getException().getMessage());
                    }
                });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            ToastUtils.showToast(this, "Введите почту");
            return false;
        }
        if (password.isEmpty()) {
            ToastUtils.showToast(this, "Введите пароль");
            return false;
        }
        return true;
    }

    private void loginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        ToastUtils.showToast(this, "Вход выполнен успешно");

                        // Получаем токен устройства и сохраняем его
                        getAndSaveFCMToken(user.getUid());

                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        ToastUtils.showToast(this, "Пожалуйста, подтвердите ваш email");
                        mAuth.signOut(); // Выход из системы, если email не подтвержден
                    }
                }
            } else {
                failedLoginAttempts++; // Увеличиваем счетчик неудачных попыток
                if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
                    binding.passowrdAttempts.setVisibility(View.VISIBLE); // Показываем TextView
                }
                String error = task.getException().getMessage();
                String message = error.contains("invalid email") || error.contains("wrong password") ?
                        "Неверный email или пароль" : "Ошибка входа: Неверный email или пароль";
                ToastUtils.showToast(this, message);
            }
        });
    }

    private void getAndSaveFCMToken(String userId) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Token", "Не удалось получить токен устройства", task.getException());
                        return;
                    }

                    // Получаем токен
                    String token = task.getResult();
                    Log.d("Token", "Токен устройства: " + token);

                    // Сохраняем токен в Realtime Database
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId)
                            .child("fcmToken")
                            .setValue(token)
                            .addOnSuccessListener(aVoid -> Log.d("Token", "Токен успешно сохранен"))
                            .addOnFailureListener(e -> Log.e("Token", "Ошибка при сохранении токена", e));
                });
    }

    private void saveCredentials(String email, String password, boolean rememberMe) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (rememberMe) {
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER_ME, true);
        } else {
            editor.remove(KEY_EMAIL);
            editor.remove(KEY_PASSWORD);
            editor.remove(KEY_REMEMBER_ME);
        }
        editor.apply();
    }
}