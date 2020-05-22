package com.example.hrithik.btp.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hrithik.btp.R;

public class PranavActivity extends AppCompatActivity {

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pranav);

        button = findViewById(R.id.req_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reqData();
            }
        });
    }

    private void reqData() {

        final String url = "http://192.168.156.97:5000/api/v1/resources/food?text= Worst Pizza tasted ever";

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(PranavActivity.this, "Response: "+response, Toast.LENGTH_SHORT).show();
                Log.d("xlr8_resp",response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(PranavActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                Log.d("xlr8_error","ERROR: "+error);

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

        
    }

}
