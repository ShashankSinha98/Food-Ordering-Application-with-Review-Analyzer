package com.example.hrithik.btp.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hrithik.btp.Activity.FoodMenuActivity;
import com.example.hrithik.btp.Activity.OrderFoodCommentActivity;
import com.example.hrithik.btp.Activity.PranavActivity;
import com.example.hrithik.btp.Activity.UserFoodOrders;
import com.example.hrithik.btp.Model.FoodQty;
import com.example.hrithik.btp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class OrderFoodCommentAdapter extends RecyclerView.Adapter<OrderFoodCommentAdapter.ViewHolder>{

    private static final String TAG = "OrderFoodCommentAdapter";
    private Context mContext;
    private ArrayList<FoodQty> mFoodList;
    private Activity mActivity;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ProgressDialog comment_progress;
    private Boolean ans = false;
    private int posSelected;
    private  AlertDialog dialog;
    private String foodCommentSelected;
    private Double pos, neg;
    private Double  foodNegativeReviewCount;
    private int foodPositiveReviewCount, foodTotalReviewCount;
    private Float posScoreFloat, negScoreFloat, foodScoreFloat;




    public OrderFoodCommentAdapter(Context mContext, ArrayList<FoodQty> mFoodList, Activity activity, FirebaseFirestore firebaseFirestore, FirebaseAuth firebaseAuth) {
        this.mContext = mContext;
        this.mFoodList = mFoodList;
        this.mActivity = activity;
        this.firebaseAuth = firebaseAuth;
        this.firebaseFirestore = firebaseFirestore;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_food_comment_single_row, viewGroup, false);
        return new OrderFoodCommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.foodName.setText(mFoodList.get(position).getFoodName());
        holder.foodPrice.setText("₹"+mFoodList.get(position).getFoodPrice());
        holder.foodQty.setText("x"+mFoodList.get(position).getFoodQty());
        holder.foodPriceTotal.setText("₹"+ (Integer.valueOf(mFoodList.get(position).getFoodPrice())
                *Integer.valueOf(mFoodList.get(position).getFoodQty()))
        );

        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.image_placeholder);
        Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(mFoodList.get(position).getFoodImage()).into(holder.foodImage);

        if(mFoodList.get(position).getFoodcomment().equals("null")){
            holder.foodCommentStatus.setText("Not Rated");
            holder.foodCommentStatus.setTextColor(Color.RED);
            holder.foodReview.setVisibility(View.GONE);
            holder.foodReview.setVisibility(View.INVISIBLE);

        } else {
            holder.foodCommentStatus.setText("Rated");
            holder.foodCommentStatus.setTextColor(Color.GREEN);
            holder.foodReview.setVisibility(View.VISIBLE);
            holder.foodReview.setText(mFoodList.get(position).getFoodcomment());
            holder.foodReviewStatus.setVisibility(View.VISIBLE);

            if(mFoodList.get(position).getFoodResp().equals("pos")) {
                holder.foodReviewStatus.setTextColor(Color.GREEN);
                holder.foodReviewStatus.setText("Positive");
            } else if(mFoodList.get(position).getFoodResp().equals("neg")) {
                holder.foodReviewStatus.setTextColor(Color.RED);
                holder.foodReviewStatus.setText("Negative");
            } else if(mFoodList.get(position).getFoodResp().equals("neu")){
                holder.foodReviewStatus.setTextColor(Color.GRAY);
                holder.foodReviewStatus.setText("Neutral");
            }
        }

        holder.foodCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mFoodList.get(position).getFoodcomment().equals("null")) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    View mView = mActivity.getLayoutInflater().inflate(R.layout.custom_food_comment_dialog, null);

                    TextView foodName = mView.findViewById(R.id.food_details_name);
                    ImageView foodImage = mView.findViewById(R.id.food_details_food_iv);
                    final EditText foodCommentET = mView.findViewById(R.id.food_comment_et);
                    Button submitBtn = mView.findViewById(R.id.submit_btn);

                    foodName.setText(mFoodList.get(position).getFoodName());
                    Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(mFoodList.get(position).getFoodImage()).into(foodImage);


                    mBuilder.setView(mView);
                    dialog = mBuilder.create();
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setCancelable(true);
                    dialog.show();

                    comment_progress = new ProgressDialog(mContext);


                    submitBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!foodCommentET.getText().toString().trim().equals("")) {

                                String cleaned_review = foodCommentET.getText().toString().trim().replaceAll("[^\\w\\s]","");

                                comment_progress.setTitle("Running ML Model");
                                comment_progress.setMessage("Please wait while we analyze your feedback !");
                                comment_progress.setCanceledOnTouchOutside(false);
                                comment_progress.setCancelable(false);
                                comment_progress.show();

                                // Running ML model on review and getting its positive score
                                float pos_score = OrderFoodCommentActivity.processReview(cleaned_review);
                                runMLSentimentOnComment(pos_score);


                                posSelected = position;
                                foodCommentSelected = foodCommentET.getText().toString().trim();
                                /*if(!reviewSentiment.equals("null"))
                                sendCommentDataToServer(position, mFoodList.get(position).getOrderDocId(), mFoodList.get(position).getFoodDocId(), foodCommentET.getText().toString().trim(), dialog, reviewSentiment);
                                else {
                                    Log.d(TAG,"Sentiment analysis Failed");
                                    Toast.makeText(mContext, "Sentiment analysis Failed", Toast.LENGTH_SHORT).show();
                                }*/
                            } else {
                                Snackbar.make(holder.foodCommentLayout, "Comment can't be Blank!!", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

    private void runMLSentimentOnComment(float pos_score) {

        posScoreFloat = pos_score;
        negScoreFloat = 1-posScoreFloat;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View mView = mActivity.getLayoutInflater().inflate(R.layout.custom_ml_result_dialog, null);

        final TextView commentText = mView.findViewById(R.id.comment_ml);
        TextView posScore = mView.findViewById(R.id.pos_score_ml);
        TextView negScore = mView.findViewById(R.id.neg_score_ml);
        Button confirm_btn = mView.findViewById(R.id.confirm_btn);
        Button deny_btn = mView.findViewById(R.id.deny_btn);
        TextView resultText = mView.findViewById(R.id.res_ml);

        if(posScoreFloat>0.65) {
            resultText.setText("POSITIVE");
            resultText.setTextColor(Color.GREEN);
        } else if(negScoreFloat > 0.65){
            resultText.setText("NEGATIVE");
            resultText.setTextColor(Color.RED);
        } else {
            resultText.setText("NEUTRAL");
            resultText.setTextColor(Color.GRAY);
        }

        commentText.setText(foodCommentSelected);
        posScore.setText("Pos: "+posScoreFloat);
        negScore.setText("Neg: "+negScoreFloat);

        mBuilder.setView(mView);
        final AlertDialog comment_dialog = mBuilder.create();
        comment_dialog.setCancelable(false);
        comment_dialog.show();
        comment_progress.dismiss();

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment_progress.show();
                if(posScoreFloat>0.65){
                    sendCommentDataToServer(posSelected, mFoodList.get(posSelected).getOrderDocId(), mFoodList.get(posSelected).getFoodDocId(), foodCommentSelected, comment_dialog, "pos");
                } else if(negScoreFloat > 0.65){
                    sendCommentDataToServer(posSelected, mFoodList.get(posSelected).getOrderDocId(), mFoodList.get(posSelected).getFoodDocId(), foodCommentSelected, comment_dialog, "neg");
                } else {
                    sendCommentDataToServer(posSelected, mFoodList.get(posSelected).getOrderDocId(), mFoodList.get(posSelected).getFoodDocId(), foodCommentSelected, comment_dialog, "neu");
                }

            }
        });

        deny_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment_progress.dismiss();
                //dialog.dismiss();
                comment_dialog.dismiss();
            }
        });

        /*try {
            final String url = "http://192.168.156.97:5000/api/v1/resources/food?text= "+foodComment;

            RequestQueue requestQueue = Volley.newRequestQueue(mContext);


            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    //Toast.makeText(mContext, "Response: " + response, Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "Response of ML: "+response);
                    Toast.makeText(mContext, "Response: "+response, Toast.LENGTH_SHORT).show();

                    String ans = "neu";

                    try {
                        JSONObject jsonObj = new JSONObject(response);
                         pos =  jsonObj.getDouble("pos");
                         posScoreFloat = Float.valueOf(String.format("%.2f",pos));

                         neg =  jsonObj.getDouble("neg");
                         negScoreFloat = Float.valueOf(String.format("%.2f",neg));

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                        View mView = mActivity.getLayoutInflater().inflate(R.layout.custom_ml_result_dialog, null);

                        final TextView commentText = mView.findViewById(R.id.comment_ml);
                        TextView posScore = mView.findViewById(R.id.pos_score_ml);
                        TextView negScore = mView.findViewById(R.id.neg_score_ml);
                        Button confirm_btn = mView.findViewById(R.id.confirm_btn);
                        Button deny_btn = mView.findViewById(R.id.deny_btn);
                        TextView resultText = mView.findViewById(R.id.res_ml);

                        if(posScoreFloat>0.65) {
                            resultText.setText("POSITIVE");
                            resultText.setTextColor(Color.GREEN);
                        } else if(negScoreFloat > 0.65){
                            resultText.setText("NEGATIVE");
                            resultText.setTextColor(Color.RED);
                        } else {
                            resultText.setText("NEUTRAL");
                            resultText.setTextColor(Color.GRAY);
                        }

                        commentText.setText(foodCommentSelected);
                        posScore.setText("Pos: "+posScoreFloat);
                        negScore.setText("Neg: "+negScoreFloat);

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.setCancelable(false);
                        dialog.show();
                        comment_progress.dismiss();

                        confirm_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                comment_progress.show();
                                if(posScoreFloat>0.65){
                                    sendCommentDataToServer(posSelected, mFoodList.get(posSelected).getOrderDocId(), mFoodList.get(posSelected).getFoodDocId(), foodCommentSelected, dialog, "pos");
                                } else if(negScoreFloat > 0.65){
                                    sendCommentDataToServer(posSelected, mFoodList.get(posSelected).getOrderDocId(), mFoodList.get(posSelected).getFoodDocId(), foodCommentSelected, dialog, "neg");
                                } else {
                                    sendCommentDataToServer(posSelected, mFoodList.get(posSelected).getOrderDocId(), mFoodList.get(posSelected).getFoodDocId(), foodCommentSelected, dialog, "neu");
                                }
                            }
                        });

                        deny_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                comment_progress.dismiss();
                                dialog.dismiss();
                            }
                        });






                        //Log.d(TAG, "Pos: "+pos+" : Neg: "+neg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }





                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(mContext, "Error: " + error, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "ERROR: " + error);

                }
            });

            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 300000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 300000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });

            requestQueue.add(stringRequest);


        } catch (Exception e){
            Log.d(TAG, e.toString());
            Toast.makeText(mContext, "OOPS! Error in running ML Model", Toast.LENGTH_SHORT).show();
        comment_progress.dismiss();

        }*/

    }

    private void sendCommentDataToServer(final int pos, final String orderDocId, final String foodDocID, final String foodComment, final AlertDialog comment_dialog, final String reviewSentiment) {

        comment_progress.setTitle("Sending Feedback");
        comment_progress.setMessage("Please wait while we send your feedback !");
        comment_progress.setCanceledOnTouchOutside(false);
        comment_progress.show();

        try {
            Log.d(TAG,"Food ID: "+foodDocID);
            firebaseFirestore.collection("Food").document(foodDocID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        ArrayList<String> commentList = (ArrayList<String>) task.getResult().get("foodComment");
                        String foodPositiveReview = String.valueOf( task.getResult().get("foodPositiveReviewCount"));
                        String foodTotalReview = String.valueOf( task.getResult().get("foodTotalReviewCount"));
                        String currFoodScore = String.valueOf( task.getResult().get("foodScore"));

                        foodPositiveReviewCount = Integer.valueOf(foodPositiveReview);
                        foodTotalReviewCount = Integer.valueOf(foodTotalReview);
                        //foodNegativeReviewCount = foodTotalReviewCount - foodPositiveReviewCount;

                        Log.d(TAG, "Old Food Score: "+currFoodScore);
                        foodTotalReviewCount+=1;
                        if(reviewSentiment.equals("pos")){

                            foodScoreFloat = Float.valueOf(currFoodScore)+posScoreFloat;
                            foodPositiveReviewCount+=1;
                            Log.d(TAG, "Adding score");

                        } else if(reviewSentiment.equals("neg")){
                            foodScoreFloat = Float.valueOf(currFoodScore)-negScoreFloat;
                            Log.d(TAG, "Subtracting score");
                        } else if(reviewSentiment.equals("neu")){
                            foodScoreFloat = Float.valueOf(currFoodScore) + posScoreFloat - negScoreFloat;
                            Log.d(TAG, "Multiple computation");
                        }

                        Log.d(TAG, "New Food Score: "+foodScoreFloat);

                        if (commentList.size() == 1 && commentList.get(0).trim().equals("null")) {
                            commentList.clear();
                            commentList.add(foodComment.trim());

                        } else {
                            commentList.add(foodComment.trim());
                        }

                        Log.d(TAG, "Response sending to Firebase(foodPositiveReviewCount): "+foodPositiveReviewCount+" :(foodTotalReviewCount) "+foodTotalReviewCount+" : "+reviewSentiment);

                        firebaseFirestore.collection("Food").document(foodDocID)
                                .update("foodComment",commentList,
                                        "foodPositiveReviewCount",foodPositiveReviewCount,
                                        "foodTotalReviewCount",foodTotalReviewCount,
                                        "foodScore", String.format("%.2f",foodScoreFloat)).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Toast.makeText(mContext, "Thank you for submitting your valuable feedback!", Toast.LENGTH_LONG).show();
                                            //dialog.dismiss();
                                            //comment_progress.dismiss();

                                            Toast.makeText(mContext, "Review Count Updated at Food Database", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Review Count Updated at Food Database");
                                            sendCommentDataToUserOrders(pos, orderDocId, foodDocID, foodComment, comment_dialog, reviewSentiment);
                                        }
                                    }
                                });

                    } else {
                        Toast.makeText(mContext, "Ooops! Error Occured.", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        comment_progress.dismiss();
                    }


                }
            });
        } catch (Exception e){
            Log.d(TAG, "Exception 1: "+e.getMessage());
            dialog.dismiss();
        }

    }

    private void sendCommentDataToUserOrders(final int pos, final String orderDocId, String foodDocID, final String foodComment, final AlertDialog comment_dialog, final String reviewSentiment) {

        firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                .collection("Orders").document(orderDocId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){
                    ArrayList<String> orderItemList = (ArrayList<String>) task.getResult().get("orderDetails");
                    //Log.d("xlr88",orderItemList.get(pos));




                    String data = orderItemList.get(pos);
                    String modifiedData = data.substring(0,data.indexOf("#Comment:"))+"#Comment: "+foodComment+"#Response: "+reviewSentiment;
                    //Log.d("xlr88", modifiedData);

                    orderItemList.set(pos,modifiedData);
                    firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                            .collection("Orders").document(orderDocId).update("orderDetails", orderItemList)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        //OrderFoodCommentActivity.orderFoodCommentAdapter.notifyDataSetChanged();
                                        //OrderFoodCommentActivity.populateList();

                                        mFoodList.get(pos).setFoodcomment(foodComment);
                                        Toast.makeText(mContext, "Thank you for submitting your valuable feedback!", Toast.LENGTH_LONG).show();
                                        comment_dialog.dismiss();
                                        dialog.dismiss();
                                        comment_progress.dismiss();
                                        UserFoodOrders.getOrdersFromServer();
                                        OrderFoodCommentActivity.populateList();
                                        FoodMenuActivity.getFoodListFromServer();
                                        //mActivity.finish();
                                        //mActivity.startActivity(mActivity.getIntent());
                                        //notifyDataSetChanged();


                                    } else {
                                        Toast.makeText(mContext, "Ooops! Error Occured.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mFoodList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView foodName, foodPrice, foodQty, foodPriceTotal, foodCommentStatus, foodReview, foodReviewStatus;
        private ImageView foodImage;
        private ConstraintLayout foodCommentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            foodName = itemView.findViewById(R.id.food_name);
            foodReviewStatus = itemView.findViewById(R.id.food_resp_tv);
            foodPrice = itemView.findViewById(R.id.food_price);
            foodQty = itemView.findViewById(R.id.food_qty);
            foodPriceTotal = itemView.findViewById(R.id.food_qty_total_price);
            foodImage = itemView.findViewById(R.id.food_img);
            foodCommentStatus = itemView.findViewById(R.id.comment_status);
            foodCommentLayout = itemView.findViewById(R.id.food_comment_layout);
            foodReview = itemView.findViewById(R.id.food_review);
        }
    }
}
