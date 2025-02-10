package com.example.laundrobook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.*;


public class Dashboard extends AppCompatActivity{
    Button about;
    Button delete_account;
    Button notification_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        about = findViewById(R.id.about);
        delete_account = findViewById(R.id.delete_account);
        notification_settings = findViewById(R.id.notification_settings);

        // root node


        delete_account.setOnClickListener(view -> {});

        about.setOnClickListener(view -> {

        });
    }








}
