package com.example.hrithik.btp.Activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hrithik.btp.Helper.DatePickerHelper;
import com.example.hrithik.btp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;


public class SetupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static String TAG = "SetupActivity";
    private ImageView calendarIcon;
    private TextView selectedDOB;
    private ConstraintLayout constraintLayout;

    private CircleImageView setupImage;
    private ProgressDialog setup_progress;



    private Uri mainImageURI = null;
    private Uri thumbImageUri = null;

    private EditText userFullNameET, userNumberET;
    private String userDOB = null, userGender;
    private Button setupBtn;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;



    private Boolean isChanged = false;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;

    private String userId;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setTitle(getResources().getString(R.string.user_setup));

        calendarIcon = findViewById(R.id.calendar_icon_iv);
        selectedDOB = findViewById(R.id.selected_dob_tv);
        userFullNameET = findViewById(R.id.setup_fullname_et);
        userNumberET = findViewById(R.id.setup_number_et);
        genderRadioGroup = findViewById(R.id.setup_gender_rg);
        setupBtn = findViewById(R.id.setup_setup_btn);
        constraintLayout = findViewById(R.id.setup_constraint_layout);

        setup_progress = new ProgressDialog(this);





        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerHelper();
                datePicker.show(getSupportFragmentManager(),"date picker");
            }
        });




        setupImage = findViewById(R.id.setup_image);

        // Selecting profile pic from external storage...
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    } else {

                        bringImagePicker();
                    }
                } else {

                    bringImagePicker();
                }
            }
        });




        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if(mAuth.getCurrentUser() != null) {

            userId = mAuth.getCurrentUser().getUid();

            Log.d(TAG, "UserId: "+userId);

            try {

                showAccountDetails();

            } catch (Exception e){
                setup_progress.dismiss();
                Log.d(TAG, "Exception: "+e.getMessage());
        }

    }else{
            Toast.makeText(this, "Please sign in first.", Toast.LENGTH_SHORT).show();
            this.finish();
        }


        storageReference = FirebaseStorage.getInstance().getReference();



        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = userFullNameET.getText().toString();
                final String user_number = userNumberET.getText().toString();
                final StorageReference image_path = storageReference.child("profile_images").child(userId+".jpg");
                final int selectedID = genderRadioGroup.getCheckedRadioButtonId();
                //Log.d(TAG, "SelectedID: "+selectedID);


                // Check if name and dp is not empty...
                if(!TextUtils.isEmpty(user_name) && mainImageURI != null && selectedID != -1 && !TextUtils.isEmpty(user_number) && userDOB != null)
                {
                    genderRadioButton = (RadioButton) findViewById(selectedID);
                    userGender = genderRadioButton.getText().toString();
                    Log.d(TAG, user_name + ", " + mainImageURI + ", " + userGender + ", " + userDOB + ", " + user_number);


                    setup_progress.setTitle("Saving Details");
                    setup_progress.setMessage("Please wait while we save your account details !");
                    setup_progress.setCanceledOnTouchOutside(false);
                    setup_progress.setCancelable(false);
                    setup_progress.show();

                    // If Image is Changed...

                    if(isChanged)
                    {

                        image_path.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }

                                // Continue with the task to get the download URL
                                return image_path.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful()) {

                                    Uri downloadUri = task.getResult();

                                    compressBitmap(downloadUri,user_name, user_number);


                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this,"UPLOAD Error : "+errorMessage,Toast.LENGTH_LONG).show();

                                }
                            }

                        });

                    }

                    else {

                        // Image Not Changed...

                        Uri downloadUri = mainImageURI;
                        Uri thumbUri = thumbImageUri;

                        storeToFirestore(user_name,user_number ,downloadUri, thumbUri);

                    }
                } else {
                    Snackbar snackbar = Snackbar.make(constraintLayout, "Please fill all details.", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

            }
        });






    }

    private void showAccountDetails(){
        // Show the Account Details / Retrieving Data...

        setup_progress.setTitle("Loading Details");
        setup_progress.setMessage("Please wait while we load your account details !");
        setup_progress.setCanceledOnTouchOutside(false);
        setup_progress.show();


        Log.d(TAG, "User ID: "+userId);


            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {

                        if (task.getResult().exists()) {

                            setupBtn.setEnabled(false);

                            String name = task.getResult().getString("name");
                            String image = task.getResult().getString("image");
                            String dob = task.getResult().getString("dob");
                            String gender = task.getResult().getString("gender");
                            String number = task.getResult().getString("number");
                            String thumb_image = task.getResult().getString("thumb_image");



                            // dob
                            selectedDOB.setTextColor(Color.BLACK);
                            selectedDOB.setText(dob);
                            calendarIcon.setEnabled(false);

                            // gender
                            genderRadioGroup.setEnabled(false);
                            RadioButton maleBtn = findViewById(R.id.male_rb);
                            RadioButton femaleBtn = findViewById(R.id.female_rb);
                            maleBtn.setEnabled(false);
                            femaleBtn.setEnabled(false);

                            if (gender.equals("Male")) {
                                maleBtn.setChecked(true);
                            } else {
                                femaleBtn.setChecked(true);
                            }

                            // number
                            userNumberET.setText(number);
                            userNumberET.setEnabled(false);

                            // image
                            thumbImageUri = Uri.parse(thumb_image);
                            mainImageURI = Uri.parse(image);
                            setupImage.setEnabled(false);


                            // name
                            userFullNameET.setText(name);
                            userFullNameET.setEnabled(false);

                            setupBtn.setAlpha(.5f);


                            RequestOptions placeHolderRequest = new RequestOptions();
                            placeHolderRequest.placeholder(R.drawable.default_image);

                            // Loading thumb img first, then original image...
                            Glide.with(SetupActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image)
                                    .thumbnail(Glide.with(SetupActivity.this).load(thumb_image))
                                    .into(setupImage);

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "FIRESTORE Retrieve Error : " + errorMessage, Toast.LENGTH_LONG).show();

                    }

                    //setupBtn.setEnabled(true);
                    setup_progress.dismiss();
                }
            });

    }

    private void bringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                .start(SetupActivity.this);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI= result.getUri();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }


    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        //String currDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        userDOB = day+" / "+(month+1)+"/ "+year;
        selectedDOB.setText(userDOB);
        selectedDOB.setTextColor(Color.BLACK);

        //Log.d(TAG, "Fn Arg Y: "+year+" M: "+month+" D: "+day);
        //Log.d(TAG, currDateString);






    }


    private void compressBitmap(final Uri downloadUri, final String user_name, final String user_number) {


        Log.d(TAG, String.valueOf(downloadUri));

        File newImageFile = new File(mainImageURI.getPath());

        Log.d(TAG, String.valueOf(newImageFile));

        try {

            // Reducing Image Quality...
            compressedImageFile = new Compressor(SetupActivity.this)
                    .setMaxWidth(100)
                    .setMaxHeight(100)
                    .setQuality(2)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {

            String errorMessage = e.getMessage();
            Toast.makeText(SetupActivity.this,"Upload Error 1 : "+errorMessage,Toast.LENGTH_LONG).show();

        }

        Log.d(TAG, String.valueOf(compressedImageFile));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();


        // File path for Thumb Image...

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference thumbFilePath = storageReference.child("profile_images/thumbs").child(user_id+".jpg");


        //Uploading of thumb img...
        final UploadTask uploadTask = thumbFilePath.putBytes(thumbData);

        //Getting URI of thumb img...
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return thumbFilePath.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if(task.isSuccessful())
                {

                    // Downloadable Thumb URI...
                    final Uri thumb_Uri = task.getResult();


                    // Success Listener for uploading Thumb URI...
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            Toast.makeText(SetupActivity.this,"The Image is Uploaded",Toast.LENGTH_LONG).show();
                            storeToFirestore(user_name, user_number,downloadUri, thumb_Uri);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            String errorMessage = e.getMessage();
                            Toast.makeText(SetupActivity.this,"Upload Error 2 : "+errorMessage,Toast.LENGTH_LONG).show();

                        }
                    });

                } else {

                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"Upload Error 3 : "+errorMessage,Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void storeToFirestore(String user_name, String user_number,  Uri downloadUri, Uri thumbUri) {


        // Storing data on Firestore...

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name",user_name);
        userMap.put("image",downloadUri.toString());
        userMap.put("thumb_image",thumbUri.toString());
        userMap.put("dob", userDOB);
        userMap.put("gender", userGender);
        userMap.put("number", user_number);



        firebaseFirestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful())
                {

                    sendToMain();


                } else {

                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this,"FIRESTORE Error : "+errorMessage,Toast.LENGTH_LONG).show();

                }

                setup_progress.dismiss();


            }
        });



    }

    private void sendToMain() {

        Toast.makeText(SetupActivity.this,"User Settings are updated!",Toast.LENGTH_LONG).show();
        Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();

    }


}
