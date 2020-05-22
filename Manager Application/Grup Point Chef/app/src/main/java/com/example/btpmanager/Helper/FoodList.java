package com.example.btpmanager.Helper;

import com.example.btpmanager.Model.FoodQty;
import com.example.btpmanager.Model.Order;

import java.util.ArrayList;

public class FoodList {

    private static FoodList foodList = null;

    public ArrayList<FoodQty> bottomFoodList;

    private FoodList(){
        bottomFoodList = new ArrayList<>();
    }

    public static FoodList getInstance(){

        if(foodList == null)
            foodList = new FoodList();

        return  foodList;

    }

}
