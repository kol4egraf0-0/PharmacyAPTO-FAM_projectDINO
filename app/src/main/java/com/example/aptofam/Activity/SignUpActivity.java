package com.example.aptofam.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.example.aptofam.Utility.ToastUtils;
import com.example.aptofam.databinding.ActivitySignupBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setVariable();
    }

    private void setVariable() {
        binding.registrateBtn.setOnClickListener(v -> {
            String name = binding.editTextName.getText().toString().trim();
            String email = binding.editTextEmail.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();
            String repeatPassword = binding.editTextRepeatPassword.getText().toString().trim();
            boolean isPrivacyPolicyAccepted = binding.checkBoxPrivacyPolicy.isChecked();

            if (validateInputs(name, email, password, repeatPassword, isPrivacyPolicyAccepted)) {
                registerWithEmail(email, password, name);
            }
        });
    }

    private boolean validateInputs(String name, String email, String password, String repeatPassword, boolean isPrivacyPolicyAccepted) {
        if (name.isEmpty()) {
            ToastUtils.showToast(this, "Введите имя");
            return false;
        }
        if (!name.matches("[А-Яа-я\\-]+")) {
            ToastUtils.showToast(this, "Имя должно быть на русском");
            return false;
        }
        if (password.isEmpty()) {
            ToastUtils.showToast(this, "Введите пароль");
            return false;
        }
        if (password.length() < 8) {
            ToastUtils.showToast(this, "Пароль должен содержать не менее 8 символов");
            return false;
        }
        if (!password.matches(".*[A-ZА-Я].*")) { // Разрешаем заглавные латинские и русские буквы
            ToastUtils.showToast(this, "Пароль должен содержать хотя бы одну заглавную букву");
            return false;
        }
        if (!password.matches(".*[a-zа-я].*")) { // Разрешаем строчные латинские и русские буквы
            ToastUtils.showToast(this, "Пароль должен содержать хотя бы одну строчную букву");
            return false;
        }
        if (!password.matches(".*\\d.*")) {
            ToastUtils.showToast(this, "Пароль должен содержать хотя бы одну цифру");
            return false;
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            ToastUtils.showToast(this, "Пароль должен содержать хотя бы один специальный символ, например какой то из этих !@#$%^&*()_+-=[]{};':\"\\|,.<>/?");
            return false;
        }
        if (repeatPassword.isEmpty()) {
            ToastUtils.showToast(this, "Введите пароль повторно");
            return false;
        }
        if (!password.equals(repeatPassword)) {
            ToastUtils.showToast(this, "Пароли не совпадают");
            return false;
        }
        if (email.isEmpty()) {
            ToastUtils.showToast(this, "Введите почту");
            return false;
        }
        if (!isPrivacyPolicyAccepted) {
            ToastUtils.showToast(this, "Примите Политику конфиденциальности");
            return false;
        }
        return true;
    }

    private void registerWithEmail(String email, String password, String name) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            saveUserDataToRealtimeDatabase(user.getUid(), name, email);
                                            sendEmailVerification(user);
                                        }
                                    });
                        }
                    } else {
                        String error = task.getException().getMessage();
                        String message = error.contains("email already in use") ?
                                "Email уже занят" : "Ошибка регистрации: Данный email уже используется!";
                        ToastUtils.showToast(this, message);
                    }
                });
    }

    private void saveUserDataToRealtimeDatabase(String userId, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);

        mDatabase.child("Users").child(userId).setValue(user)
                .addOnFailureListener(e -> {
                    ToastUtils.showToast(this, "Ошибка при сохранении данных: " + e.getMessage()+ ", повторите попытку");
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ToastUtils.showToast(this, "Письмо с подтверждением отправлено на " + user.getEmail());
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    } else {
                        ToastUtils.showToast(this, "Не удалось отправить письмо с подтверждением: " + task.getException().getMessage()+", повторите попытку");
                    }
                });
    }
}