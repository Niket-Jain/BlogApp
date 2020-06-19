package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPasswordText;
    private ImageView loginLogoImageView;
    private Button loginButton;
    private Button registerButton;
    private ProgressBar loginProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginProgressBar = findViewById(R.id.regProgressBar);
        loginEmailText= (EditText) findViewById(R.id.regEmailText);
        loginPasswordText= (EditText) findViewById(R.id.regPasswordText);
        loginLogoImageView= findViewById(R.id.loginLogoImageView);
        loginButton= (Button) findViewById(R.id.CreateAccount_Button);
        registerButton= (Button) findViewById(R.id.alreadyExistButton);

        mAuth= FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent regIntent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loginText= loginEmailText.getText().toString();
                String passwordText= loginPasswordText.getText().toString();

                if (!TextUtils.isEmpty(loginText) && !TextUtils.isEmpty(passwordText)) {

                    loginProgressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                //Go to main Activity.

                                SendToMain();
                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }

                            loginProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
                }



            }
        });
    }

    private  void SendToMain(){
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            SendToMain();
        }
    }
}
