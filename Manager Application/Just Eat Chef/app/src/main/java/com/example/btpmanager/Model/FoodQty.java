package com.example.btpmanager.Model;

public class FoodQty {

    private String foodName, foodQty, foodPrice, foodID, foodCuisineCode, foodImage;

    public FoodQty(String foodName, String foodQty, String foodPrice, String foodID, String foodCuisineCode, String foodImage) {
        this.foodName = foodName;
        this.foodQty = foodQty;
        this.foodPrice = foodPrice;
        this.foodID = foodID;
        this.foodCuisineCode = foodCuisineCode;
        this.foodImage = foodImage;
    }



    public String getFoodName() {
        return foodName;
    }

    public String getFoodQty() {
        return foodQty;
    }

    public String getFoodPrice() {
        return foodPrice;
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
