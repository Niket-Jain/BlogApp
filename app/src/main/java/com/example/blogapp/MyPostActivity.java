package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class MyPostActivity extends AppCompatActivity {

    private static int MAX_LENGTH = 100;
    private ImageView myPostImageView;
    private EditText postDescriptionEditText;
    private Button postButton;
    private ProgressBar myPostProgressBar;
    private Toolbar myPosttoolbar;

    private Uri postImageURi;
    private String currentUSerId;

    private File compressedImageFile;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();

        currentUSerId= mAuth.getCurrentUser().getUid();

        myPostImageView= findViewById(R.id.newPostImageView);
        postDescriptionEditText= findViewById(R.id.postdescriptionEditText);
        postButton=findViewById(R.id.postButton);
        myPostProgressBar= findViewById(R.id.myPostProgressbar);

        myPosttoolbar = (Toolbar) findViewById(R.id.myPostToolBar);
        setSupportActionBar(myPosttoolbar);
        myPosttoolbar.setTitle("Add a post");

        myPostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(MyPostActivity.this);
            }
        });

       postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String description= postDescriptionEditText.getText().toString();

                if (!TextUtils.isEmpty(description) && postImageURi != null){

                    myPostProgressBar.setVisibility(View.VISIBLE);

                    final String randomID= UUID.randomUUID().toString();

                    final StorageReference filePath= storageReference.child("Post_Details").child(randomID+ ".jpg");

                    filePath.putFile(postImageURi).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){

                                // Compression of Image is left to do as for the thumbnails. Main problem was Context field.

                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        Map<String,Object> postMAP= new HashMap<>();
                                        postMAP.put("image_URL",uri.toString());
                                        postMAP.put("description",description);
                                        postMAP.put("user_id",currentUSerId);
                                        postMAP.put("timestamp",randomID);

                                        firebaseFirestore.collection("Posts").add(postMAP).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                                Toast.makeText(MyPostActivity.this, "Uploaded Successfull", Toast.LENGTH_SHORT).show();
                                                Intent mainIntent= new Intent(MyPostActivity.this,MainActivity.class);
                                                startActivity(mainIntent);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                myPostProgressBar.setVisibility(View.INVISIBLE);
                                                String errorMessage= e.getMessage();
                                                Toast.makeText(MyPostActivity.this, "Error:Outer" + errorMessage, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });

                            }
                        }
                    });

                }else{

                    Toast.makeText(MyPostActivity.this, "There is a problem in If statement", Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                postImageURi= result.getUri();
                myPostImageView.setImageURI(postImageURi);


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
                Toast.makeText(this, "Error:" + error.toString() , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}


