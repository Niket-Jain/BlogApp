package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar_main;
    private Button addPostFloatingButton;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private String current_UserID;

    private HomeFragment homeFragment;
    private NotificationFragment notificationFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth= FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();

        toolbar_main =  findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
        toolbar_main.setTitle("Photo Blog");


        homeFragment= new HomeFragment();
        notificationFragment= new NotificationFragment();
        accountFragment= new AccountFragment();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);


        addPostFloatingButton= findViewById(R.id.addPostFloatingButton);

        addPostFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent myPostIntent = new Intent(MainActivity.this,MyPostActivity.class);
              startActivity(myPostIntent);
              Toast.makeText(MainActivity.this, "IS FloatingButton Working?", Toast.LENGTH_LONG).show();
            }
        });

        // For setting HomeFragment as a default page
        ReplaceFragment(homeFragment);

       bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {

               switch (item.getItemId()){

                   case R.id.bottom_home:
                       ReplaceFragment(homeFragment);
                       return true;

                   case R.id.notification_bottom:
                       ReplaceFragment(notificationFragment);
                       return true;
                   case R.id.account_bottom:
                       ReplaceFragment(accountFragment);
                       return true;

                   default:
                       Toast.makeText(MainActivity.this, "NOT possible", Toast.LENGTH_SHORT).show();
                       return false;
               }
           }
       });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null){
           SendToLoginPage();
        }else{

            current_UserID= mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()){
                        if (!task.getResult().exists()){

                            Intent setUpIntent= new Intent(MainActivity.this,SetupActivity.class);
                            startActivity(setUpIntent);
                        }
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String errorMsg = e.getMessage();
                    Toast.makeText(MainActivity.this, "Error:" + errorMsg , Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.logout_action:
                logout();
                return true;
            case R.id.setting_action:
                Intent settingIntent= new Intent(MainActivity.this,SetupActivity.class);
                startActivity(settingIntent);
            default:
                return false;
        }
    }

    private void logout() {
        mAuth.signOut();
        SendToLoginPage();
    }

    private void SendToLoginPage() {
        Intent intent= new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void ReplaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();
    }
}
