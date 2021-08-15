package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergivonavi.materialbanner.Banner;
import com.sergivonavi.materialbanner.BannerInterface;

import java.util.Objects;

public class dashboardActivity extends AppCompatActivity {
    TextView welcome_user;
    Button enroll,view_status,edit_profile,logout;
    String name = "USER";
    private boolean chk = false;
    private FirebaseAuth mAuth;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        chk = checkEnrolled();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_700)));
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        getWindow().setEnterTransition(new Fade());
        setTitle("Dashboard");

        mAuth = FirebaseAuth.getInstance();
        chk = checkEnrolled(); //TODO: CAUSING UNKNOWN ERROR
        if(mAuth!=null){
            getDisplayName(mAuth);
        }
        else {
            mAuth = FirebaseAuth.getInstance();
        }

        welcome_user = findViewById(R.id.welcome_user);
        String welcome_text = "WELCOME " + name.toUpperCase();
        welcome_user.setText(welcome_text);
        enroll = findViewById(R.id.dash_btn_1);
        view_status = findViewById(R.id.dash_btn_2);
        edit_profile = findViewById(R.id.dash_btn_3);
        logout = findViewById(R.id.dash_btn_4);

        enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(dashboardActivity.this,enrollActivity.class);
                startActivity(i1, ActivityOptions.makeSceneTransitionAnimation(dashboardActivity.this).toBundle());
            }
        });

        logout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent i1 = new Intent(dashboardActivity.this,LoginActivity.class);
                    startActivity(i1);
                }
            }
        });
        Banner banner = findViewById(R.id.banner);
        banner.setLeftButtonListener(new BannerInterface.OnClickListener() {
            @Override
            public void onClick(BannerInterface banner) {
                banner.dismiss();
            }
        });
        banner.setRightButtonListener(new BannerInterface.OnClickListener() {
            @Override
            public void onClick(BannerInterface banner) {
                Intent intent = new Intent(dashboardActivity.this,enrollActivity.class);
                startActivity(intent);
            }
        });
        banner.setOnShowListener(new BannerInterface.OnShowListener() {
            @Override
            public void onShow() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        banner.dismiss();
                    }
                },2300);
            }
        });

        view_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk){
                    Intent intent = new Intent(dashboardActivity.this,ApplicationStatus.class);
                    startActivity(intent);
                }
                else{
                    //TODO: ADD MATERIAL BANNER
                    banner.setMessage("You have not enrolled for this exam. To enroll for the exam press redirect.");
                    banner.setRightButton("Redirect", new BannerInterface.OnClickListener() {
                        @Override
                        public void onClick(BannerInterface banner) {
                            Intent intent = new Intent(dashboardActivity.this,enrollActivity.class);
                            startActivity(intent);
                        }
                    });
                    banner.show();
                }
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chk){
                    Intent intent = new Intent(dashboardActivity.this,editProfile.class);
                    startActivity(intent);
                }
                else{
                    banner.setMessage("You have not enrolled for this exam. To enroll for the exam press redirect.");
                    banner.setRightButton("Redirect", new BannerInterface.OnClickListener() {
                        @Override
                        public void onClick(BannerInterface banner) {
                            Intent intent = new Intent(dashboardActivity.this,enrollActivity.class);
                            startActivity(intent);
                        }
                    });
                    banner.show();
                }
            }
        });
    }

    private boolean checkEnrolled() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference childRef = null;
        if (currentUser != null) {
            childRef = myRef.child(currentUser.getUid()).child("stat");
        }
        Handler handler = new Handler();
        DatabaseReference finalChildRef = childRef;
        handler.post(new Runnable() {
            @Override
            public void run() {
                finalChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String val = snapshot.child("application status").getValue(String.class);
                            if(val.equals("true")){
                                chk = true;
                            }
                            else{
                                chk = false;
                            }
                            Log.d("enrollActivity", "onDataChange: "+val+chk);
                        }
                        else {
                            chk = false;
                            Log.d("enrollActivity", "onDataChange: " + chk);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG", "onCancelled: ERROR RETRIEVING APPLICATION STATUS ");
                    }
                });
            }
        });
        return chk;
    }

    private void getDisplayName(FirebaseAuth mAuth) {
        if(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName()!=null) {
            //GOOGLE SIGN IN
            String[] name_array = Objects.requireNonNull(Objects.requireNonNull(mAuth.getCurrentUser())
                    .getDisplayName()).split(" ");
            name = name_array[0];
        }
        else
        {
            name = "USER";
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}