package com.example.aptofam.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.aptofam.Adapter.QuestionAnswerAdapter;
import com.example.aptofam.Model.QuestionModel;
import com.example.aptofam.R;
import com.example.aptofam.Utility.NavigationScrollvUpDown;
import com.example.aptofam.Utility.NavigationUtility;
import com.example.aptofam.Utility.NoScrollExListView;
import com.example.aptofam.databinding.ActivityQuestionAnswerBinding;

import java.util.ArrayList;
import java.util.List;

public class QuestionAnswerActivity extends BaseActivity {
    ActivityQuestionAnswerBinding binding;
    private List<QuestionModel> questionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionAnswerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavigationUtility.setupBottomNavigation(this, binding.getRoot());
        NavigationScrollvUpDown.setupScrollViewScrollListener(binding.scrollView2, binding.bottomNav);

        setVariable();

        // Инициализация списка вопросов
        questionList = new ArrayList<>();
        questionList.add(new QuestionModel("Как сделать заказ в приложении?", "Выбери товар, добавь в корзину, нажми \"Оформить заказ\" и следуй инструкциям"));
        questionList.add(new QuestionModel("В течении какого времени можно забрать заказ?", "Обычно заказ доступен для самовывоза в течение 2-3 дней. Точное время указано в уведомлении"));
        questionList.add(new QuestionModel("Преимущества заказа в приложении?", "Скидки, бонусы, быстрый доступ к акциям и удобное оформление заказов"));
        questionList.add(new QuestionModel("Как много можно использовать промокод?", "Промокод можно применить один в оформлении заказа, но они неограничены в одной покупке, можно этот же прокод применить в последующей покупке"));
        questionList.add(new QuestionModel("Где взять промокод?", "Промокод можно взять в промокдах в профиле, нжать на него и он автоматически скопируется в буфер обмена"));
        questionList.add(new QuestionModel("Чем отличается хиты продаж от акции?", "Хиты продаж — это популярные товары, а акции — временные предложения со скидками или специальными условиями"));
        questionList.add(new QuestionModel("Что означает серая кнопка для добавлении в корзину?", "Она означает что товар на данном моменте находится в количестве 0 в выбранной аптеке"));
        // Настройка адаптера
        QuestionAnswerAdapter adapter = new QuestionAnswerAdapter(this, questionList);
        binding.expandableQSListView.setAdapter(adapter);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        View decor = window.getDecorView();
        decor.setSystemUiVisibility(0);
    }
    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> {
            finish();
        });
    }
}
