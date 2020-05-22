package com.example.hrithik.btp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.hrithik.btp.Adapters.CuisineAdapter;
import com.example.hrithik.btp.Adapters.FoodAdapter;
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

/*

    Activity INFO:

    Show cuisine list at left and corresponding
    food list at right. User can add food to cart from this
    Activity.


 */


public class MenuFragment extends Fragment {

    private static  String TAG = "MenuFragment";
    private ArrayList<Food> localFoodDatabase;

    private static ArrayList<Food> foodList;
    private static ArrayList<Cuisine> cuisineArrayList;
    private static FoodAdapter mFoodAdapter;
    private static RecyclerView foodRecyclerView;

    private RecyclerView cuisineRecyclerView;
    private CuisineAdapter mCuisineAdapter;

    public static String cuisineSelected = "101";
    private  View rootView;

    private boolean isCuisineVisible = true;


    //TODO Cuisine recycler view at left of screen

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        foodRecyclerView = rootView.findViewById(R.id.menu_rv);
        cuisineRecyclerView = rootView.findViewById(R.id.cuisine_rv);

        foodList = new ArrayList<>();
        cuisineArrayList = new ArrayList<>();
        localFoodDatabase = new ArrayList<>();

      //  addDataToDatabase();
      //  addcuisines();


        // Food Recycler View
        //addDataToFoodList(cuisineSelected);
        mFoodAdapter = new FoodAdapter(getContext(), foodList, getActivity());
        LinearLayoutManager foodManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        foodRecyclerView.setLayoutManager(foodManager);
        foodRecyclerView.setAdapter(mFoodAdapter);



        // Cuisine Recycler View
        mCuisineAdapter = new CuisineAdapter(getContext(), cuisineArrayList, getActivity());
        LinearLayoutManager cuisineManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        cuisineRecyclerView.setLayoutManager(cuisineManager);
        cuisineRecyclerView.setAdapter(mCuisineAdapter);



        // Floating Action Button
        final FloatingActionButton fab = rootView.findViewById(R.id.slide_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "FAB CLICKED", Toast.LENGTH_SHORT).show();

                if(isCuisineVisible) {
                    isCuisineVisible=false;
                    cuisineRecyclerView.setVisibility(View.GONE);
                    fab.setImageResource(R.drawable.right_arrow_icon);
                }else{
                    isCuisineVisible=true;
                    cuisineRecyclerView.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.left_arrow_white);
                }
            }
        });






        return rootView;
    }/*

    private void addDataToFoodList(String cuisineSelectedCode) {

        foodList = new ArrayList<>();
        addDataToDatabase();

        for(int i=0; i<localFoodDatabase.size(); i++){
            if(cuisineSelectedCode.equals(localFoodDatabase.get(i).getFoodCuisineCode()))
                foodList.add(localFoodDatabase.get(i));
        }

        Log.d(TAG, "Updated Food List: "+String.valueOf(foodList));


        Log.d(TAG, String.valueOf(foodRecyclerView));
        Log.d(TAG, String.valueOf(mFoodAdapter));

    }

    public static void cuisineOnClick(Context context, String cuisineCode, Activity activity){
        Toast.makeText(context, cuisineCode, Toast.LENGTH_SHORT).show();

        MenuFragment fragment = new MenuFragment();
        fragment.addDataToFoodList(cuisineCode);

        Log.d(TAG,"Adapter OnClick: "+ foodRecyclerView);
        Log.d(TAG,"Adapter OnClick: "+ mFoodAdapter);

        mFoodAdapter = new FoodAdapter(context, foodList, activity);
        foodRecyclerView.setAdapter(mFoodAdapter);
        mFoodAdapter.notifyDataSetChanged();


    }

    private void addcuisines() {

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


    }

    void addDataToDatabase(){
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



    }
*/

}
