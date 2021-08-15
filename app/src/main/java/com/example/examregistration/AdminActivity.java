package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.examregistration.R.*;

public class AdminActivity extends AppCompatActivity {
    private TextView textView;
    private ListView listview;
    private List<UserHelperClass> canList;
    String total;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        canList = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                canList.clear();
                int count=0;
                total = String.valueOf(snapshot.getChildrenCount());
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    String status = snapshot1.child("stat").child("application status").getValue(String.class);
                    if(Objects.equals(status, "true")){
                        count+=1;
                        UserHelperClass candeets = new UserHelperClass();
                        String imageurl= snapshot1.child("documents").child("candidate pic").child("imageUrl").getValue(String.class);
                        String candidateName = snapshot1.child("form").child("candidateName").getValue(String.class);
                        String candidateMail = snapshot1.child("login deets").child("mail").getValue(String.class);

                        candeets.setImageUrl(imageurl);
                        candeets.setUid(snapshot1.getKey());
                        candeets.setfileName(candidateName);
                        candeets.setMail(candidateMail);
                        canList.add(candeets);
                    }
                }

                String msg = "Applications pending : ("+count+"/"+total+")";
                textView.setText(msg);
                CandidateList adapter = new CandidateList(AdminActivity.this,canList);
                listview.setAdapter(adapter);

                listview.setOnItemClickListener((parent, view, position, id) -> {
                    listview.getItemAtPosition(position);
                    TextView tv = view.findViewById(R.id.uid);
                    Intent intent = new Intent(AdminActivity.this,AdminVerify.class);
                    intent.putExtra("uid",tv.getText().toString());
                    startActivity(intent);
                    Toast.makeText(AdminActivity.this, tv.getText().toString() , Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("TAG", "onCancelled: ERROR RETRIEVING DATA");
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_admin);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(color.purple_700)));

        textView = findViewById(id.textview1);
        listview = findViewById(id.candidatesList);
        canList = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == id.action_logout){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(AdminActivity.this,AdminLogin.class);
            startActivity(intent);
            return true;
        }
        else if(item.getItemId() == id.action_nothing){
            Toast.makeText(this, "\\_(o o /)_/", Toast.LENGTH_SHORT).show();
            return true;
        }
        else return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        finishAffinity();
    }
}