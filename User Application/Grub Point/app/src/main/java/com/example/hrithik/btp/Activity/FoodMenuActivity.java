package com.example.hrithik.btp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.hrithik.btp.Adapters.CuisineAdapter;
import com.example.hrithik.btp.Adapters.FoodAdapter;
import com.example.hrithik.btp.Fragments.MenuFragment;
import com.example.hrithik.btp.Helper.FoodCart;
import com.example.hrithik.btp.Helper.FoodDatabase;
import com.example.hrithik.btp.Model.Cuisine;
import com.example.hrithik.btp.Model.Food;
import com.example.hrithik.btp.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FoodMenuActivity extends AppCompatActivity {

    private static  String TAG = "FoodMenuActivity";
    private static ArrayList<Food> localFoodDatabase;

    private static ArrayList<Food> foodList;
    private static ArrayList<Cuisine> cuisineArrayList;
    private static FoodAdapter mFoodAdapter;
    private static RecyclerView foodRecyclerView;

    private static RecyclerView cuisineRecyclerView;
    private static CuisineAdapter mCuisineAdapter;

    public static String cuisineSelected = "101";
    //private View rootView;

    private boolean isCuisineVisible = false;


    private static FirebaseFirestore firebaseFirestore;
    private static Query query;

    private static Context mContext;
    private static Activity mActivity;
    private ImageView foodCartIV;
    private static TextView cartItemCount;
    private ImageView orderHistoryIcon;

    private Toolbar toolbar;




    //TODO  FAB double click error


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);

        setTitle("Menu");

        mContext = FoodMenuActivity.this;
        mActivity = FoodMenuActivity.this;
        foodRecyclerView = findViewById(R.id.menu_rv);
        cuisineRecyclerView = findViewById(R.id.cuisine_rv);
        foodCartIV = findViewById(R.id.food_cart_icon_iv);
        cartItemCount = findViewById(R.id.cart_item_count_tv);
        toolbar = findViewById(R.id.food_menu_toolbar);
        orderHistoryIcon = findViewById(R.id.orders_menu_icon);

        orderHistoryIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodMenuActivity.this, UserFoodOrders.class));
            }
        });

        foodList = new ArrayList<>();
        cuisineArrayList = new ArrayList<>();
        localFoodDatabase = new ArrayList<>();

        //toolbar.inflateMenu(R.menu.food_menu_options);




       // addDataToDatabase();
        //addcuisines();


        // Food Recycler View
        getFoodListFromServer();
        //addcuisines();
        //addDataToFoodList(cuisineSelected);
        //Log.d(TAG, "Final Food List: "+foodList);




        // Cuisine Recycler View
        //mCuisineAdapter = new CuisineAdapter(FoodMenuActivity.this, cuisineArrayList, FoodMenuActivity.this);
        //LinearLayoutManager cuisineManager = new LinearLayoutManager(FoodMenuActivity.this, LinearLayoutManager.VERTICAL, false);
        //cuisineRecyclerView.setLayoutManager(cuisineManager);
        //cuisineRecyclerView.setAdapter(mCuisineAdapter);

        foodCartIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FoodMenuActivity.this,FoodCartActivity.class));
            }
        });



        // Floating Action Button
        final FloatingActionButton fab = findViewById(R.id.slide_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "FAB CLICKED", Toast.LENGTH_SHORT).show();

                if(isCuisineVisible) {
                    Log.d(TAG, "Cuisine visibility GONE called");
                    isCuisineVisible=false;
                    cuisineRecyclerView.setVisibility(View.GONE);
                    fab.setImageResource(R.drawable.right_arrow_icon);
                }else{
                    Log.d(TAG, "Cuisine visibility VISIBLE called");
                    isCuisineVisible=true;
                    cuisineRecyclerView.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.left_arrow_white);
                }
            }
        });

        FoodMenuActivity.updateCartItemCountIcon();

    }

    public static void updateCartItemCountIcon(){
        FoodCart cart = FoodCart.getInstance();
        int size = 0;

        for(int i=0; i<cart.foodCartList.size(); i++){
            size+= cart.foodCartList.get(i).getQuantity();
        }

        if(size == 0){
            cartItemCount.setVisibility(View.GONE);
        } else {
            cartItemCount.setVisibility(View.VISIBLE);
            cartItemCount.setText(String.valueOf(size));
        }
    }

    private void displayCart(){
        FoodCart cart = FoodCart.getInstance();

        for(int i=0; i<cart.foodCartList.size(); i++){
            Log.d(TAG, cart.foodCartList.get(i).getFood().getFoodName()+" : "+cart.foodCartList.get(i).getQuantity());
        }
    }

    public static void getFoodListFromServer(){

        Log.d(TAG, "get food list from server called");

        // Retriving food data from database...
        localFoodDatabase = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        query = firebaseFirestore.collection("Food").orderBy("foodScore", Query.Direction.DESCENDING);
        query.addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                Log.d(TAG, "Query Snapshot: "+queryDocumentSnapshots);

                if(queryDocumentSnapshots!=null && !queryDocumentSnapshots.isEmpty()){

                    for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){

                       String foodImage =  doc.getDocument().getString("foodImage");
                       String foodName = doc.getDocument().getString("foodName");
                       String foodDesc = doc.getDocument().getString("foodDesc");
                       //String foodRating = doc.getDocument().getString("foodRating");
                       String foodPrice =  doc.getDocument().getString("foodPrice");
                       String foodCuisineCode = doc.getDocument().getString("foodCuisineCode");
                       String foodTotalReviewCount = String.valueOf(doc.getDocument().get("foodTotalReviewCount"));
                       String foodScore =  String.valueOf(doc.getDocument().get("foodScore"));
                       String foodPositiveReviewCount =  String.valueOf(doc.getDocument().get("foodPositiveReviewCount"));
                       String foddDocID = doc.getDocument().getId();
                       String foodCode = String.valueOf(doc.getDocument().get("foodCode"));



                       // Food food = doc.getDocument().toObject(Food.class);
                        //food.setFoodDocID(doc.getDocument().getId());
                        localFoodDatabase.add(new Food(foodCode, foodImage,foodName,foodDesc,foodPrice,foodCuisineCode,foodTotalReviewCount,foodScore,foodPositiveReviewCount,foddDocID));

                    }
                    Log.d(TAG, "Local Food DB: "+String.valueOf(localFoodDatabase));

                    FoodDatabase foodDatabase = FoodDatabase.getInstance();
                    foodDatabase.savedFoodDatabaseList = localFoodDatabase;

                    addDataToFoodList(cuisineSelected);
                    addcuisines();
                    Collections.sort(foodList, new Comparator<Food>() {
                        @Override
                        public int compare(Food f1, Food f2) {

                            if(Float.valueOf(f1.getFoodScore())> Float.valueOf(f2.getFoodScore()))
                                return -1;
                            else
                                return 1;
                        }
                    });

                    mFoodAdapter = new FoodAdapter(mContext, foodList, mActivity);

                    LinearLayoutManager foodManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    foodRecyclerView.setLayoutManager(foodManager);
                    foodRecyclerView.setAdapter(mFoodAdapter);


                }
            }
        });
    }

    private static void addDataToFoodList(String cuisineSelectedCode) {

        Log.d(TAG,"add data to food list called");

        foodList = new ArrayList<>();
        //addDataToDatabase();
        //getFoodListFromServer();

        FoodDatabase foodDatabase = FoodDatabase.getInstance();
        Log.d(TAG, "Saved DB: "+foodDatabase.savedFoodDatabaseList);
        localFoodDatabase =  foodDatabase.savedFoodDatabaseList;

        if(cuisineSelectedCode.equals("100")){
            foodList = localFoodDatabase;
        } else {
            for (int i = 0; i < localFoodDatabase.size(); i++) {
                if (cuisineSelectedCode.equals(localFoodDatabase.get(i).getFoodCuisineCode()))
                    foodList.add(localFoodDatabase.get(i));
            }
        }

        /*Collections.sort(foodList, new Comparator<Food>() {
            @Override
            public int compare(Food f1, Food f2) {

                if(f1.getFoodScore().compareTo(f2.getFoodScore()) > 0)
                    return 1;
                else
                    return 0;
            }
        });*/

        Log.d(TAG, "Updated Food List: "+String.valueOf(foodList));


        //Log.d(TAG, String.valueOf(foodRecyclerView));
        //Log.d(TAG, String.valueOf(mFoodAdapter));

    }

    public static void cuisineOnClick(Context context, String cuisineCode, Activity activity){

        Log.d(TAG, "Cuisine onClick called");

        //Toast.makeText(context, cuisineCode, Toast.LENGTH_SHORT).show();

        FoodMenuActivity foodMenuActivity = new FoodMenuActivity();
        foodMenuActivity.addDataToFoodList(cuisineCode);

        Log.d(TAG,"Adapter OnClick: "+ foodRecyclerView);
        Log.d(TAG,"Adapter OnClick: "+ mFoodAdapter);
        Collections.sort(foodList, new Comparator<Food>() {
            @Override
            public int compare(Food f1, Food f2) {

                if(Float.valueOf(f1.getFoodScore())> Float.valueOf(f2.getFoodScore()))
                    return -1;
                else
                    return 1;
            }
        });

        mFoodAdapter = new FoodAdapter(context, foodList, activity);
        foodRecyclerView.setAdapter(mFoodAdapter);
        mFoodAdapter.notifyDataSetChanged();


    }

    private static void addcuisines() {

        Log.d(TAG, "add Cuisines called.");

        cuisineArrayList.clear();

        cuisineArrayList.add(new Cuisine("101","Snacks"));
        cuisineArrayList.add(new Cuisine("102","Drinks"));
        cuisineArrayList.add(new Cuisine("103","South Indian"));
        cuisineArrayList.add(new Cuisine("104","Chinese"));
        cuisineArrayList.add(new Cuisine("105","Shakes"));


         for(int i=0; i<cuisineArrayList.size(); i++){
            int count=0;
            for(int j=0; j<localFoodDatabase.size(); j++){

                if(cuisineArrayList.get(i).getCuisineCode().equals(localFoodDatabase.get(j).getFoodCuisineCode()))
                    count++;
            }
            cuisineArrayList.get(i).setCuisineName(cuisineArrayList.get(i).getCuisineName()+"\n("+count+")");
        }

        // Adding ALL Cuisine Section Here Manually....
        cuisineArrayList.add(new Cuisine("100","All\n("+localFoodDatabase.size()+")"));


        mCuisineAdapter = new CuisineAdapter(mContext, cuisineArrayList, mActivity);
        LinearLayoutManager cuisineManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        cuisineRecyclerView.setLayoutManager(cuisineManager);
        cuisineRecyclerView.setAdapter(mCuisineAdapter);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.my_profile_option_menu)
            startActivity(new Intent(this,SetupActivity.class));
        return true;
    }

    /* void addDataToDatabase(){
        localFoodDatabase = new ArrayList<>();
        localFoodDatabase.add(new Food(R.drawable.pizza, "Pizza", "Pizza is a savory dish of Italian origin, consisting of a usually round, flattened base of leavened wheat-based dough topped with tomatoes, cheese, and various other ingredients (anchovies, olives, meat, etc.) baked at a high temperature, traditionally in a wood-fired oven.", "4/5", "₹ 250","101"));
        localFoodDatabase.add(new Food(R.drawable.burger, "Burger", "A hamburger is a sandwich consisting of one or more cooked patties of ground meat, usually beef, placed inside a sliced bread roll or bun. The patty may be pan fried, grilled, or flame broiled. ... A hamburger topped with cheese is called a cheeseburger", "4.5/5", "₹ 50","101"));
        localFoodDatabase.add(new Food(R.drawable.pasta, "Pasta", "Pasta is a type of food made from a mixture of flour, eggs, and water that is formed into different shapes and then boiled. Spaghetti, macaroni, and noodles are types of pasta", "3.9/5", "₹ 100", "101"));
        localFoodDatabase.add(new Food(R.drawable.noodles, "Noodles", "A noodle is a piece of pasta, especially a long, skinny one. You can eat noodles with butter and cheese or sauce, or slurp them from a bowl of soup. Noodles are cut or rolled from a dough that contains some kind of flour — wheat, buckwheat, and rice flour are all commonly used", "3.7/5", "₹ 80", "104"));
        localFoodDatabase.add(new Food(R.drawable.french_fries, "French Fries", "French fries or simply fries or chips, are pieces of potato that have been deep-fried. ... These are deep-fried, very thin, salted slices of potato that are usually served at room temperature. French fries have numerous variants, from thick-cut to shoestring, crinkle, curly and many other names", "4.2/5", "₹ 100","101"));
        // localFoodDatabase.add(new Food(R.drawable.samosa, "Samosa", "A samosa is a fried or baked pastry with a savoury filling, such as spiced potatoes, onions, peas, meat, or lentils. It may take different forms, including triangular, cone, or half-moon shapes, depending on the region.","3.5/5","₹ 20", "101"));
        localFoodDatabase.add(new Food(R.drawable.chilli_potato, "Chilli Potato", "Chilli potato is a Indo chinese starter made with fried potatoes tossed in spicy, slightly sweet & sour chilli sauce. ","4.5/5","₹ 120", "101"));
        localFoodDatabase.add(new Food(R.drawable.pav_bhaji, "Pav Bhaji", "Pav bhaji has many variations in ingredients and garnishes, but is essentially a spiced mixture of mashed vegetables in a thick gravy, usually cooked on a flat griddle (tava) and served hot with a soft white bread roll.","4.5/5","₹ 220", "101"));
        localFoodDatabase.add(new Food(R.drawable.spring_roll, "Spring Roll", "A spring roll is a Chinese food consisting of a small roll of thin pastry filled with vegetables and sometimes meat, and then fried.","3.9/5","₹ 90", "101"));
        localFoodDatabase.add(new Food(R.drawable.oreo_shake, "Oreo Shake", "A beloved classic recipe used in restaurants the world over, this Oreo milk shake is the ideal sweet treat.","4.9/5","₹ 120", "105"));
        localFoodDatabase.add(new Food(R.drawable.banana_shake, "Banana Shake", " Banana milkshake recipe - Delicious, creamy and healthy banana shake you can whip up in minutes.","4.5/5","₹ 100", "105"));
        localFoodDatabase.add(new Food(R.drawable.choclate_shake, "Chocolate Shake", "","4.9/5","₹ 150", "105"));
        localFoodDatabase.add(new Food(R.drawable.coca_cola, "Coca Cola", "","4.5/5","₹ 40", "102"));
        localFoodDatabase.add(new Food(R.drawable.dosa, "Dosa ", "A dosa is a cooked flat thin layered rice batter, originating from the South India, made from a fermented batter. It is somewhat similar to a crepe in appearance.","4.9/5","₹ 150", "103"));
    }*/

    @Override
    public void onBackPressed() {
        Intent backToMainIntent = new Intent(FoodMenuActivity.this, MainActivity.class);
        backToMainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backToMainIntent);
    }
}
