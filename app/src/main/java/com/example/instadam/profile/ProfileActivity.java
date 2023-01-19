package com.example.instadam.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.components.Post;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.settings.SettingsActivity;
import com.example.instadam.user.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ProfileActivity: ", "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView pseudo = findViewById(R.id.pseudo);
        Button editProfile = findViewById(R.id.editProfile);

        pseudo.setText(User.getInstance(ProfileActivity.this).getUsername());

        getAllImages();

        editProfile.setOnClickListener(click -> redirectToSettingsActivity());
    }

    public void getAllImages() {
        Log.d("ProfileActivity: ", "getAllImages()");

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        TextView nbrPublications = findViewById(R.id.nbrPublications);
        GridView grid = findViewById(R.id.gridView);
        ArrayList<Post> posts = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), User.getInstance(ProfileActivity.this).getAccessToken());

        request.makeRequest(Request.Method.GET, "/v1/images/user/" + User.getInstance(ProfileActivity.this).getId(), headers, body, response -> {
                Log.d("ProfileActivity: ", "getAllImages() -> rqstGet getImagesByUserID OK");

                try {
                    JSONArray images = new JSONArray(response);

                    nbrPublications.setText(images.length() <= 1 ? images.length() + " publication" : images.length() + " publications");

                    for (int i = 0; i < images.length(); i++) {
                        JSONObject post = images.getJSONObject(i);
                        Log.d("Post", post.toString());

                        posts.add(new Post(
                                post.getString("id"),
                                post.getString("name"),
                                post.getString("image")
                        ));
                    }

                    grid.setAdapter(new ProfilePostsAdapter(this, posts));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, Throwable::printStackTrace
        );
    }

    public void redirectToSettingsActivity() {
        Log.d("ProfileActivity: ", "redirectToSettingsActivity()");

        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        ProfileActivity.this.startActivity(intent);
    }

}