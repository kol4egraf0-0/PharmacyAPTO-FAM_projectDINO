package com.example.aptofam.Model;

import java.util.ArrayList;

public class ParentModel {
    private String name;
    private ArrayList<ChildModel> itemList = new ArrayList<ChildModel>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ChildModel> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<ChildModel> itemList) {
        this.itemList = itemList;
    }
}
