package com.example.instadam.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.instadam.MainActivity;
import com.example.instadam.R;
import com.example.instadam.auth.LoginActivity;
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.feed.FeedPostsAdapter;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView pseudo = findViewById(R.id.pseudo);
        Button editProfile = findViewById(R.id.editProfile);

        pseudo.setText(User.getInstance(ProfileActivity.this).getUsername());

        getAllImages();

        editProfile.setOnClickListener(click -> redirectToSettingsActivity());
    }

    public void getAllImages() {
        TextView nbrPublications = findViewById(R.id.nbrPublications);

        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), User.getInstance(ProfileActivity.this).getAccessToken());

        request.makeRequest(Request.Method.GET, "/v1/images/user/" + User.getInstance(ProfileActivity.this).getId(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // TODO
                        // response to array
                        // nbrPublications.setText(response.size());
                        Log.e("getAllImages response", response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO
                        error.printStackTrace();
                    }
                }
        );
    }

    public void redirectToSettingsActivity() {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        ProfileActivity.this.startActivity(intent);
    }
}