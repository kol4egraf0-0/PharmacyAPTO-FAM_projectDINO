package com.example.aptofam.Model;

public class SliderModel {
    private String url;
    private String sliderDescription;
    private int sliderId;

    public SliderModel(){
     }
    public SliderModel(String url, int sliderId, String sliderDescription) {
        this.url = url;
        this.sliderId = sliderId;
        this.sliderDescription = sliderDescription;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSliderDescription() {
        return sliderDescription;
    }

    public void setSliderDescription(String sliderDescription) {
        this.sliderDescription = sliderDescription;
    }

    public int getSliderId() {
        return sliderId;
    }

    public void setSliderId(int sliderId) {
        this.sliderId = sliderId;
    }
}
