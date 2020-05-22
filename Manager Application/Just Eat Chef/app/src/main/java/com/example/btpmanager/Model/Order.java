package com.example.btpmanager.Model;

import java.util.ArrayList;

public class Order {

    private String customerName, customerNumber, customerID, customerImage, amountPaid, timestamp,orderStatus, docId, custOrderDocID;
    private ArrayList<FoodQty> ordersList;

    public Order(String customerName, String customerNumber, String customerID, String customerImage, String amountPaid, String timestamp, String orderStatus, ArrayList<FoodQty> ordersList, String docId, String custOrderDocID) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.customerID = customerID;
        this.customerImage = customerImage;
        this.amountPaid = amountPaid;
        this.timestamp = timestamp;
        this.orderStatus = orderStatus;
        this.ordersList = ordersList;
        this.custOrderDocID = custOrderDocID;
        this.docId = docId;
    }

    public String getCustOrderDocID() {
        return custOrderDocID;
    }

    public String getDocId() {
        return docId;
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
