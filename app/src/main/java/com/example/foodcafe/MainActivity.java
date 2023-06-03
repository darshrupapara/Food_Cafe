package com.example.foodcafe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Activity activity = this;
    TextView b1,b2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1 = findViewById(R.id.b1);
        b2 = findViewById(R.id.b2);

        b1.setOnClickListener(view -> {
            startActivity(new Intent(activity,LoginActivity.class));
        });

        b2.setOnClickListener(view -> {
           startActivity(new Intent(activity,RegistrationActivity.class));
        });
    }
}