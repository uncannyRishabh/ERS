package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Objects;

public class ApplicationStatus extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private boolean chk = false;
    private static final int CREATE_FILE = 1;
    private FileOutputStream fileOutupStream = null;
    private int arrow_state = 0;

    Chip chip2;
    ImageView arrowUp,arrowDown;
    TextView downloadText;
    Button downloadBtn;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        chk = weGood();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_status);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_700)));

        ActivityCompat.requestPermissions(ApplicationStatus.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        mAuth = FirebaseAuth.getInstance();
        chk = weGood();
        arrowUp = findViewById(R.id.arrow_up);
        arrowDown = findViewById(R.id.arrow_down);
        downloadText = findViewById(R.id.download);
        downloadBtn = findViewById(R.id.downloadBtn);
        chip2 = findViewById(R.id.chip2);

        Log.d("TAG", "onCreate: "+arrow_state);
        arrowDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow_state=1;
                if(arrowDown.getVisibility()==View.GONE) {
                    arrowDown.setVisibility(View.VISIBLE);
                }
                ((MotionLayout)findViewById(R.id.motionL)).transitionToEnd();
                arrowDown.setVisibility(View.GONE);
                arrowUp.setVisibility(View.VISIBLE);
            }
        });

        arrowUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow_state=0;
                arrowDown.setImageResource(R.drawable.arrow_down);
                if(arrowUp.getVisibility()==View.GONE){
                    arrowUp.setVisibility(View.VISIBLE);
                }
                ((MotionLayout)findViewById(R.id.motionL)).transitionToStart();
                arrowUp.setVisibility(View.GONE);
                arrowDown.setVisibility(View.VISIBLE);
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ApplicationStatus.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // Do the file write
//                    generatePDF();
                } else {
                    // Request permission from the user
                    ActivityCompat.requestPermissions(ApplicationStatus.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }

                String myFilePath = Environment.getExternalStorageDirectory().getPath() + "/myPDFFile.pdf";
                File myFile = new File(myFilePath);
                createFile(Uri.fromFile(myFile));
            }
        });
    }

    private boolean weGood() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        DatabaseReference childRef = myRef.child(currentUser.getUid()).child("stat").child("wegood");
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String val = snapshot.getValue(String.class);
                            if(Objects.equals(val,"accepted")){
                                chk = true;
                                chip2.setVisibility(View.VISIBLE);
                                arrowDown.setVisibility(View.VISIBLE);
                                downloadText.setVisibility(View.VISIBLE);
                                downloadBtn.setVisibility(View.VISIBLE);
                                
                            }
                            else if(Objects.equals(val,"declined")){
                                chip2.setBackgroundColor(Color.RED);
                                String msg = "Declined";
                                chip2.setText(msg);
                                arrowDown.setVisibility(View.VISIBLE);

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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "pdfFile.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        startActivityForResult(intent, CREATE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CREATE_FILE) {

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100, 250, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Paint myPaint = new Paint();
            String myString = "HALL TICKET";
            int x = 10, y=25;

            for (String line: myString.split("\n")){
                page.getCanvas().drawText(line, x, y, myPaint);
                y+=myPaint.descent()-myPaint.ascent();
            }
            document.finishPage(page);

            try {
                fileOutupStream = (FileOutputStream) getContentResolver().openOutputStream(data.getData());
                try {
                    document.writeTo(fileOutupStream);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                document.close();
                fileOutupStream.flush();
                fileOutupStream.close();

                Toast.makeText(this, "saved ", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "something went wrong" + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(arrow_state==1){
            Log.d("TAG", "onRestart: " + arrow_state);
            arrowDown.setImageResource(R.drawable.arrow_up);
            arrowDown.setVisibility(View.GONE);
        }
        if(arrow_state==0){
            Log.d("TAG", "onRestart: " + arrow_state);
        }
    }

}