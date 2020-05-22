package com.example.hrithik.btp.Model;

public class FoodCartQtyHelper {

    private Food food;
    private int quantity;

    public FoodCartQtyHelper(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
