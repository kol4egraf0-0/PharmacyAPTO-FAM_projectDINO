package com.example.aptofam.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemsModel implements Serializable {
    private String title;
    private ArrayList<String> picUrl;
    private double price;
    private double priceSale;
    private String priceSaleStrStrike;
    private int numberInCart;
    private String id;
    private Map<String, String> characteristics;
    private List<String> keywords;
    private List<Integer> categoryIds;
    private Integer milligrams;
    private List<Integer> aptekaIds;
    private Integer ageUsage;
    private List<Integer> quantity;

    public List<Integer> getQuantity() {
        return quantity;
    }

    public void setQuantity(List<Integer> quantity) {
        this.quantity = quantity;
    }

    public Integer getAgeUsage() {
        return ageUsage;
    }

    public void setAgeUsage(Integer ageUsage) {
        this.ageUsage = ageUsage;
    }

    public List<Integer> getAptekaIds() {
        return aptekaIds;
    }

    public void setAptekaIds(List<Integer> aptekaIds) {
        this.aptekaIds = aptekaIds;
    }

    public Integer getMilligrams() {
        return milligrams;
    }

    public void setMilligrams(Integer milligrams) {
        this.milligrams = milligrams;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Map<String, String> getCharacteristics() {
        return characteristics;
    }
    public void setCharacteristics(Map<String, String> characteristics) {
        this.characteristics = characteristics;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(ArrayList<String> picUrl) {
        this.picUrl = picUrl;
    }

    public double getPriceSale() {
        return priceSale;
    }

    public void setPriceSale(double priceSale) {
        this.priceSale = priceSale;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getNumberInCart() {
        return Math.max(0, numberInCart);
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public String getPriceSaleStrStrike() {
        return priceSaleStrStrike;
    }
    public void setPriceSaleStrStrike(String priceSaleStr) {
        this.priceSaleStrStrike = priceSaleStr;
    }
    public ItemsModel() {
    }
}
