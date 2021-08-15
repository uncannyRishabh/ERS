package com.example.examregistration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout name,mail,password,confirm_password;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    String user_name;
    String confirm_pass,email="null",pass="null";
    String uid;
//    int n_user=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Button registerButton = findViewById(R.id.registerButton);
        name = findViewById(R.id.register_name);
        mail = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        confirm_password = findViewById(R.id.register_confirm_password);

        registerButton.setOnClickListener(v -> {
            user_name = name.getEditText().getText().toString().trim();
            confirm_pass = confirm_password.getEditText().getText().toString().trim();

            if(validateFields()) {
                if (validatePassword(password.getEditText().getText().toString(), confirm_password.getEditText().getText().toString())) {
                    registerUser();
                    updateUI(mAuth.getCurrentUser());
                } else {
                    confirm_password.setError("Passwords do not match");
                }
            }
        });
    }

    private boolean validateFields() {
        int valid=0;
        if(name.getEditText().getText().toString().equals("")){
            name.setError("Field cannot be empty");
            valid+=0;
        }
        else {
            name.setError(null);
            valid+=1;
        }

        if(mail.getEditText().getText().toString().equals("")){
            mail.setError("Field cannot be empty");
            valid+=0;
        }
        else{
            mail.setError(null);
            valid+=1;
        }

        if(password.getEditText().getText().toString().equals("")){
            password.setError("Field cannot be empty");
            valid+=0;
        }
        else{
            password.setError(null);
            valid+=1;
        }

        if(confirm_password.getEditText().getText().toString().equals("")){
            confirm_password.setError("Field cannot be empty");
            valid+=0;
        }
        else{
            confirm_password.setError(null);
            valid+=1;
        }

        Log.d("VALID SCORE : ",String.valueOf(valid));
        if(valid==4)
            return true;
        else
            return false;
    }

    private Boolean validatePassword(String password, String confirm_pass) {
        if (password.equals(confirm_pass)) {
            confirm_password.setError(null);
            Log.d("###PASSWORD MISMATCH##", " PASSWORD MATCHES " + password + "&" + confirm_pass);
            return true;
        } else{
            confirm_password.setError("Passwords do not match");
            Log.d("###PASSWORD MISMATCH##", " PASSWORD DO NOT MATCH " + password + "&" + confirm_pass);
            return false;
        }
    }

    private void registerUser() {
        email = Objects.requireNonNull(mail.getEditText()).getText().toString().trim();
        pass = Objects.requireNonNull(password.getEditText()).getText().toString().trim();

        Log.d("#*# DEBUG #*#", "username: "+user_name+" email : "+email+" pass : "+pass);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DEBUG : ", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();

                            updateUI(user);
                        } else {
                            Log.w("DEBUG : ", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void createDBEntry(FirebaseUser user) {
        String uid = user.getUid()+"";
        DatabaseReference myRef = database.getReference("users");

        Map<String,Object> entry = new HashMap<>();
        entry.put("mail",email);
        entry.put("method","Email");
        entry.put("name",user_name);
        Log.d("*## DB entry ##*","uid : "+uid+" DB entry failed");
        //TODO:surround with try/catch
        myRef.child(uid).child("login deets").updateChildren(entry);
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            createDBEntry(user);
            Intent i1 = new Intent(RegisterActivity.this,dashboardActivity.class);
            i1.putExtra("user_name",user_name);
            startActivity(i1);

            //Toast.makeText(RegisterActivity.this,"uid : "+user.getUid(),Toast.LENGTH_SHORT).show();
            Log.d("*##* UID 01 *##* : ",user.getUid());
        }
    }
}