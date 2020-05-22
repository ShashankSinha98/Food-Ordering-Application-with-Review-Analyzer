package com.example.hrithik.btp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScanActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    private final String TAG = "QRScanActivity";
    private AlertDialog alertDialog;
    private String scanResult = "";
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan Result");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            scannerView.resumeCameraPreview(QRScanActivity.this);
            }
        });





        builder.setMessage(scanResult);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        scannerView.setAutoFocus(true);
        //scannerView.setFlash(true);
        callTimer();

    }

    private void callTimer() {
        final Timer t = new Timer();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {

                        if (this != null)
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Log.d(TAG, String.valueOf(scannerView.hasFocus()));
                                    if(!scannerView.hasFocus()){
                                        scannerView.requestFocus();

                                    }

                                }
                            });


                    }

                }, 0, 1000);

            }
        }, 0);


    }


    @Override
    public void handleResult(Result result) {
       // Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_SHORT).show();
        // onBackPressed();

        //Todo REPLACE WITH ACTUAL CODE
       /* scanResult = result.getText();
        builder.setNeutralButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String escapedQuery = null;
                try {
                    escapedQuery = URLEncoder.encode(scanResult, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if(escapedQuery!=null){
                    Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                    startActivity(intent);
                    finish();
                }
            }
        });
        builder.setMessage(scanResult);
        alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show(); */

       startActivity(new Intent(QRScanActivity.this, FoodMenuActivity.class));


    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}
