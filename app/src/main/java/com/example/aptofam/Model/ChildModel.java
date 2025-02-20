package com.example.aptofam.Model;

import java.util.ArrayList;
import java.util.List;

public class ChildModel {
    private String number = "";
    private List<String> names = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void addName(String name) {
        this.names.add(name);
    }

    public void addDescription(String description) {
        this.descriptions.add(description);
    }

    public String getName(int index) {
        return index >= 0 && index < names.size() ? names.get(index) : "";
    }

    public String getDescription(int index) {
        return index >= 0 && index < descriptions.size() ? descriptions.get(index) : "";
    }

    public int getSize() {
        return names.size(); // или descriptions.size(), если они всегда одинаковы
    }
}
