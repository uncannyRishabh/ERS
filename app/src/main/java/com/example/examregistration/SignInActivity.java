package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    TextInputLayout email,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().hide();

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.R) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
        else{
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }

        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email_signin);
        password = findViewById(R.id.login_password);

        TextView signUp = findViewById(R.id.new_user_signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(SignInActivity.this,RegisterActivity.class);
                startActivity(i1);
            }
        });

        TextView forgot_password = findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(SignInActivity.this,forgotPasswordActivity.class);
                if(!Objects.requireNonNull(email.getEditText()).getText().toString().equals("")) {
                    i1.putExtra("user_entered_mail", email.getEditText().getText().toString());
                }
                startActivity(i1);
            }
        });

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateFields()) {
                    loginUser(v);
                }
            }
        });
    }

    private boolean validateFields() {
        int val_score=0;
        if(!Objects.requireNonNull(email.getEditText()).getText().toString().equals("")){
            email.setError(null);
            val_score+=1;
        }
        else {
            email.setError("Fields cannot be empty");
            val_score+=0;
        }
        if(!Objects.requireNonNull(password.getEditText()).getText().toString().equals("")){
            password.setError(null);
            val_score+=1;
        }
        else{
            password.setError("Fields cannot be empty");
            val_score+=0;
        }

        if(val_score==2)
            return true;
        else
            return false;
    }

    private void loginUser(View v) {
        String mail = email.getEditText().getText().toString().trim();
        String pass = password.getEditText().getText().toString().trim();
        mAuth.signInWithEmailAndPassword(mail, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("DEBUG : ", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("DEBUG : ", "signInWithEmail:failure", task.getException());
                            Snackbar.make(v,"Incorrect email or password entered",Snackbar.LENGTH_LONG).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            Intent i1 = new Intent(SignInActivity.this,dashboardActivity.class);
            startActivity(i1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}