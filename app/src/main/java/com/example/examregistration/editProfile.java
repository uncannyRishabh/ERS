package com.example.examregistration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.internal.IResolveAccountCallbacks;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class editProfile extends AppCompatActivity {
    TextInputLayout datePickerLayout,candidateName,fathername,motherName,nationality,address,city
            ,pincode,contactNumber,email,pickGender,category,pickCountry,pickState,pickDistrict;
    ImageView imageView;
    Button choose;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private Uri mImageUri;
    private FirebaseAuth mAuth;
    private static final int SELECT_IMAGE = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Profile");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.purple_700)));

        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        imageView = findViewById(R.id.imageV);
        choose = findViewById(R.id.upload_image);
        candidateName = findViewById(R.id.candidate_name);
        fathername    = findViewById(R.id.father_name);
        motherName    = findViewById(R.id.mother_name);
        nationality   = findViewById(R.id.nationality);
        address       = findViewById(R.id.address);
        city          = findViewById(R.id.city_town_village);
        pincode       = findViewById(R.id.pincode);
        contactNumber = findViewById(R.id.contact_number);
        email         = findViewById(R.id.email_address);
        pickGender    = findViewById(R.id.gender_picker);
        category      = findViewById(R.id.Category);
        pickCountry   = findViewById(R.id.country);
        pickState     = findViewById(R.id.state);
        pickDistrict  = findViewById(R.id.district);
        datePickerLayout = findViewById(R.id.Date_Picker);
        datePickerLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                int month = cal.get(Calendar.MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        editProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month += 1;
                        String text = dayOfMonth + "-" + month + "-" + year;
                        Objects.requireNonNull(datePickerLayout.getEditText()).setText(text);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        final String[] gender = new String[] {"Male","Female","Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),R.layout.gender_dropdown_populate,gender);
        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

        editTextFilledExposedDropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickGender.getEditText().getText().toString().equals("")){
                    editTextFilledExposedDropdown.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editTextFilledExposedDropdown.showDropDown();
                    }
                },50);
            }
        });
        pickGender.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickGender.getEditText().getText().toString().equals("")){
                    editTextFilledExposedDropdown.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editTextFilledExposedDropdown.showDropDown();
                    }
                },50);
            }
        });


        final String[] COUNTRIES = new String[] {"Argentina","Belgium","Canada","Denmark","Egypt", "France","Germany"
                ,"Hungary","India","Japan","Kazakhstan","Latvia","Maldives","Nepal","Oman","Paxxtan","Qatar","Romania"
                ,"Spain","Taiwan","Uganda","Viet Nam","Wallis and Futuna Islands","Yemen","Zimbawe"};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = findViewById(R.id.country_dropdown);
        textView.setAdapter(adapter1);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickCountry.getEditText().getText().toString().equals("")){
                    textView.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.showDropDown();
                    }
                },50);
            }
        });
        pickCountry.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickCountry.getEditText().getText().toString().equals("")){
                    textView.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView.showDropDown();
                    }
                },50);
            }
        });

        final String[] states = new String[] {"Andhra Pradesh","Arunachal Pradesh","Assam","Bihar","Karnataka"
                ,"Kerala","Chhattisgarh","Uttar Pradesh","Goa","Gujarat","Haryana","Himachal Pradesh","Jammu and Kashmir"
                ,"Jharkhand","West Bengal","Madhya Pradesh","Maharashtra","Manipur","Meghalaya","Mizoram","Nagaland"
                ,"Orissa","Punjab","Rajasthan","Sikkim","Tamil Nadu","Telangana","Tripura","Uttarakhand"};
        Arrays.sort(states);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, states);
        AutoCompleteTextView textView1 = findViewById(R.id.states);
        textView1.setAdapter(adapter2);

        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickState.getEditText().getText().toString().equals("")){
                    textView1.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView1.showDropDown();
                    }
                },50);
            }
        });
        pickState.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickState.getEditText().getText().toString().equals("")){
                    textView1.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView1.showDropDown();
                    }
                },50);
            }
        });

        final String[] districts = new String[] {"Alipurduar","Bankura","Birbhum","Cooch Behar","Dakshin Dinajpur (South Dinajpur)"
                ,"Darjeeling","Hooghly","Howrah","Jalpaiguri","Jhargram","Kalimpong","Kolkata","Malda","Murshidabad","Nadia"
                ,"North 24 Parganas","Paschim Medinipur (West Medinipur)","Paschim (West) Burdwan","Purba Burdwan (Bardhaman)"
                ,"Purba Medinipur (East Medinipur)","Purulia","South 24 Parganas","Uttar Dinajpur (North Dinajpur)"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, districts);
        AutoCompleteTextView textView2 = findViewById(R.id.districts_dropdown);
        textView2.setAdapter(adapter3);

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickDistrict.getEditText().getText().toString().equals("")){
                    textView2.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView2.showDropDown();
                    }
                },50);
            }
        });
        pickDistrict.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pickDistrict.getEditText().getText().toString().equals("")){
                    textView2.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView2.showDropDown();
                    }
                },50);
            }
        });

        final String[] categories = new String[] {"General","OBC","SC","ST"};
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, categories);
        AutoCompleteTextView textView3 = findViewById(R.id.category_dropdown);
        textView3.setAdapter(adapter4);

        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!category.getEditText().getText().toString().equals("")){
                    textView3.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView3.showDropDown();
                    }
                },50);
            }
        });
        category.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!category.getEditText().getText().toString().equals("")){
                    textView3.setText("");
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textView3.showDropDown();
                    }
                },50);
            }
        });

        //getting data to resp. fields
        setData();

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,SELECT_IMAGE);
            }
        });

        ConstraintLayout coordinatorLayout = findViewById(R.id.constraintLayoutEP);
        Button submit = findViewById(R.id.submit_form_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allFieldsAreFilled()) {
                    uploadData();
                    Snackbar.make(coordinatorLayout,"Profile Updated Successfully",Snackbar.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent i1 = new Intent(editProfile.this,dashboardActivity.class);
                            startActivity(i1);
                        }
                    },500);
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_IMAGE && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            mImageUri = data.getData();
            Log.d("TAG", "onActivityResult: "+mImageUri.getPath());
            imageView.setPadding(0,0,0,0);
            Glide.with(this).load(mImageUri).into(imageView);
            uploadImage();
        }
    }

    private void uploadImage() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child("candidate pics/" + System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri);

            Task<Uri> urlTask = mUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri == null) {
                            Toast.makeText(getApplicationContext(), "TASK FAILED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            Map<String,Object> upload = new HashMap<>();
                            upload.put("imageUrl",downloadUri.toString());
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            mDatabaseRef.child(currentUser.getUid()).child("documents").child("candidate pic").updateChildren(upload);
                        }
                    }
                }
            });
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getApplicationContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void setData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        FirebaseUser currentUser = mAuth.getCurrentUser();

        DatabaseReference ImageRef = myRef.child(currentUser.getUid()).child("documents");
        ImageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String url = snapshot.child("candidate pic").child("imageUrl").getValue(String.class);
                    RequestOptions options = new RequestOptions()
                            .centerCrop()
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error);
                    Glide.with(editProfile.this).load(url).apply(options).into(imageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                goToPreviousActivity();
            }
        });

        DatabaseReference childRef = myRef.child(currentUser.getUid()).child("form");
        Handler datahandler = new Handler();
        datahandler.post(new Runnable() {
            @Override
            public void run() {
                childRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Log.d("CHECK", "CHECK_IF_USER_HAS_FILLED_FORM: true");
                            String candidatename = snapshot.child("candidateName").getValue(String.class);
                            String mail = snapshot.child("emailAddress").getValue(String.class);
                            String fatherName = snapshot.child("fatherName").getValue(String.class);
                            String mothername = snapshot.child("motherName").getValue(String.class);
                            String dob = snapshot.child("dateOfBirth").getValue(String.class);
                            String cgender = snapshot.child("gender").getValue(String.class);
                            String cnationality = snapshot.child("nationality").getValue(String.class);
                            String ccategory = snapshot.child("category").getValue(String.class);
                            String caddress = snapshot.child("address").getValue(String.class);
                            String ccity = snapshot.child("cityName").getValue(String.class);
                            String ccountry = snapshot.child("country").getValue(String.class);
                            String cstate = snapshot.child("state").getValue(String.class);
                            String cdistrict = snapshot.child("district").getValue(String.class);
                            String cpincode = snapshot.child("pincode").getValue(String.class);
                            String contact = snapshot.child("contactNumber").getValue(String.class);

                            candidateName.getEditText().setText(candidatename);
                            fathername.getEditText().setText(fatherName);
                            motherName.getEditText().setText(mothername);
                            datePickerLayout.getEditText().setText(dob);
                            pickGender.getEditText().setText(cgender);
                            nationality.getEditText().setText(cnationality);
                            category.getEditText().setText(ccategory);
                            address.getEditText().setText(caddress);
                            city.getEditText().setText(ccity);
                            pickCountry.getEditText().setText(ccountry);
                            pickState.getEditText().setText(cstate);
                            pickDistrict.getEditText().setText(cdistrict);
                            pincode.getEditText().setText(cpincode);
                            contactNumber.getEditText().setText(contact);
                            email.getEditText().setText(mail);

                        }
                        else {
                            Log.d("CHECK", "CHECK_IF_USER_HAS_FILLED_FORM: false");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(editProfile.this, "Check your network and try again", Toast.LENGTH_SHORT).show();
                        goToPreviousActivity();
                    }
                });
            }
        });

    }

    private void goToPreviousActivity() {
        Intent intent = new Intent(editProfile.this,dashboardActivity.class);
        startActivity(intent);
    }

    private boolean allFieldsAreFilled() {
        if(!candidateName.getEditText().getText().toString().equals("") &&
                !fathername.getEditText().getText().toString().equals("") && !motherName.getEditText().getText().toString().equals("")
                && !datePickerLayout.getEditText().getText().toString().equals("") && !pickGender.getEditText().getText().toString().equals("")
                && !nationality.getEditText().getText().toString().equals("") && !category.getEditText().getText().toString().equals("")
                && !address.getEditText().getText().toString().equals("") && !city.getEditText().getText().toString().equals("")
                && !pickCountry.getEditText().getText().toString().equals("") && !pickState.getEditText().getText().toString().equals("")
                && !pickDistrict.getEditText().getText().toString().equals("") && !pincode.getEditText().getText().toString().equals("")
                && !contactNumber.getEditText().getText().toString().equals("") && !email.getEditText().getText().toString().equals(""))
        {
            return true;
        }
        else {
            Snackbar.make(getCurrentFocus(),"All Fields must be filled before submitting", Snackbar.LENGTH_SHORT).show();
            return false;
        }

    }

    private void uploadData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Handler handler2 = new Handler();
        handler2.post(() -> {
            Map<String,Object> entry = new HashMap<>();
            entry.put("candidateName",candidateName.getEditText().getText().toString());
            entry.put("fatherName",fathername.getEditText().getText().toString());
            entry.put("motherName",motherName.getEditText().getText().toString());
            entry.put("dateOfBirth",datePickerLayout.getEditText().getText().toString());
            entry.put("gender",pickGender.getEditText().getText().toString());
            entry.put("nationality",nationality.getEditText().getText().toString());
            entry.put("category",category.getEditText().getText().toString());
            entry.put("address",address.getEditText().getText().toString());
            entry.put("cityName",city.getEditText().getText().toString());
            entry.put("country",pickCountry.getEditText().getText().toString());
            entry.put("state",pickState.getEditText().getText().toString());
            entry.put("district",pickDistrict.getEditText().getText().toString());
            entry.put("pincode",pincode.getEditText().getText().toString());
            entry.put("contactNumber",contactNumber.getEditText().getText().toString());
            entry.put("emailAddress",email.getEditText().getText().toString());

            myRef.child(currentUser.getUid()).child("form").updateChildren(entry);
        });
    }
}