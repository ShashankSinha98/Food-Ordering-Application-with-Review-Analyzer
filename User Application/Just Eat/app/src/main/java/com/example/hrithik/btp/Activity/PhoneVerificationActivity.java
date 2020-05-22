package com.example.hrithik.btp.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.hrithik.btp.R;

public class PhoneVerificationActivity extends AppCompatActivity {
    ImageView enter;
    EditText phoneno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phoneverification);
        enter = findViewById(R.id.enterarrow);
        phoneno = findViewById(R.id.phoneno);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+91" + phoneno.getText().toString();
                if (!phone.isEmpty() && phone.length() == 13) {
                    Intent otpv = new Intent(PhoneVerificationActivity.this, OTPActivity.class);
                    otpv.putExtra("phonenumber", phone);
                    startActivity(otpv);

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid Input!", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


}
