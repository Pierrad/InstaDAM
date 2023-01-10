package com.example.instadam.feed;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.geolocation.Geolocation;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private ArrayList<FeedPost> posts;
    private ListView postsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        registerElements();
        fetchPosts();
    }

    public void registerElements() {
        postsList = findViewById(R.id.feed_posts);
    }

    public void fetchPosts() {
        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), User.getInstance(FeedActivity.this).getAccessToken());

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        body.put("latitude", "43.34");
        body.put("longitude", "7.1");

        request.makeRequest(Request.Method.POST, "/v1/images/geolocation", headers, body, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonResponse = new JSONArray(response);
                            // iterate over the response and create a new post for each
                            posts = new ArrayList<>();
                            for (int i = 0; i < jsonResponse.length(); i++) {
                                JSONObject post = jsonResponse.getJSONObject(i);
                                JSONObject author = post.getJSONObject("user");
                                JSONObject geolocation = post.getJSONObject("geolocation");
                                JSONArray coordinates = geolocation.getJSONArray("coordinates");
                                Geolocation location = new Geolocation(
                                        coordinates.getDouble(0),
                                        coordinates.getDouble(1)
                                );
                                posts.add(new FeedPost(
                                        post.getString("name"),
                                        post.getString("description"),
                                        author.getString("name"),
                                        location,
                                        post.getString("image")
                                ));
                            }

                            renderPosts();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
    }

    public void renderPosts() {
        FeedPostsAdapter adapter = new FeedPostsAdapter(this, posts);
        postsList.setAdapter(adapter);
    }
}