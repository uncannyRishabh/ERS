package com.example.examregistration;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.examregistration.ui.main.SectionsPagerAdapter;
import com.google.android.material.textfield.TextInputLayout;

import java.io.InputStream;

public class formActivity extends AppCompatActivity {
    ImageView backnav,info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_form);
                SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getApplicationContext(), getSupportFragmentManager());
                ViewPager viewPager = findViewById(R.id.view_pager);
                viewPager.setAdapter(sectionsPagerAdapter);
                TabLayout tabs = findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);

                backnav = findViewById(R.id.imageView3);
                backnav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(formActivity.this,enrollActivity.class);
                        startActivity(intent);
                    }
                });

                info = findViewById(R.id.imageView4);
                info.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: Display PDF for guidelines from assets in a dialog/fragment

                    }
                });
            }
        });
    }
}