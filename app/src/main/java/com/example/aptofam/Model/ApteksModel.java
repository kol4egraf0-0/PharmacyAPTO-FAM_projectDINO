package com.example.aptofam.Model;

public class ApteksModel {
    private int aptekaId;
    private String aptekaName;
    private double latitude;
    private double longitude;

    public ApteksModel(){}


    public ApteksModel(int aptekaId, String aptekaName, double latitude, double longitude) {
        this.aptekaId = aptekaId;
        this.aptekaName = aptekaName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public int getAptekaId() {
        return aptekaId;
    }

    public void setAptekaId(int aptekaId) {
        this.aptekaId = aptekaId;
    }

    public String getAptekaName() {
        return aptekaName;
    }

    public void setAptekaName(String aptekaName) {
        this.aptekaName = aptekaName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}