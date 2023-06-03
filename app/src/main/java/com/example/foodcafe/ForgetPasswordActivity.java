package com.example.foodcafe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText email;
    TextView resetpass;
    LottieAnimationView progressbar;
    FirebaseAuth fireAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        email = findViewById(R.id.email);
        resetpass = findViewById(R.id.resetpass);

        progressbar = findViewById(R.id.progressbar);

        fireAuth = FirebaseAuth.getInstance();

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

        resetpass.setOnClickListener(view -> {

            progressbar.setVisibility(View.VISIBLE);
            resetpass.setEnabled(false);
            resetpass.setTextColor(Color.argb(50,0,0,0));

            fireAuth.sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgetPasswordActivity.this, "Email sent successfully!", Toast.LENGTH_SHORT).show();
                            }else{
                                resetpass.setEnabled(true);
                                resetpass.setTextColor(Color.rgb(0,0,0));
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            progressbar.setVisibility(View.GONE);
                        }
                    });
        });

    }

    private void checkInputs() {
        if (TextUtils.isEmpty(email.getText())){
            resetpass.setEnabled(false);
            resetpass.setTextColor(Color.argb(50,0,0,0));
        }else{
            resetpass.setEnabled(true);
            resetpass.setTextColor(Color.rgb(0,0,0));
        }
    }

}