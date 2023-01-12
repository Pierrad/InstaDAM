package com.example.instadam.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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

public class FeedActivity extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    private ArrayList<FeedPost> posts;
    private ListView postsList;
    private Boolean hasLocation = false;

    // TODO
    // - Add pagination (when scrolling down, load more posts)
    // - Use device geolocation in fetchPost call

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        registerElements();
    }

    public void registerElements() {
        postsList = findViewById(R.id.feed_posts);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (!hasLocation) {
            hasLocation = true;
            fetchPosts(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    public void fetchPosts(double latitude, double longitude) {
        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), User.getInstance(FeedActivity.this).getAccessToken());

        Log.d("FEEDACTIVITY", "Latitude: " + latitude + " Longitude: " + longitude);

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        body.put("latitude", String.valueOf(latitude));
        body.put("longitude",  String.valueOf(longitude));
        body.put("page", "1");

        request.makeRequest(Request.Method.POST, "/v1/images/geolocation", headers, body, response -> {
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
        }, Throwable::printStackTrace
        );
    }

    public void renderPosts() {
        FeedPostsAdapter adapter = new FeedPostsAdapter(this, posts);
        postsList.setAdapter(adapter);
    }

}