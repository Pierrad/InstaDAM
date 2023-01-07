package com.example.instadam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instadam.auth.LoginActivity;
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.user.User;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disable dark mode

        // Show Splash Screen

        if (hasAccount()) {
            User.getInstance(this);
            Intent intent = new Intent(this, FeedActivity.class);
            this.startActivity(intent);
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }
    }

    public boolean hasAccount() {
        SharedPreferences sh = getSharedPreferences(String.valueOf(R.string.SP_USER), MODE_PRIVATE);
        return sh.contains("token");
    }
}