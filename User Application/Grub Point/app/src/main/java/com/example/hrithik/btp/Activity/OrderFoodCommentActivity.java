package com.example.hrithik.btp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.hrithik.btp.Adapters.OrderFoodCommentAdapter;
import com.example.hrithik.btp.Model.FoodQty;
import com.example.hrithik.btp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderFoodCommentActivity extends AppCompatActivity {

    private ImageView backIcon;
    private static final String TAG = "OFCA";
    private static ArrayList<FoodQty> foodList;

    private static RecyclerView feedbackRecyclerView;
    public static OrderFoodCommentAdapter orderFoodCommentAdapter;

    private static FirebaseAuth mAuth;
    private static FirebaseFirestore firebaseFirestore;
    public static Context mContext;
    public static Activity mActivity;

    private static float[][] input_val;
    private static float[][] outputValue;
    private static Interpreter tfliteInterpreter;
    private static HashMap<String, Float> word_to_idx_dict;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_food_comment);

        input_val = new float[1][500];
        outputValue = new float[1][1];

        // 1. Loading Model
        loadModel();

        // 2. Loading Vocab
        loadVocab();

        backIcon = findViewById(R.id.back_arrow_feedback);
        feedbackRecyclerView = findViewById(R.id.feedback_rv);
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        foodList = (ArrayList<FoodQty>) bundle.getSerializable("food_list");

        mContext = this;
        mActivity = OrderFoodCommentActivity.this;


        populateList();


    }

    public static void populateList() {

        orderFoodCommentAdapter = new OrderFoodCommentAdapter(mContext, foodList, mActivity, firebaseFirestore, mAuth);
        LinearLayoutManager feedbackManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        feedbackRecyclerView.setLayoutManager(feedbackManager);
        feedbackRecyclerView.setAdapter(orderFoodCommentAdapter);
    }

    private void loadModel() {

        try{
            tfliteInterpreter = new Interpreter(loadModelFile());
            Log.d(TAG+"_model","Model Loaded");
        } catch(Exception ex){
            ex.printStackTrace();
            Log.d(TAG+"_error","Model Not Loaded: "+ex.getMessage());
        }

    }

    public static float processReview(String cleaned_review){
        input_val = new float[1][500];
        outputValue = new float[1][1];


        String words[] = cleaned_review.split("\\s+");
        int j=input_val[0].length-1;

        for(int i=0; i<words.length && j>=0; i++){
            if(word_to_idx_dict.get(words[i].toLowerCase())!=null){
                input_val[0][j] = word_to_idx_dict.get(words[i].toLowerCase());
                j--;
            }
        }

        String res= "";
        for(int i=0; i<input_val[0].length; i++){
            res+=input_val[0][i]+", ";
        }
        Log.d(TAG+"_ex1",res);

        int left = -1;
        int right = input_val[0].length-1;

        for(int i=0; i<=right; i++){
            if(input_val[0][i]!=0){
                left = i;
                break;
            }
        }

        while(left<=right){
            float temp = input_val[0][left];
            input_val[0][left] = input_val[0][right];
            input_val[0][right] = temp;
            left++;
            right--;
        }

        res= "";
        for(int i=0; i<input_val[0].length; i++){
            res+=input_val[0][i]+", ";
        }

        Log.d(TAG+"_ex2",res);

        return runModel(input_val,outputValue);
    }

    private static float runModel(float[][] input_val, float[][] outputValue) {
        tfliteInterpreter.run(input_val, outputValue);

        float output[] = OrderFoodCommentActivity.outputValue[0];

        Float res = output[0];

        Log.d(TAG+"_res",String.valueOf(res));

        return res;

    }

    private MappedByteBuffer loadModelFile() throws IOException {
        // Open the model using an input stream, and memory map it to load
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("rnn_model_food_review_analysis.tflite");
        FileInputStream inputStream = new FileInputStream((fileDescriptor.getFileDescriptor()));
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void loadVocab() {

        AssetManager assetManager = getAssets();
        InputStream input;
        try {
            input = assetManager.open("food_review_vocab_word_to_idx_dict.txt");

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            Log.d(TAG+"_voc_size:", String.valueOf(size));



            // byte buffer into a string
            String text = new String(buffer);
            Log.d(TAG+"_vocab",text);

            String sub_str = text.substring(1,text.length()-1);
            String[] strs = sub_str.split(",");
            word_to_idx_dict = new HashMap<String, Float>();

            for(int i=0; i<strs.length;i++){
                String one_map = strs[i];
                int key_st = one_map.indexOf("\"");
                int key_end = one_map.indexOf(":")-1;
                String key = one_map.substring(key_st+1,key_end);

                int val_st = key_end+2;
                float value = Float.parseFloat(one_map.substring(val_st));
                word_to_idx_dict.put(key,value);
                //System.out.println(strs[i]);
            }

            Log.d(TAG+"_dict", String.valueOf(word_to_idx_dict));
            //Log.d("xlr8_map_tst",String.valueOf(word_to_idx_dict.get("movie")));


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG+"_vocabErr",e.getMessage());
        }
    }
}
