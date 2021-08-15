package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sergivonavi.materialbanner.Banner;
import com.sergivonavi.materialbanner.BannerInterface;

public class enrollActivity extends AppCompatActivity {
    CardView cardView1,cardView2;
    ImageView arrowUp,arrowDown;
    Button apply;

    private boolean chk = false;
    private FirebaseAuth mAuth;

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_700)));

        mAuth = FirebaseAuth.getInstance();

        cardView1 = findViewById(R.id.cardView_1);
        cardView2 = findViewById(R.id.cardView_2);
        arrowUp = findViewById(R.id.arrow_up);
        arrowDown = findViewById(R.id.arrow_down);
        apply = findViewById(R.id.apply_now);

        cardView2.setVisibility(View.INVISIBLE);

        if(checkEnrolled()){
            updateView();
            Log.d("enrollActivity", "checkEnrolled: "+chk);
        }

        arrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardView2.getVisibility()==View.INVISIBLE) {
                    arrowDown.setVisibility(View.GONE);
                    arrowUp.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(cardView2,new AutoTransition());
                    cardView2.setVisibility(View.VISIBLE);
                }

            }
        });

        arrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardView2.getVisibility()==View.VISIBLE){
                    arrowDown.setVisibility(View.VISIBLE);
                    arrowUp.setVisibility(View.INVISIBLE);
                    TransitionManager.beginDelayedTransition(cardView2,new AutoTransition());
                    cardView2.setVisibility(View.INVISIBLE);
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
                Intent intent = new Intent(enrollActivity.this,editProfile.class);
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

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEnrolled()){
                    banner.show();
                }
                else {
                    Intent i1 = new Intent(enrollActivity.this,formActivity.class);
                    startActivity(i1);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkEnrolled()){
            updateView();
            Log.d("enrollActivity", "checkEnrolled: "+chk);
        }
    }

    public void updateView(){
        apply.setText(R.string.enrollment_done);
    }

    public boolean checkEnrolled(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference childRef = myRef.child(currentUser.getUid()).child("stat");
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String val = snapshot.child("application status").getValue(String.class);
                            if(val.equals("true")){
                                chk = true;
                                updateView();
                                Log.d("enrollActivity", "onDataChange: "+val+chk);
                            }
                            else{
                                chk = false;
                                Log.d("enrollActivity", "onDataChange: "+val+chk);
                            }
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

}