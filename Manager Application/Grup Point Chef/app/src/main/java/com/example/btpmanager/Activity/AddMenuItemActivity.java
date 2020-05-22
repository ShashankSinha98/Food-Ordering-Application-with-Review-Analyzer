package com.example.btpmanager.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.example.btpmanager.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AddMenuItemActivity extends AppCompatActivity {

    private static final String TAG = "AddMenuItemActivity";
    private Toolbar mtoolbar;
    private CircleImageView foodImage;
    private EditText foodName, foodPrice, foodDesc;
    private RadioGroup cuisineGroup;
    private RadioButton selectedCuisineRadioBtn;
    private MaterialButton addItemBtn;
    private ConstraintLayout constraintLayout;

    private Bitmap compressedImageFile;


    private Uri mainImageURI = null;
    private Uri thumbImageUri = null;

    private Boolean isChanged = false;


    private FirebaseFirestore firebaseFirestore;
    private String foodCuisineCode, foodCode;
    private int selectedId;

    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private String foodNameSelected, foodPriceSelected, foodDescriptionSelected;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu_item);
        mtoolbar = findViewById(R.id.menu_toolbar);
        mtoolbar.setTitle("ADD NEW FOOD");
        //mtoolbar.inflateMenu(R.menu.savemenu);

        foodImage = findViewById(R.id.add_item_image);
        foodName = findViewById(R.id.add_item_food_name_et);
        foodPrice = findViewById(R.id.add_item_food_price_et);
        constraintLayout = findViewById(R.id.add_item_layout);
        foodDesc = findViewById(R.id.add_item_food_desc_et);
        cuisineGroup = findViewById(R.id.cuisine_group);
        addItemBtn = findViewById(R.id.add_item_button);
        progressDialog = new ProgressDialog(AddMenuItemActivity.this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        getFoodCodeFromServer();


        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedId = cuisineGroup.getCheckedRadioButtonId();
                selectedCuisineRadioBtn = findViewById(selectedId);
                
                foodCuisineCode = getFoodCuisineCode(selectedCuisineRadioBtn.getText().toString());

                Log.d(TAG,foodName.getText().toString().trim()+" : "+foodPrice.getText().toString().trim()+" : "+foodDesc.getText().toString().trim()
                +" : "+foodCuisineCode+" : "+foodCode);

                if(TextUtils.isEmpty(foodName.getText().toString()) || TextUtils.isEmpty(foodPrice.getText().toString()) || TextUtils.isEmpty(foodDesc.getText().toString()) || mainImageURI==null){
                    Snackbar bar = Snackbar.make(constraintLayout, "Please fill all details.", Snackbar.LENGTH_SHORT);
                    bar.show();
                } else {

                    foodNameSelected = foodName.getText().toString().trim();
                    foodDescriptionSelected = foodDesc.getText().toString().trim();
                    foodPriceSelected = foodPrice.getText().toString().trim();

                    final StorageReference image_path = storageReference.child("food_images").child(foodName.getText().toString().trim()+".jpg");

                    //TODO add progress dialog

                    progressDialog.setTitle("Saving Details");
                    progressDialog.setMessage("Please wait while we add your food to database");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    if(isChanged)
                    {

                        image_path.putFile(mainImageURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    progressDialog.dismiss();
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

                                    compressBitmap(downloadUri, foodName.getText().toString().trim());


                                } else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(AddMenuItemActivity.this,"UPLOAD Error : "+errorMessage,Toast.LENGTH_LONG).show();

                                }
                            }

                        });

                    }


                }
            }
        });

        foodImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AddMenuItemActivity.this, "Food Image Tapped", Toast.LENGTH_SHORT).show();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                {
                    if(ContextCompat.checkSelfPermission(AddMenuItemActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        ActivityCompat.requestPermissions(AddMenuItemActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

                    } else {

                        bringImagePicker();
                    }
                } else {

                    bringImagePicker();
                }

            }
        });




    }

    private void compressBitmap(final Uri downloadUri, String foodName) {


        Log.d(TAG, String.valueOf(downloadUri));

        File newImageFile = new File(mainImageURI.getPath());

        Log.d(TAG, String.valueOf(newImageFile));

        try {

            // Reducing Image Quality...
            compressedImageFile = new Compressor(AddMenuItemActivity.this)
                    .setMaxWidth(100)
                    .setMaxHeight(100)
                    .setQuality(2)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {

            String errorMessage = e.getMessage();
            Toast.makeText(AddMenuItemActivity.this,"Upload Error 1 : "+errorMessage,Toast.LENGTH_LONG).show();

        }

        Log.d(TAG, String.valueOf(compressedImageFile));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();


        // File path for Thumb Image...

        final StorageReference thumbFilePath = storageReference.child("food_images/thumbs").child(foodName+".jpg");


        //Uploading of thumb img...
        final UploadTask uploadTask = thumbFilePath.putBytes(thumbData);

        //Getting URI of thumb img...
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    progressDialog.dismiss();
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


                            Toast.makeText(AddMenuItemActivity.this,"The Image is Uploaded",Toast.LENGTH_LONG).show();
                            //progressDialog.dismiss();
                            storeToFirestore(downloadUri, thumb_Uri);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            String errorMessage = e.getMessage();
                            Toast.makeText(AddMenuItemActivity.this,"Upload Error 2 : "+errorMessage,Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    progressDialog.dismiss();
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(AddMenuItemActivity.this,"Upload Error 3 : "+errorMessage,Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void storeToFirestore( Uri downloadUri, Uri thumbUri) {


        // Storing data on Firestore...

        ArrayList<String> commentList = new ArrayList<>();
        commentList.add("null");

        Map<String, Object> foodMap = new HashMap<>();
        foodMap.put("foodName",foodNameSelected);
        foodMap.put("foodImage",downloadUri.toString());
        foodMap.put("foodThumbImage",thumbUri.toString());
        foodMap.put("foodDesc", foodDescriptionSelected);
        foodMap.put("foodPrice", foodPriceSelected);
        foodMap.put("foodCode", foodCode);
        foodMap.put("foodCuisineCode",foodCuisineCode);
        foodMap.put("foodPositiveReviewCount","0");
        foodMap.put("foodScore", "0");
        foodMap.put("foodTotalReviewCount","0");
        foodMap.put("foodComment", commentList);





        firebaseFirestore.collection("Food").add(foodMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                if(task.isSuccessful()){

                    firebaseFirestore.collection("Control Panel").document(
                            "7cVs8RdONsX5sVI1eoiQ").update("current_food_code",foodCode).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(AddMenuItemActivity.this, "Food Added Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });


                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(AddMenuItemActivity.this,"FIRESTORE Error : "+errorMessage,Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }
        });


    }



    private void getFoodCodeFromServer(){

        progressDialog.setTitle("Setting up Add Panel");
        progressDialog.setMessage("Please wait while we set up your add panel.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        try {
            firebaseFirestore.collection("Control Panel").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {

                        for (DocumentSnapshot doc : task.getResult()) {
                            String currCode = doc.getString("current_food_code");
                            foodCode = String.valueOf(Integer.valueOf(currCode) + 1);
                            progressDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(AddMenuItemActivity.this, "OOPS! Something went wrong.", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        } catch (Exception e){
            progressDialog.dismiss();
            Log.d(TAG, e.toString());
            Toast.makeText(this, "OOPS! Something went wrong.", Toast.LENGTH_SHORT).show();
        }


    }

    private String getFoodCuisineCode(String cuisineSelected) {

        String res = "";

        switch (cuisineSelected){
            case "Snacks":res="101";
                    break;
            case "Drinks":res="102";
                break;
            case "South Indian":res="103";
                break;
            case "Chinese":res="104";
                break;
            case "Shakes":res="105";
                break;
        }

        return res;

    }

    private void bringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                .start(AddMenuItemActivity.this);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageURI= result.getUri();
                foodImage.setImageURI(mainImageURI);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }


}