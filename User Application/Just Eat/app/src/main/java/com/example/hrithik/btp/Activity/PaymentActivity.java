package com.example.hrithik.btp.Activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.hrithik.btp.Helper.JSONParser;
import com.example.hrithik.btp.R;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PaymentActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private String custId = "", orderId = "", merchantId = "", amountToPay = "";

    private static String TAG = "PaymentActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();

        orderId = intent.getExtras().getString("orderid");
        custId = intent.getExtras().getString("custid");
        amountToPay = intent.getExtras().getString("amount_to_pay");

        merchantId = "YOUR_PAYTM_MERCHANT_ID";

        sendUserDetailToServer d1 = new sendUserDetailToServer();
        d1.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {

        Log.d(TAG, "onTransactionResponse: "+String.valueOf(inResponse));
        String status = inResponse.getString("STATUS");


            Intent intent = new Intent(PaymentActivity.this, PaymentOrderStatusActivity.class);
            intent.putExtra("status",status);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();




    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

    }

    @Override
    public void onBackPressedCancelTransaction() {

        Log.d(TAG, "onBackPressedCancelTransaction called");
        Intent intent = new Intent(PaymentActivity.this, FoodMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();



    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

    }

    public class sendUserDetailToServer extends AsyncTask<ArrayList<String>, Void, String> {


        //http://babayaga98.epizy.com/generateChecksum.php
        String url = "https://babayaga19.000webhostapp.com/paytm/generateChecksum.php";
        String verifyUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        String CHECKSUMHASH = "";


        @Override
        protected void onPreExecute() {
            //Toast.makeText(PaymentActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"onPreExecute");

        }


        @Override
        protected String doInBackground(ArrayList<String>... arrayLists) {

            JSONParser jsonParser = new JSONParser(PaymentActivity.this);

            String param =
                    "MID=" + merchantId +
                            "&ORDER_ID=" + orderId +
                            "&CUST_ID=" + custId +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+amountToPay+"&WEBSITE=WEBSTAGING" +
                            "&CALLBACK_URL=" + verifyUrl + "&INDUSTRY_TYPE_ID=Retail";

            Log.e(TAG,param);

            JSONObject jsonObject = jsonParser.makeHttpRequest(url, "POST", param);

            // yaha per checksum ke saht order id or status receive hoga..
//            Log.e("CheckSum result >>", jsonObject.toString());

            if (jsonObject != null) {
                Log.e("CheckSum result >>", jsonObject.toString());
                try {
                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                    Log.e(TAG,"Checksum: " + CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG,"Exception 2: "+e.getMessage());

                }
            } else {
                // Toast.makeText(getApplicationContext(), "json Obj is null", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Json Obj is null!!");
            }

            return CHECKSUMHASH;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.d(TAG,"onPostExecute");
            Log.e(" setup acc ", "  signup result  " + result);
            //Toast.makeText(PaymentActivity.this, "Signup result : " + result, Toast.LENGTH_SHORT).show();

            PaytmPGService Service = PaytmPGService.getStagingService();

            HashMap<String, String> paramMap = new HashMap<String, String>();

            paramMap.put("MID", merchantId); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custId);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", amountToPay);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,verifyUrl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");


            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());


            Service.initialize(Order,null);

            // start payment service call here
            Service.startPaymentTransaction(PaymentActivity.this, true, true,
                    PaymentActivity.this  );
        }



    }

}
