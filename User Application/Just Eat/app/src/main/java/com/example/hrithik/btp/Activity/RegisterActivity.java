package com.example.hrithik.btp.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.hrithik.btp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText reg_email;
    private EditText reg_pass;
    private EditText reg_confirm_pass;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressDialog reg_progress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        //Importing UI
        reg_email = findViewById(R.id.register_email_et);
        reg_pass = findViewById(R.id.register_password_et);
        reg_confirm_pass = findViewById(R.id.register_confirm_password_et);
        reg_btn = findViewById(R.id.create_new_account_btn);
        reg_login_btn = findViewById(R.id.already_have_account_btn);

        reg_progress = new ProgressDialog(this);


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = reg_email.getText().toString();
                String pass = reg_pass.getText().toString();
                String confirm_pass = reg_confirm_pass.getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirm_pass))
                {
                    if(pass.equals(confirm_pass))
                    {

                        reg_progress.setTitle("Creating New Account");
                        reg_progress.setMessage("Please wait while we create your new account !");
                        reg_progress.setCanceledOnTouchOutside(false);
                        reg_progress.show();

                        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {

                                    Intent setupIntent = new Intent(RegisterActivity.this,MainActivity.class);
                                    startActivity(setupIntent);
                                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                    finish();


                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error : "+errorMessage,Toast.LENGTH_LONG).show();

                                }

                                reg_progress.dismiss();
                            }
                        });

                    } else {

                        Toast.makeText(RegisterActivity.this,"Confirm Password and Password doesn't match! Please try Again.",Toast.LENGTH_LONG).show();
                    }
                } else {

                    Toast.makeText(RegisterActivity.this,"Fill up all the details.",Toast.LENGTH_LONG).show();
                }
            }
        });





        reg_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            sendToMain();
        }
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

    }
}
