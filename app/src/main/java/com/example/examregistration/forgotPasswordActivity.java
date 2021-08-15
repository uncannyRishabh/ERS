package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class forgotPasswordActivity extends AppCompatActivity {
    TextInputLayout email;
    Button forgot_password;
    FirebaseAuth mAuth;
    String mail= "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_700)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Recover Password");
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.fp_email);

        if(getIntent().getStringExtra("user_entered_mail") != null) {
            Objects.requireNonNull(email.getEditText()).setText(getIntent().getStringExtra("user_entered_mail"));
        }

        forgot_password = findViewById(R.id.fp_Button);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = Objects.requireNonNull(email.getEditText()).getText().toString().trim();
                mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(v,"Email sent to "+mail+" successfully", Snackbar.LENGTH_SHORT).show();
                        Thread t1 = new Thread(){
                            @Override
                            public void run() {
                                try {
                                    sleep(2000);
                                } catch (InterruptedException e) {
                                    Log.d("# THREAD #"," THREAD INTERRUPTED");
                                }
                                Intent i1 = new Intent(forgotPasswordActivity.this,SignInActivity.class);
                                startActivity(i1);
                            }
                        };
                        t1.start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(v,"Failed to send email", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }
}