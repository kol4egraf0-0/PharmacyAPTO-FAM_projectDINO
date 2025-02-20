package com.example.aptofam.Model;

public class QuestionModel {
    private String question;
    private String answer;

    public QuestionModel(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
