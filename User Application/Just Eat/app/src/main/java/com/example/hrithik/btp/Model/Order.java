package com.example.hrithik.btp.Model;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {

    private String customerName, customerNumber, customerID, customerImage, amountPaid, timestamp,orderStatus, orderDocId;
    private ArrayList<FoodQty> ordersList;

    public Order(String customerName, String customerNumber, String customerID, String customerImage, String amountPaid, String timestamp, String orderStatus, ArrayList<FoodQty> ordersList) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.customerID = customerID;
        this.customerImage = customerImage;
        this.amountPaid = amountPaid;
        this.timestamp = timestamp;
        this.orderStatus = orderStatus;
        this.ordersList = ordersList;
    }

    public String getOrderDocId() {
        return orderDocId;
    }

    public void setOrderDocId(String orderDocId) {
        this.orderDocId = orderDocId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getCustomerImage() {
        return customerImage;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public ArrayList<FoodQty> getOrdersList() {
        return ordersList;
    }
}
