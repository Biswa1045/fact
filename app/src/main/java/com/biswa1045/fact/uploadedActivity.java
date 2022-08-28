package com.biswa1045.fact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class uploadedActivity extends AppCompatActivity {
     ImageView img;
     CardView post_btn;
     Uri filePath,imguri_crped;
     FirebaseStorage storage;
     StorageReference storageReference;
     ImageView back_upload;
     private final int PICK_IMAGE_REQUEST = 22;
     private final String SAMPLE_CROPPED_IMG = "CropFact";
     private FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploaded);
        post_btn = findViewById(R.id.post_btn);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
       // post_btn.setVisibility(View.INVISIBLE);

        img = findViewById(R.id.img);
        back_upload = findViewById(R.id.back_upload);


        SelectImage();


        back_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),userActivity.class));
//                overridePendingTransition(0,0);

            }
        });
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });
    }

    private void startCrop(@NonNull Uri uri){
        String destinationFileName = SAMPLE_CROPPED_IMG;
        destinationFileName +=".jpg";
        UCrop uCrop = UCrop.of(uri,Uri.fromFile(new File(getCacheDir(),destinationFileName)));
        uCrop.withAspectRatio(2,3);
        uCrop.withMaxResultSize(450,450);
        uCrop.withOptions(getCropOptions());
        uCrop.start(uploadedActivity.this);
    }

    private UCrop.Options getCropOptions(){

        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(90);
        //options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(false);
        options.setStatusBarColor(getResources().getColor(R.color.front_main));
        options.setToolbarColor(getResources().getColor(R.color.front_main));
        options.setToolbarTitle("cropping");
        return options;
    }

    private void SelectImage()
    {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            startCrop(filePath);
        }else if(requestCode == UCrop.REQUEST_CROP) {
            imguri_crped = UCrop.getOutput(data);
                if (imguri_crped != null) {
                    img.setImageURI(imguri_crped);
                  }
        }else if(requestCode ==UCrop.RESULT_ERROR){
                final Throwable cropError = UCrop.getError(data);
                Toast.makeText(uploadedActivity.this, "please try again later", Toast.LENGTH_SHORT).show();
                }


    }
    private void uploadImage()
    {
        if (imguri_crped != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("uploads").child(firebaseUser.getUid()+"_"+currentDateandTime+".jpg");

            // adding listeners on upload
            // or failure of image
            ref.putFile(imguri_crped).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(uploadedActivity.this, "Image Uploaded!!",Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(uploadedActivity.this, "Image Upload Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        }
    }

    private void count(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Facts_image");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count =snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void upload(String factid_ul,String url_us,String uploader_img_us,String uploader_name_us){
        Map<String, Object> us = new HashMap<>();
        us.put("imageUri",url_us);
        FirebaseFirestore.getInstance().collection("User")
                .document(firebaseUser.getUid())
                .collection("uploads")
                .document(factid_ul)
                .set(us)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Facts_image");
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long count =snapshot.getChildrenCount();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "check network connection", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),userActivity.class));
        overridePendingTransition(0,0);
    }
}