package com.example.foodcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    Activity activity = this;
    TextView signin;
    EditText email,name,pass,cpass;
    LottieAnimationView progressbar;
    TextView signup;
    FirebaseAuth fireAuth;
    FirebaseFirestore firestore;
    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        signin = findViewById(R.id.signin);
        email = findViewById(R.id.email);
        name = findViewById(R.id.name);
        pass = findViewById(R.id.pass);
        cpass = findViewById(R.id.cpass);
        progressbar = findViewById(R.id.progressbar);
        signup = findViewById(R.id.signup);

        fireAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        signin.setOnClickListener(view -> {
            startActivity(new Intent(activity,LoginActivity.class));
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
        name.addTextChangedListener(new TextWatcher() {
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
        cpass.addTextChangedListener(new TextWatcher() {
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

        signup.setOnClickListener(view -> {
            checkEmailAndPassword();
        });

    }

        private void checkInputs() {
            if (!TextUtils.isEmpty(email.getText())){
                if (!TextUtils.isEmpty(name.getText())){
                    if(!TextUtils.isEmpty(pass.getText()) && pass.length() >= 6){
                        if(!TextUtils.isEmpty(cpass.getText())){
                            signup.setEnabled(true);
                            signup.setTextColor(Color.rgb(0,0,0));
                        }else{
                            signup.setEnabled(false);
                            signup.setTextColor(Color.argb(50,0,0,0));
                        }
                    }else{
                        signup.setEnabled(false);
                        signup.setTextColor(Color.argb(50,0,0,0));
                    }
                }else{
                    signup.setEnabled(false);
                    signup.setTextColor(Color.argb(50,0,0,0));
                }
            }else{
                signup.setEnabled(false);
                signup.setTextColor(Color.argb(50,0,0,0));
            }
        }

        private void checkEmailAndPassword() {
            if(email.getText().toString().matches(emailpattern)){
                if(pass.getText().toString().equals(cpass.getText().toString())){

                    progressbar.setVisibility(View.VISIBLE);
                    signup.setEnabled(false);
                    signup.setTextColor(Color.argb(50,0,0,0));

                    fireAuth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){

                                        Map<String,Object> userdata = new HashMap<>();
                                        userdata.put("name",name.getText().toString());
                                        userdata.put("email",email.getText().toString());

                                        firestore.collection("USERS").document(fireAuth.getUid())
                                                .set(userdata)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                    CollectionReference userDataReference = firestore.collection("USERS").document(fireAuth.getUid()).collection("USER_DATA");

                                                                    //maps
                                                                    Map<String ,Object> wishListMap = new HashMap<>();
                                                                    wishListMap.put("list_size",(long)0);

                                                                    Map<String ,Object> ratingsMap = new HashMap<>();
                                                                    ratingsMap.put("list_size",(long)0);

                                                                    Map<String ,Object> cartMap = new HashMap<>();
                                                                    cartMap.put("list_size",(long)0);

                                                                    Map<String ,Object> myAddressesMap = new HashMap<>();
                                                                    myAddressesMap.put("list_size",(long)0);

                                                                    Map<String ,Object> notificationsMap = new HashMap<>();
                                                                    notificationsMap.put("list_size",(long)0);
                                                                    //maps

                                                                    List<String> documentNames = new ArrayList<>();
                                                                    documentNames.add("MY_WISHLIST");
                                                                    documentNames.add("MY_RATINGS");
                                                                    documentNames.add("MY_CART");
                                                                    documentNames.add("MY_ADDRESSES");
                                                                    documentNames.add("MY_NOTIFICATIONS");

                                                                    List<Map<String ,Object>> documentFields = new ArrayList<>();
                                                                    documentFields.add(wishListMap);
                                                                    documentFields.add(ratingsMap);
                                                                    documentFields.add(cartMap);
                                                                    documentFields.add(myAddressesMap);
                                                                    documentFields.add(notificationsMap);

                                                                    for (int x = 0 ; x < documentNames.size() ; x++){
                                                                        int finalX = x;
                                                                        userDataReference.document(documentNames.get(x))
                                                                                .set(documentFields.get(x))
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            if (finalX == documentNames.size() - 1) {
                                                                                                Intent mainIntent = new Intent(activity, LoginActivity.class);
                                                                                                startActivity(mainIntent);
                                                                                                Toast.makeText(activity, "Register successfully!", Toast.LENGTH_SHORT).show();
                                                                                                activity.finish();
                                                                                            }
                                                                                        }else {
                                                                                            progressbar.setVisibility(View.INVISIBLE);
                                                                                            signup.setEnabled(true);
                                                                                            signup.setTextColor(Color.rgb(0,0,0));
                                                                                            String error = task.getException().getMessage();
                                                                                            Toast.makeText(activity, "Something went wrong... please try again", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }
                                                                }else{
                                                                    String error = task.getException().getMessage();
                                                                    Toast.makeText(activity, "Something went wrong... please try again", Toast.LENGTH_SHORT).show();
                                                                }
                                                                signup.setEnabled(true);
                                                                signup.setTextColor(Color.rgb(0,0,0));
                                                            }
                                                        });
                                    }else{
                                        progressbar.setVisibility(View.INVISIBLE);
                                        signup.setEnabled(true);
                                        signup.setTextColor(Color.rgb(0,0,0));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(activity, "Something went wrong... please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    cpass.setError("Password doesn't matched!");
                }
            }else{
                email.setError("Invalid Email!");
            }
        }

}