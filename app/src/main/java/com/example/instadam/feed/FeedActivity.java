package com.example.instadam.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private LinearLayoutManager layoutManager;

    private RecyclerView postsRecyclerList;
    private ArrayList<FeedPost> posts = new ArrayList<>();
    private FeedPostsAdapter adapter;
    private AlertDialog dialog;

    private boolean hasLocation = false;
    private boolean isLoading = false;
    private int page = 1;
    private int totalPageNumber = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        adapter = new FeedPostsAdapter(posts, this);
        layoutManager = new LinearLayoutManager(this);

        postsRecyclerList = findViewById(R.id.feed_posts);
        postsRecyclerList.setLayoutManager(layoutManager);
        postsRecyclerList.setAdapter(adapter);

        checkLocation();
        checkLocationProvider();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLocation();
        checkLocationProvider();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        System.out.println("Location changed");
        if (!hasLocation) {
            hasLocation = true;
            fetchPosts(location.getLatitude(), location.getLongitude(), page);

            postsRecyclerList.addOnScrollListener(new PaginationListener(layoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    page++;
                    fetchPosts(location.getLatitude(), location.getLongitude(), page);
                }

                @Override
                public boolean isLastPage() {
                    return page == totalPageNumber;
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    public void fetchPosts(double latitude, double longitude, int page) {
        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL), User.getInstance(FeedActivity.this).getAccessToken());

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        body.put("latitude", String.valueOf(latitude));
        body.put("longitude", String.valueOf(longitude));
        body.put("page", String.valueOf(page));

        request.makeRequest(Request.Method.POST, "/v1/images/geolocation", headers, body, response -> {
                    try {
                        JSONObject responseJSON = new JSONObject(response);
                        totalPageNumber = responseJSON.getInt("totalPage");
                        JSONArray images = responseJSON.getJSONArray("images");
                        posts = new ArrayList<>();
                        for (int i = 0; i < images.length(); i++) {
                            JSONObject post = images.getJSONObject(i);
                            Log.d("Post", post.toString());
                            JSONObject author = post.getJSONObject("user");
                            JSONObject geolocation = post.getJSONObject("geolocation");
                            JSONArray coordinates = geolocation.getJSONArray("coordinates");
                            Geolocation location = new Geolocation(
                                    coordinates.getDouble(0),
                                    coordinates.getDouble(1),
                                    post.getString("address")
                            );
                            posts.add(new FeedPost(
                                    post.getString("name"),
                                    post.getString("description"),
                                    author.getString("name"),
                                    location,
                                    post.getString("image")
                            ));
                        }
                        isLoading = false;
                        renderPosts();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace
        );
    }

    public void renderPosts() {
        adapter.addItems(posts);
    }

    public void checkLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }

    }

    public void checkLocationProvider() {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertNoLocation();
        }
    }

    public void showAlertNoLocation() {
         dialog = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.activate_location_in_settings))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.activate), (dialog, id) -> {
                    dialog.dismiss();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton(getString(R.string.no), (dialog, id) -> dialog.cancel())
                .create();

        dialog.show();
    }
}