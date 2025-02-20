package com.example.aptofam.Model;

public class ProfileButtonModel {
    private String text;
    private int iconResId;
    private Class<?> targetActivity;

    public ProfileButtonModel(String text, int iconResId, Class<?> targetActivity) {
        this.text = text;
        this.iconResId = iconResId;
        this.targetActivity = targetActivity;
    }

    public String getText() {
        return text;
    }

    public int getIconResId() {
        return iconResId;
    }

    public Class<?> getTargetActivity() {
        return targetActivity;
    }
}
