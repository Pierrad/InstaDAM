package com.example.instadam.notification;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.instadam.MainActivity;
import com.example.instadam.R;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}