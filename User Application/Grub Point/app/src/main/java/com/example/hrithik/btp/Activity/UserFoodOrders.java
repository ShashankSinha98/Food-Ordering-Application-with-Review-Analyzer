package com.example.hrithik.btp.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hrithik.btp.Adapters.UserOrderAdapter;
import com.example.hrithik.btp.Helper.FoodOrders;
import com.example.hrithik.btp.Model.FoodQty;
import com.example.hrithik.btp.Model.Order;
import com.example.hrithik.btp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UserFoodOrders extends AppCompatActivity {

    private static final String TAG = "UserFoodOrders";
    private ImageView orderBackIcon;
    public static ProgressDialog fetching_progress;

    private static  FirebaseAuth mAuth;
    private static FirebaseFirestore firebaseFirestore;
    private static RecyclerView orderRecyclerView;
    private static UserOrderAdapter userOrderAdapter;

    private static String userId;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_food_orders);

        orderBackIcon = findViewById(R.id.back_arrow_order);
        orderRecyclerView = findViewById(R.id.order_rv);

        orderBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fetching_progress = new ProgressDialog(this);



        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        userId = mAuth.getCurrentUser().getUid();
        mContext = UserFoodOrders.this;

        getOrdersFromServer();

    }

    public static void getOrdersFromServer() {
        Log.d(TAG,"Getting data");

        fetching_progress.setTitle("Fetching Data");
        fetching_progress.setMessage("Please wait while we fetch the data from server!");
        fetching_progress.setCanceledOnTouchOutside(false);
        fetching_progress.setCancelable(false);
        fetching_progress.show();


        FoodOrders foodOrders = FoodOrders.getInstance();
        foodOrders.mainOrderArrayList.clear();


        try {
            firebaseFirestore.collection("Users").document(userId).collection("Orders")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {

                        ArrayList<Order> orderArrayList = new ArrayList<>();
                        String custName, custNumber, orderAmtPaid, custImage, orderTimestamp, custID, orderStatus, orderDocID;

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {

                            ArrayList<String> orderDetails = (ArrayList<String>) documentSnapshot.get("orderDetails");
                            Log.d(TAG, "ID: "+documentSnapshot.getId()+", Order Details: "+orderDetails.size());

                            ArrayList<FoodQty> foodQtyList = new ArrayList<>();

                            custName = documentSnapshot.getString("name");
                            custNumber = documentSnapshot.getString("number");
                            orderAmtPaid = String.valueOf((Long)documentSnapshot.get("amount_paid"));
                            custImage = documentSnapshot.getString("customer_thumb_image");
                            orderTimestamp = String.valueOf(documentSnapshot.get("timestamp"));
                            custID = documentSnapshot.getString("uid");
                            orderStatus = documentSnapshot.getString("order_status");
                            orderDocID = documentSnapshot.getId();




                            for(int i=0; i<orderDetails.size(); i++){
                                String[] orderData = orderDetails.get(i).split("#");

                                String foodName=null, foodQty=null, foodPrice=null, foodID=null, foodCuisineCode=null, foodImage=null, foodComment=null, foodDocId = null, foodResp = null;

                                for(int j=1; j<orderData.length; j++){

                                    Log.d(TAG, "Order Data : "+j+" : "+orderData[j]);
                                    String[] temp = orderData[j].split(":");
                                    //Log.d(TAG, "Data: "+temp[1].trim());

                                    switch (j){
                                        case 1: foodName = temp[1].trim();
                                            break;
                                        case 2: foodQty =  temp[1].trim();
                                            break;
                                        case 3: foodPrice =  temp[1].trim();
                                            break;
                                        case 4: foodID =  temp[1].trim();
                                            break;
                                        case 5: foodCuisineCode =  temp[1].trim();
                                            break;
                                        case 6: foodImage =  orderData[j].substring(orderData[j].indexOf(":")+1).trim();
                                            break;
                                        case 7: foodDocId = temp[1].trim();
                                            break;
                                        case 8: foodComment = temp[1].trim();
                                            break;
                                        case 9: foodResp = temp[1].trim();
                                            break;


                                    }
                                }

                                foodQtyList.add(new FoodQty(foodName, foodQty, foodPrice, foodID, foodCuisineCode, foodImage,foodComment, foodDocId, orderDocID, foodResp));
                                Log.d(TAG, foodName+", "+foodImage);

                                foodName=null; foodQty=null; foodPrice=null; foodID=null; foodCuisineCode=null; foodImage=null;
                            }

                            orderArrayList.add(new Order(custName,custNumber,custID,custImage,orderAmtPaid,orderTimestamp,orderStatus,foodQtyList));

                        }

                        FoodOrders foodOrders = FoodOrders.getInstance();
                        foodOrders.mainOrderArrayList = orderArrayList;


                        // Populating List
                        userOrderAdapter = new UserOrderAdapter(mContext, foodOrders.mainOrderArrayList);
                        LinearLayoutManager orderMananger = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                        orderRecyclerView.setLayoutManager(orderMananger);
                        orderRecyclerView.setAdapter(userOrderAdapter);



                         Log.d(TAG, "Main list size2: "+foodOrders.mainOrderArrayList.size());


                        fetching_progress.dismiss();
                        Toast.makeText(mContext, task.getResult().size()+" Data fetched. Updating list...", Toast.LENGTH_SHORT).show();


                    } else {
                        fetching_progress.dismiss();
                        Toast.makeText(mContext, "Something is wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Something is wrong");
                    }

                }

            });

        } catch (Exception e){
            fetching_progress.dismiss();
            Toast.makeText(mContext, "Error Occured", Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.getMessage());
        }


    }

}
