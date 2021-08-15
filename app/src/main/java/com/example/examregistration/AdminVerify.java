package com.example.examregistration;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class AdminVerify extends AppCompatActivity {
    private TextView candidateName,docType,fathername,mothername,dob,gender,address,phone,email;
    private ImageView candidateImage;
    private PDFView candidatedoc;
    private String pdfUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_verify);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_700)));
        candidateName = findViewById(R.id.canName);
        candidateImage = findViewById(R.id.canImage);
        candidatedoc = findViewById(R.id.can_pdf);
        docType = findViewById(R.id.tview2);
        LinearLayout accept = findViewById(R.id.acceptBtn);
        LinearLayout decline = findViewById(R.id.declineBtn);
        fathername = findViewById(R.id.fname);
        mothername = findViewById(R.id.mname);
        dob = findViewById(R.id.dateofbirth);
        gender = findViewById(R.id.gender);
        address = findViewById(R.id.residentialaddress);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);

        String uid = getIntent().getStringExtra("uid");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> update = new HashMap<>();
                update.put("wegood","accepted");
                myRef.child("stat").updateChildren(update);

                Intent goBack = new Intent(AdminVerify.this,AdminActivity.class);
                startActivity(goBack);
            }
        });

        decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> update = new HashMap<>();
                update.put("wegood","declined");
                myRef.child("stat").updateChildren(update);

                Intent goBack = new Intent(AdminVerify.this,AdminActivity.class);
                startActivity(goBack);
            }
        });

        Handler handler = new Handler();
        handler.post(() -> myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String name = snapshot.child("form").child("candidateName").getValue(String.class);
                    String documentType = snapshot.child("documents").child("candidate ID proof").child("docType").getValue(String.class);
                    String imageUrl = snapshot.child("documents").child("candidate pic").child("imageUrl").getValue(String.class);
                    String mail = snapshot.child("form").child("emailAddress").getValue(String.class);
                    String fatherName = snapshot.child("form").child("fatherName").getValue(String.class);
                    String motherName = snapshot.child("form").child("motherName").getValue(String.class);
                    String dobirth = snapshot.child("form").child("dateOfBirth").getValue(String.class);
                    String cgender = snapshot.child("form").child("gender").getValue(String.class);
                    String caddress = snapshot.child("form").child("address").getValue(String.class);
                    String contact = snapshot.child("form").child("contactNumber").getValue(String.class);
                    pdfUrl = snapshot.child("documents").child("candidate ID proof").child("imageUrl").getValue(String.class);

                    new RetrivePDFfromUrl().execute(pdfUrl);

                    String msg = null;
                    if (documentType != null) {
                        msg = "Document Type : "+documentType.toUpperCase();
                    }
                    candidateName.setText(name);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error);
                    Glide.with(AdminVerify.this).load(imageUrl).apply(options).into(candidateImage);

                    String temp1 = fathername.getText().toString()+"\n"+fatherName+"\n";
                    fathername.setText(temp1);
                    String temp2 = mothername.getText().toString()+"\n"+motherName+"\n";
                    mothername.setText(temp2);
                    String temp3 = dob.getText().toString()+"\n"+dobirth+"\n";
                    dob.setText(temp3);
                    String temp4 = gender.getText().toString()+"\n"+cgender+"\n";
                    gender.setText(temp4);
                    String temp5 = address.getText().toString()+"\n"+caddress+"\n";
                    address.setText(temp5);
                    String temp6 = phone.getText().toString()+"\n"+contact+"\n";
                    phone.setText(temp6);
                    String temp7 = email.getText().toString()+"\n"+mail+"\n";
                    email.setText(temp7);

                    docType.setText(msg);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {  }
        }));

    }

    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
        candidatedoc.fromStream(inputStream).swipeHorizontal(true).load();
        }
    }
}

