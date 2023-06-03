package com.example.foodcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    Activity activity = this;
    EditText email,pass;
    LottieAnimationView progressbar;
    TextView signin;
    TextView signup;
    TextView forgetpass;
    FirebaseAuth fireAuth;
    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);

        progressbar = findViewById(R.id.progressbar);

        signin = findViewById(R.id.signin);
        signup = findViewById(R.id.signup);
        forgetpass = findViewById(R.id.forgetpass);

        fireAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(view -> {
            startActivity(new Intent(activity,RegistrationActivity.class));
        });

        forgetpass.setOnClickListener(view -> {
            startActivity(new Intent(activity,ForgetPasswordActivity.class));
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signin.setOnClickListener(view -> {
            checkEmailAndPassword();
        });

    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(email.getText())){
            if (!TextUtils.isEmpty(pass.getText())){
                signin.setEnabled(true);
                signin.setTextColor(Color.rgb(0,0,0));
            }else{
                signin.setEnabled(false);
                signin.setTextColor(Color.argb(50,0,0,0));
            }
        }else{
            signin.setEnabled(false);
            signin.setTextColor(Color.argb(50,0,0,0));
        }
    }

    private void checkEmailAndPassword() {
        if (email.getText().toString().matches(emailpattern)){
            if (pass.length() >= 6){

                progressbar.setVisibility(View.VISIBLE);
                signin.setEnabled(false);
                signin.setTextColor(Color.argb(50,0,0,0));

                fireAuth.signInWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    Toast.makeText(activity, "Login successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(activity,NavigationActivity.class));
                                    finish();
                                }else{
                                    progressbar.setVisibility(View.INVISIBLE);
                                    signin.setEnabled(true);
                                    signin.setTextColor(Color.rgb(0,0,0));
                                    String error = task.getException().getMessage();
                                    Toast.makeText(activity, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                                }
                                signin.setEnabled(true);
                                signin.setTextColor(Color.rgb(0,0,0));
                            }
                        });
            }else{
                Toast.makeText(activity, "Incorrect password!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(activity, "Incorrect email!", Toast.LENGTH_SHORT).show();
        }
    }

}