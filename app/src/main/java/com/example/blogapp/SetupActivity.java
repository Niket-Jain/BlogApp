package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity {

   private CircleImageView circleImageView;
   private Uri mainImageURI= null;
   private EditText usernameEditText;
   private Button updateButton;
   private ProgressBar setup_progressBar;

   private StorageReference storageReference;
   private FirebaseAuth firebaseAuth;
   private FirebaseFirestore firebaseFirestore;
   private  String user_id;

   private boolean isChanaged= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        final Toolbar setUpToolbar= findViewById(R.id.setUpToolbar);
        setSupportActionBar(setUpToolbar);
        setUpToolbar.setTitle("Setup Profile");

        circleImageView= findViewById(R.id.circleImageView);
        usernameEditText= findViewById(R.id.usernameEditText);
        updateButton= findViewById(R.id.updateButton);
        setup_progressBar= findViewById(R.id.setup_progressBAr);

        firebaseAuth= FirebaseAuth.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore= FirebaseFirestore.getInstance();
        user_id= firebaseAuth.getCurrentUser().getUid();

        setup_progressBar.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {

                    if (task.getResult().exists()) {
                        Toast.makeText(SetupActivity.this, "Data Exists", Toast.LENGTH_SHORT).show();

                        String name= task.getResult().getString("name");
                        String image= task.getResult().getString("image");

                        mainImageURI= Uri.parse(image);

                        usernameEditText.setText(name);

//This line of code is for holding an image when the data is loading. Where as Glide is an open source library to change the image View to some kind of String.
// So that we can safe it to the app and Reflect back to the Circle Image View.
// We are going to safe the data in Offline mode as well.

                        RequestOptions placeHolderRequest= new RequestOptions();
                        placeHolderRequest.placeholder(R.drawable.pic);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image).into(circleImageView);


                    }
                }else{
                    String error= task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, "Exception Error: " + error, Toast.LENGTH_SHORT).show();
                }

                setup_progressBar.setVisibility(View.INVISIBLE);
                updateButton.setEnabled(true);
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernameEditText.getText().toString();

                if (!TextUtils.isEmpty(username) && mainImageURI != null) {

                if (isChanaged) {

                        setup_progressBar.setVisibility(View.VISIBLE);
                        user_id = firebaseAuth.getCurrentUser().getUid();

                        final StorageReference img_path = storageReference.child("Profile_Pictures").child(user_id + ".jpg");

                        // This below code is to upload Image to the firebase database. "getDownloadurl" is not applicable now with "task.getresult.getdownload". So we are using different method here.

                        img_path.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                img_path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        StoreInFireStore(uri, username);
                                    }

                                }).addOnFailureListener(new OnFailureListener() { // this is working as ELSE in the Video.
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        String ErrorMessage = e.getMessage();
                                        Toast.makeText(SetupActivity.this, "Error: " + ErrorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        });
                    }
                    else{
                        StoreInFireStore(null,username);
                        }
                }
            }
        });

      circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }

                    else{
                        BringImagePickeer();
                    }

                }else{
                    BringImagePickeer();
                }

            }
        });
    }

    private void StoreInFireStore(Uri uri,String username) {

        Uri downloadURI;

        if (uri != null) {
           downloadURI = uri;

        } else {
            downloadURI= mainImageURI;
        }

        Toast.makeText(SetupActivity.this, "The Image is uploaded", Toast.LENGTH_SHORT).show();
        setup_progressBar.setVisibility(View.INVISIBLE);

        Map<String,String> userMap= new HashMap<>();
        userMap.put("name",username);
        userMap.put("image",downloadURI.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(SetupActivity.this, "The User settings are updated", Toast.LENGTH_SHORT).show();
                Intent mainIntent= new Intent(SetupActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String exception= e.getMessage();
                Toast.makeText(SetupActivity.this, "FireStore Error: " + exception, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BringImagePickeer() {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(SetupActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mainImageURI = result.getUri();

                circleImageView.setImageURI(mainImageURI);

                isChanaged=true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();
            }
        }
    }
}
