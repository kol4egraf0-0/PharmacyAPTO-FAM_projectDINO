package com.example.aptofam.Model;

import java.util.List;

public class CouponModel {
    private String code;
    private String discount;
    private String validUntil;
    private String description;
    private Integer percentage; // Процент скидки
    private Integer minQuantity; // Минимальное количество товаров
    private List<Integer> categoryIds; // Список категорий, к которым применим промокод
    private Integer amountMoney;

    // Пустой конструктор необходим для Firebase
    public CouponModel() {
    }

    public CouponModel(String code, String discount, String validUntil, String description, Integer percentage, Integer minQuantity, List<Integer> categoryIds, Integer amountMoney) {
        this.code = code;
        this.discount = discount;
        this.validUntil = validUntil;
        this.description = description;
        this.percentage = percentage;
        this.minQuantity = minQuantity;
        this.categoryIds = categoryIds;
        this.amountMoney = amountMoney;
    }

    // Геттеры и сеттеры
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPercentage() {
        return percentage;
    }

    public void setPercentage(Integer percentage) {
        this.percentage = percentage;
    }

    public Integer getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(Integer minQuantity) {
        this.minQuantity = minQuantity;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Integer getAmountMoney() {
        return amountMoney;
    }

    public void setAmountMoney(Integer amountMoney) {
        this.amountMoney = amountMoney;
    }
}