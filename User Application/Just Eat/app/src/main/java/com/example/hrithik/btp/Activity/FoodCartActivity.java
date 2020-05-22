package com.example.hrithik.btp.Activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hrithik.btp.Adapters.CartAdapter;
import com.example.hrithik.btp.Helper.FoodCart;
import com.example.hrithik.btp.Model.Food;
import com.example.hrithik.btp.Model.FoodCartQtyHelper;
import com.example.hrithik.btp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import me.dm7.barcodescanner.core.IViewFinder;

public class FoodCartActivity extends AppCompatActivity {

    private static final String TAG = "FoodCartActivity";
    private static ArrayList<FoodCartQtyHelper> cartList;
    private CartAdapter cartAdapter;
    private RecyclerView cartRecyclerView;
    private static TextView totalItemsTV;
    private static TextView totalCartAmountTV;
    private ImageView cartBackIcon;
    private Button checkoutBtn;
    private FirebaseAuth mAuth;
    private ConstraintLayout constraintLayout;


    private static String amountToPay = "";


    private String orderID = "", custID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_cart);

        cartRecyclerView = findViewById(R.id.food_cart_recycler_view);
        totalCartAmountTV = findViewById(R.id.total_cart_amount_tv);
        totalItemsTV = findViewById(R.id.cart_total_qty_tv);
        cartBackIcon = findViewById(R.id.back_arrow_cart);
        checkoutBtn = findViewById(R.id.food_cart_checkout_btn);
        constraintLayout = findViewById(R.id.food_cart_constraint_layout);


        FoodCart cart = FoodCart.getInstance();
        if(cart.foodCartList.isEmpty()){
            checkoutBtn.setAlpha(0.5f);
            checkoutBtn.setEnabled(false);
        } else {
            checkoutBtn.setAlpha(1f);
            checkoutBtn.setEnabled(true);
        }



        cartBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cartList = cart.foodCartList;

        mAuth = FirebaseAuth.getInstance();

        cartAdapter = new CartAdapter(FoodCartActivity.this,FoodCartActivity.this, cartList);
        LinearLayoutManager cartManager = new LinearLayoutManager(FoodCartActivity.this, LinearLayoutManager.VERTICAL, false);
        cartRecyclerView.setLayoutManager(cartManager);
        cartRecyclerView.setAdapter(cartAdapter);

        updateCartDetails();




            checkoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mAuth.getCurrentUser() != null) {

                        custID = mAuth.getCurrentUser().getUid();
                        Log.d(TAG,"Customer ID: "+custID);
                        orderID = getAlphaNumericString(20);

                        Intent intent = new Intent(FoodCartActivity.this, PaymentActivity.class);
                        intent.putExtra("orderid", orderID);
                        intent.putExtra("custid", custID);
                        intent.putExtra("amount_to_pay", amountToPay);
                        startActivity(intent);

                    }  else {
                        Snackbar snackbar = Snackbar.make(constraintLayout, "Please Sign In first.", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            });











    }

    public static void updateCartDetails(){
        FoodCart cart = FoodCart.getInstance();
        int size = 0;
        for(int i=0; i<cart.foodCartList.size(); i++){
            size+= cart.foodCartList.get(i).getQuantity();
        }

        totalItemsTV.setText("Total Items: "+size);
        int totalAmount = 0;
        for(int i=0; i<cartList.size(); i++){
            totalAmount+= (Integer.parseInt(cartList.get(i).getFood().getFoodPrice()) * cartList.get(i).getQuantity());
        }

        amountToPay = String.valueOf(totalAmount);

        totalCartAmountTV.setText("Amount To Be Paid: ₹ "+totalAmount);


    }

    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
