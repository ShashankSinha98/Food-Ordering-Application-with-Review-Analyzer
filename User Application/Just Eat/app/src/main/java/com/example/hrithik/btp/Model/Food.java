package com.example.hrithik.btp.Model;

import java.io.Serializable;

public class Food implements Serializable {

    private String foodImage, foodDocID;
    private String foodCode, foodName, foodDesc, foodPrice, foodCuisineCode;
    private String foodTotalReviewCount, foodScore, foodPositiveReviewCount;


    public String getFoodDocID() {
        return foodDocID;
    }

    public void setFoodDocID(String foodDocID) {
        this.foodDocID = foodDocID;
    }


    public Food(String foodCode, String foodImage, String foodName, String foodDesc, String foodPrice, String foodCuisineCode, String foodTotalReviewCount, String foodScore, String foodPositiveReviewCount, String foodDocID) {
        this.foodDocID = foodDocID;
        this.foodCode = foodCode;
        this.foodImage = foodImage;
        this.foodTotalReviewCount = foodTotalReviewCount;
        this.foodScore = foodScore;
        this.foodPositiveReviewCount = foodPositiveReviewCount;
        this.foodName = foodName;
        this.foodDesc = foodDesc;
        this.foodPrice = foodPrice;
        this.foodCuisineCode = foodCuisineCode;
    }

    public String getFoodTotalReviewCount() {
        return foodTotalReviewCount;
    }

    public String getFoodScore() {
        return foodScore;
    }

    public String getFoodPositiveReviewCount() {
        return foodPositiveReviewCount;
    }

    public Food(){}

    public String getFoodCode() {
        return foodCode;
    }

    public String getFoodCuisineCode() {
        return foodCuisineCode;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodDesc() {
        return foodDesc;
    }


    public String getFoodPrice() {
        return foodPrice;
    }
}
