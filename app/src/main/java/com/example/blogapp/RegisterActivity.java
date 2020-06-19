package com.example.blogapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccount_Button;
    private Button alreadyExist_Button;
    private ProgressBar regProgressBaar;
    private EditText regEmailText;
    private EditText regPasswordText;
    private EditText regConfirmPassText;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register1);

        CreateAccount_Button= findViewById(R.id.CreateAccount_Button);
        alreadyExist_Button= findViewById(R.id.alreadyExistButton);
        regProgressBaar= findViewById(R.id.regProgressBar);
        regEmailText= findViewById(R.id.regEmailText);
        regPasswordText= findViewById(R.id.regPasswordText);
        regConfirmPassText= findViewById(R.id.regConfirmPassText);

        mAuth= FirebaseAuth.getInstance();

        alreadyExist_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent= new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        CreateAccount_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regEmail= regEmailText.getText().toString();
                String regPass= regPasswordText.getText().toString();
                String confirmPass= regConfirmPassText.getText().toString();

                if (!TextUtils.isEmpty(regEmail) && !TextUtils.isEmpty(regPass) && !TextUtils.isEmpty(confirmPass)){

                    if (regPass.equals(confirmPass)){

                        regProgressBaar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(regEmail,regPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){
                                    // Go the setup Page.
                                    // New Account function is not working.It was not Sign in It was CreateUSer Method.
                                    Intent setupIntent= new Intent(RegisterActivity.this,SetupActivity.class);
                                    startActivity(setupIntent);

                                }else{

                                    String errorMessage= task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error:" + errorMessage , Toast.LENGTH_LONG).show();
                                }

                                regProgressBaar.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else{
                        Toast.makeText(RegisterActivity.this, "Both the Password doesn't matches ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user= mAuth.getCurrentUser();
        if (user != null){
            SendToMain();
        }
    }

    private void SendToMain() {

        Intent intent= new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

}
