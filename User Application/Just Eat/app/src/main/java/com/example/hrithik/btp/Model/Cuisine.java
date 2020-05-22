package com.example.hrithik.btp.Model;

public class Cuisine {

    private String cuisineCode, cuisineName;

    public Cuisine(String cuisineCode, String cuisineName) {
        this.cuisineCode = cuisineCode;
        this.cuisineName = cuisineName;
    }

    public String getCuisineName() {
        return cuisineName;
    }

    public String getCuisineCode() {
        return cuisineCode;
    }

    public void setCuisineCode(String cuisineCode) {
        this.cuisineCode = cuisineCode;
    }

    public void setCuisineName(String cuisineName) {
        this.cuisineName = cuisineName;
    }
}
