package com.example.hrithik.btp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.hrithik.btp.Helper.ConnectionDetector;
import com.example.hrithik.btp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME = 1500;
    private ConnectionDetector connectionDetector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        connectionDetector = new ConnectionDetector(getApplicationContext());

        // Checking for INTERNET CONNECTIVITY
        if (connectionDetector.isNetworkConnectionAvailable()) {
            // NETWORK AVAILABLE
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    // removing progress bar
            //        findViewById(R.id.loadingPanel).setVisibility(View.GONE);

                    // checking if user is logged in or not
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        // USER LOGGED IN. Directing inside app.
                        Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                    } else {
                        // USER NOT LOGGED IN. Directing to LOG IN page.
                        Intent verificationIntent = new Intent(SplashScreenActivity.this, PhoneVerificationActivity.class);
                        startActivity(verificationIntent);
                    }
                }
            }, SPLASH_TIME);
        } else {

            // NETWORK NOT AVAILABLE- Showing dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Internet Connection");
            builder.setMessage("Please turn on internet connection to continue...");
            builder.setIcon(R.drawable.fail);
            builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        }

    }


}





