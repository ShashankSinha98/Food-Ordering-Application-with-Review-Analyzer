package com.example.hrithik.btp.Model;

import java.io.Serializable;

public class FoodQty implements Serializable {

    private String foodName, foodQty, foodPrice, foodID, foodCuisineCode, foodImage, foodcomment, foodDocId, orderDocId, foodResp;

    public FoodQty(String foodName, String foodQty, String foodPrice, String foodID, String foodCuisineCode, String foodImage, String foodcomment, String foodDocId, String orderDocId, String foodResp) {
        this.foodResp = foodResp;
        this.foodName = foodName;
        this.orderDocId = orderDocId;
        this.foodQty = foodQty;
        this.foodPrice = foodPrice;
        this.foodID = foodID;
        this.foodDocId = foodDocId;
        this.foodCuisineCode = foodCuisineCode;
        this.foodImage = foodImage;
        this.foodcomment = foodcomment;
    }

    public void setFoodcomment(String foodcomment) {
        this.foodcomment = foodcomment;
    }

    public String getOrderDocId() {
        return orderDocId;
    }

    public void setOrderDocId(String orderDocId) {
        this.orderDocId = orderDocId;
    }

    public String getFoodcomment() {
        return foodcomment;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodResp() {
        return foodResp;
    }

    public String getFoodQty() {
        return foodQty;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public String getFoodDocId() {
        return foodDocId;
    }

    public void setFoodDocId(String foodDocId) {
        this.foodDocId = foodDocId;
    }

    public String getFoodID() {
        return foodID;
    }

    public String getFoodCuisineCode() {
        return foodCuisineCode;
    }

    public String getFoodImage() {
        return foodImage;
    }
}
