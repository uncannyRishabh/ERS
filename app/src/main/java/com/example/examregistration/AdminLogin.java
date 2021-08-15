package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class AdminLogin extends AppCompatActivity {
    ScrollView scrollView;
    TextInputLayout email,pass;
    Button login;
    FirebaseAuth mAuth;

    private String mail,password;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_700)));
        getSupportActionBar().setTitle("Admin Mode");

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.admin_mail);
        pass = findViewById(R.id.admin_pass);
        login = findViewById(R.id.admin_btn);
        scrollView = findViewById(R.id.admin_scroll);

        login.setOnClickListener(v -> {
            if(fieldsNotEmpty()){
                loginUser(v);
                login.setBackgroundColor(Color.parseColor("#D6D9EA"));
                login.setClickable(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        login.setBackgroundColor(Color.parseColor("#8C9EFF"));
                        login.setClickable(true);
                    }
                },5000);
            }
        });
    }

    private boolean fieldsNotEmpty() {
        int val = 0;
        if(email.getEditText().getText().toString().equals("")){
            email.setError("Field cannot be empty");
        }
        else {
            email.setError("");
            val+=1;
        }
        if(pass.getEditText().getText().toString().equals("")){
            pass.setError("Field cannot be empty");
        }
        else {
            pass.setError("");
            val+=1;
        }

        if(val==2) return true;
        else return false;
    }

    private void getAdminCredentials() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("admincred/"+id);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String adminuid = snapshot.child("adminuid").getValue(String.class);
                    String adminpass = snapshot.child("pass").getValue(String.class);
                    Log.d("LOG TAG", "onDataChange: id : "+adminuid);
                    if(Objects.equals(adminuid,mail) && Objects.equals(adminpass,password)){
                        updateUI(currentUser);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(" TAG ", "onCancelled: error getting admin creds");
                Snackbar.make(scrollView,"Unable to connect to servers", Snackbar.LENGTH_SHORT).setAction("RETRY", v -> getAdminCredentials()).show();
            }
        });
    }

    private void loginUser(View v) {
        mail = email.getEditText().getText().toString().trim();
        password = pass.getEditText().getText().toString().trim();
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        getAdminCredentials();
                        Log.d("DEBUG : ", "signInWithEmail:success");
                    } else {
                        Log.w("DEBUG : ", "signInWithEmail:failure", task.getException());
                        Snackbar.make(v,"Incorrect email or password entered",Snackbar.LENGTH_LONG).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            Intent i1 = new Intent(AdminLogin.this,AdminActivity.class);
            startActivity(i1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AdminLogin.this,LoginActivity.class);
        startActivity(intent);
    }
}