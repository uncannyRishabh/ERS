package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    TextView textView1,total;
    Button confirm_pay;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mAuth = FirebaseAuth.getInstance();
        textView1 = findViewById(R.id.tv2);
        total = findViewById(R.id.tv6);
        confirm_pay = findViewById(R.id.confirm_pay);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference childRef = myRef.child(currentUser.getUid()).child("form");
        childRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String ccategory = snapshot.child("category").getValue(String.class);
                    Log.d("TAG", "onDataChange: "+ccategory);
                    String amt = "";
                    switch (ccategory){
                        case "General":
                            amt = "750";
                            textView1.setText(R.string.gen_fee);
                            break;
                        case "OBC":
                            amt = "150";
                            textView1.setText(R.string.obc_fee);
                            break;
                        case "SC":
                            amt = "80";
                            textView1.setText(R.string.sc_fee);
                            break;
                        case "ST":
                            amt = "80";
                            textView1.setText(R.string.st_fee);
                            break;
                    }
                    int t = Integer.parseInt(amt) + 20;
                    String totalDisplay = String.valueOf(t)+"₹";
                    total.setText(totalDisplay);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        confirm_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference childRef = myRef.child(currentUser.getUid()).child("stat");
                Map<String,Object> entry = new HashMap<>();
                entry.put("application status","true");
                entry.put("wegood","false");
                childRef.updateChildren(entry);
                Intent i1 = new Intent(PaymentActivity.this,dashboardActivity.class);
                startActivity(i1);
            }
        });
    }
}