package com.example.examregistration;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

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

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Fragment1 extends Fragment {
    TextInputLayout datePickerLayout,candidateName,fathername,motherName,nationality,address,city
            ,pincode,contactNumber,email,pickGender,category,pickCountry,pickState,pickDistrict;

    private FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab1,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        //hooks
        candidateName = view.findViewById(R.id.candidate_name);
        fathername    = view.findViewById(R.id.father_name);
        motherName    = view.findViewById(R.id.mother_name);
        nationality   = view.findViewById(R.id.nationality);
        address       = view.findViewById(R.id.address);
        city          = view.findViewById(R.id.city_town_village);
        pincode       = view.findViewById(R.id.pincode);
        contactNumber = view.findViewById(R.id.contact_number);
        email         = view.findViewById(R.id.email_address);
        pickGender    = view.findViewById(R.id.gender_picker);
        category      = view.findViewById(R.id.Category);
        pickCountry   = view.findViewById(R.id.country);
        pickState     = view.findViewById(R.id.state);
        pickDistrict  = view.findViewById(R.id.district);
        datePickerLayout = view.findViewById(R.id.Date_Picker);

        Handler handler1 = new Handler();
        handler1.post(new Runnable() {
            @Override
            public void run() {

                datePickerLayout.setEndIconOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.Q)
                    @Override
                    public void onClick(View v) {
                        Calendar cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        int month = cal.get(Calendar.MONTH);

                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                getContext(), new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                        month += 1;
                                        String text = dayOfMonth + "-" + month + "-" + year;
                                        Objects.requireNonNull(datePickerLayout.getEditText()).setText(text);
                                    }
                                }
                                , year, month, day);
                        datePickerDialog.show();
                    }
                });

                final String[] gender = new String[] {"Male","Female","Other"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),R.layout.gender_dropdown_populate,gender);
                AutoCompleteTextView editTextFilledExposedDropdown = view.findViewById(R.id.filled_exposed_dropdown);
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
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, COUNTRIES);
                AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.country_dropdown);
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
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, states);
                AutoCompleteTextView textView1 = (AutoCompleteTextView) view.findViewById(R.id.states);
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
                ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, districts);
                AutoCompleteTextView textView2 = (AutoCompleteTextView) view.findViewById(R.id.districts_dropdown);
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
                ArrayAdapter<String> adapter4 = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categories);
                AutoCompleteTextView textView3 = (AutoCompleteTextView) view.findViewById(R.id.category_dropdown);
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
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName()!=null){ //Google Sign In
                    String name = mAuth.getCurrentUser().getDisplayName();
                    String number = mAuth.getCurrentUser().getPhoneNumber();
                    String mail = mAuth.getCurrentUser().getEmail();

                    candidateName.getEditText().setText(name);
                    contactNumber.getEditText().setText(number);
                    email.getEditText().setText(mail);
                }

                //set data if already saved
                setData();

                Button submit = getActivity().findViewById(R.id.submit_form_btn);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (allFieldsAreFilled()) {
                            uploadData();

                            TabLayout tabhost = getActivity().findViewById(R.id.tabs);
                            tabhost.getTabAt(1).select();
                        }
                    }
                });
            }
        });
    }

    private void setData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        FirebaseUser currentUser = mAuth.getCurrentUser();
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

                        }
                        else {
                            Log.d("CHECK", "CHECK_IF_USER_HAS_FILLED_FORM: false");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
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
            Snackbar.make(getView(),"All Fields must be filled before submitting", Snackbar.LENGTH_SHORT).show();
            return false;
        }

    }

    private void uploadData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Handler handler2 = new Handler();
        handler2.post(new Runnable() {
            @Override
            public void run() {
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
            }
        });
    }
}
