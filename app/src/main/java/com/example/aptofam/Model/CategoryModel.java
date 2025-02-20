package com.example.aptofam.Model;

import java.util.List;

public class CategoryModel {
    private int categoryId;
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    private String categoryName;
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    private int sliderOrder;

    public int getSliderOrder() {
        return sliderOrder;
    }

    public void setSliderOrder(int sliderOrder) {
        this.sliderOrder = sliderOrder;
    }
}
