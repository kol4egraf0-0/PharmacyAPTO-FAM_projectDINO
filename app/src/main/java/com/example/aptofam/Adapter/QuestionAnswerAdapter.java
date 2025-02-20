package com.example.aptofam.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.aptofam.Model.QuestionModel;
import com.example.aptofam.R;

import java.util.List;

public class QuestionAnswerAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<QuestionModel> questionList;

    public QuestionAnswerAdapter(Context context, List<QuestionModel> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @Override
    public int getGroupCount() {
        return questionList.size(); // Количество групп (вопросов)
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1; // Каждая группа (вопрос) имеет только один дочерний элемент (ответ)
    }

    @Override
    public Object getGroup(int groupPosition) {
        return questionList.get(groupPosition).getQuestion(); // Возвращает вопрос
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return questionList.get(groupPosition).getAnswer(); // Возвращает ответ
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition; // ID группы
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition; // ID дочернего элемента
    }

    @Override
    public boolean hasStableIds() {
        return false; // ID не стабильны
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group_qs, null);
        }

        TextView questionTextView = convertView.findViewById(R.id.questionTextView);
        questionTextView.setText((String) getGroup(groupPosition));

        // Меняем цвет текста в зависимости от состояния (открыт/закрыт)
        if (isExpanded) {
            questionTextView.setTextColor(ContextCompat.getColor(context, R.color.blue)); // Синий цвет при открытии
        } else {
            questionTextView.setTextColor(ContextCompat.getColor(context, R.color.black)); // Чёрный цвет при закрытии
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        // Создаем view для дочернего элемента (ответа)
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_qs, null); // Используем макет для дочернего элемента
        }

        TextView answerTextView = convertView.findViewById(R.id.answerTextView);
        answerTextView.setText((String) getChild(groupPosition, childPosition)); // Устанавливаем текст ответа

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true; // Дочерние элементы можно выбирать
    }
}