package com.example.examregistration;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    ImageView signup_logo;
    TextView signup_text;
    private GoogleSignInClient mGoogleSignInClient;
    private final int RC_SIGN_IN = 1;
    private int clicks = 0;
    FirebaseDatabase database;
    FirebaseAuth mAuth;

    public void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(mAuth!=null){
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(currentUser!=null){
//            Toast.makeText(LoginActivity.this,"uid : "+currentUser.getUid(),Toast.LENGTH_SHORT).show();
                    Intent i1 = new Intent(LoginActivity.this,dashboardActivity.class);
                    startActivity(i1);
                }
            },800);
        }
        else{
            mAuth = FirebaseAuth.getInstance();
            Handler handler = new Handler();
            handler.post(() -> {
                if(currentUser!=null){
                    Intent i1 = new Intent(LoginActivity.this,dashboardActivity.class);
                    startActivity(i1);
                }
            });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.R) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
        else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );

        }

        createRequest();
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        signup_logo = findViewById(R.id.LImage);
        signup_text = findViewById(R.id.continue_with);
        Button google = findViewById(R.id.google_SignIn);
        Button login_with_email = findViewById(R.id.email_signin);

        login_with_email.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams)v.getLayoutParams();
                Log.d("TAG", "onApplyWindowInsets: bottom Margin "+insets.getSystemWindowInsetBottom());
                params.bottomMargin = insets.getSystemWindowInsetBottom();
                v.setLayoutParams(params);
                return null;
            }
        });

        signup_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicks+=1;
                if(clicks==7){
                    signup_text.setVisibility(View.INVISIBLE);
                    google.setVisibility(View.INVISIBLE);
                    login_with_email.setVisibility(View.INVISIBLE);
                    ((MotionLayout)findViewById(R.id.LoginImage)).transitionToEnd();
//                    Toast.makeText(LoginActivity.this, "ADMIN MODE", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LoginActivity.this,AdminLogin.class);
                            startActivity(intent);
                        }
                    },550);
                }
            }
        });

        login_with_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(LoginActivity.this,SignInActivity.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View,String>(signup_logo,"splash_logo_transition");
                pairs[1] = new Pair<View,String>(signup_text, "splash_text_transition");
                pairs[2] = new Pair<View,String>(login_with_email, "email_button_transition");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this,pairs);
                startActivity(i1,options.toBundle());
            }
        });


        //Google login
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                login_with_email.setClickable(false);
                google.setClickable(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        login_with_email.setClickable(true);
                        google.setClickable(true);
                    }
                },5000);
            }
        });
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    private void createDBEntry(FirebaseUser user) {
        String uid = user.getUid()+"";
        DatabaseReference myRef = database.getReference("users");

        Map<String,Object> entry = new HashMap<>();
        entry.put("mail",user.getEmail());
        entry.put("method","Google");
        entry.put("name",user.getDisplayName());
        Log.d("*## DB entry ##*","uid : "+uid+" DB entry succeeded");

        //TODO:surround with try/catch
        myRef.child(uid).child("login deets").updateChildren(entry);
    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser!=null){
            createDBEntry(currentUser);
            Intent i1 = new Intent(LoginActivity.this,dashboardActivity.class);
            startActivity(i1);
        }
        else
            Log.d("DEBUG  : ","AUTH FAILED" );
    }

}