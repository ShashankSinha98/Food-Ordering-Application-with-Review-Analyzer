package com.example.hrithik.btp.Helper;

import com.example.hrithik.btp.Model.Order;

import java.util.ArrayList;

public class FoodOrders {

    private static FoodOrders foodOrders = null;

    public ArrayList<Order> mainOrderArrayList;

    private FoodOrders(){
        mainOrderArrayList = new ArrayList<>();
    }

    public static FoodOrders getInstance(){

        if(foodOrders == null)
            foodOrders = new FoodOrders();

        return  foodOrders;

    }

}
