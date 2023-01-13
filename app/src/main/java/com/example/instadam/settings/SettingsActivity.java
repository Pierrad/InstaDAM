package com.example.instadam.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.instadam.R;
import com.example.instadam.user.User;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText pseudo = findViewById(R.id.pseudo);
        TextView email = findViewById(R.id.pseudo);
        Button save = findViewById(R.id.save);

        pseudo.setText(User.getInstance(SettingsActivity.this).getUsername());
        email.setText(User.getInstance(SettingsActivity.this).getEmail());

        //save.setOnClickListener(click -> );
    }

    public void save(String pseudo) {

    }
}