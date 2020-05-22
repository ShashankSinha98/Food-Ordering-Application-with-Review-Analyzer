package com.example.hrithik.btp.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hrithik.btp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private String TAG = "OTPActivity";

    ImageView otpenter;
    EditText otp;
    private String verificationid;
    private FirebaseAuth mAuth;
    TextView resendotp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        otpenter = findViewById(R.id.otpenter);
        otp = findViewById(R.id.otp);
        resendotp = findViewById(R.id.resendotp);

        final String phoneNumber = getIntent().getStringExtra("phonenumber");
        sendVerification(phoneNumber);

        mAuth = FirebaseAuth.getInstance();

        otpenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = otp.getText().toString();
                if (!phone.isEmpty() && phone.length() == 6) {
                    verifycode(phone);

                } else {
                    Toast.makeText(getApplicationContext(), "Invalid OTP!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerification(phoneNumber);
                Toast.makeText(getApplicationContext(), "OTP resent on your mobile number!", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void verifycode(String code) {
        Log.d(TAG, "Verify code");
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationid, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        Log.d(TAG, "Sign in with credentials");
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent otpv = new Intent(OTPActivity.this, MainActivity.class);
                    otpv.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(otpv);

                } else {
                    Toast.makeText(OTPActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendVerification(String number) {
        Log.d(TAG, "Sending Verification");
        Log.d(TAG, "Phone no: "+number);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                10,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );
        Toast.makeText(getApplicationContext(), "An OTP has been sent to your mobile number", Toast.LENGTH_SHORT).show();

    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {



        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
            Log.d(TAG, "code: "+verificationid);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifycode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    };


}

