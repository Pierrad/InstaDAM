package com.example.instadam.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.instadam.R;
import com.example.instadam.components.Post;
import com.example.instadam.geolocation.Geolocation;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The FeedActivity shows a list of posts around the user's location. The posts are paginated and the user can scroll to load more posts.
 * This activity uses a RecyclerView to show the posts and a PaginationListener to load more posts when the user scrolls to the end of the list.
 */
public class FeedActivity extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    private LinearLayoutManager layoutManager;
    private Geocoder geocoder;

    private RecyclerView postsRecyclerList;
    private ArrayList<Post> posts = new ArrayList<>();
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
        geocoder = new Geocoder(this);

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

    /**
     * Called when the request permissions has been answered.
     *
     * @param requestCode  The request code.
     * @param permissions  The permissions.
     * @param grantResults The results.
     */
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

    /**
     * Called when the location has changed.
     *
     * @param location The new location.
     */
    @Override
    public void onLocationChanged(@NonNull Location location) {
        User.getInstance(this).setCurrentPosition(new Geolocation(location.getLatitude(), location.getLongitude()));
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

    /**
     * Fetches the posts from the API.
     *
     * @param latitude  The latitude of the user.
     * @param longitude The longitude of the user.
     * @param page      The page number to fetch.
     */
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
                            double latitudePost = coordinates.getDouble(0);
                            double longitudePost = coordinates.getDouble(1);
                            Geolocation location = new Geolocation(
                                    latitudePost,
                                    longitudePost
                            );

                            AddressTask task = new AddressTask(location);
                            task.execute(latitudePost, longitudePost);

                            posts.add(new Post(
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

    /**
     * Renders the posts in the RecyclerView.
     * addItems triggers the adapter to update the RecyclerView with new posts.
     */
    public void renderPosts() {
        adapter.addItems(posts);
    }

    /**
     * Checks if the user has granted the location permission.
     * If not, it asks for the permission.
     */
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

    /**
     * Checks if the location provider is enabled.
     * If not, it asks the user to enable it.
     */
    public void checkLocationProvider() {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertNoLocation();
        }
    }

    /**
     * Shows an alert to the user to enable the location provider.
     * If the user clicks on the "OK" button, it redirects him to the location settings using a new intent.
     */
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

    /**
     * AsyncTask to get the address from the latitude and longitude because "getAddressFromGeocoder" is an heavy operation
     * that is causing the UI thread to skip frames. To avoid this, we use an AsyncTask.
     */
    @SuppressLint("StaticFieldLeak")
    private class AddressTask extends AsyncTask<Double, Void, String> {
        private final Geolocation location;

        public AddressTask(Geolocation location) {
            this.location = location;
        }

        @Override
        protected String doInBackground(Double... coordinates) {
            return getAddressFromGeocoder(coordinates[0], coordinates[1]);
        }

        @Override
        protected void onPostExecute(String address) {
            location.setAddress(address);
        }
    }

    /**
     * Gets the address from the latitude and longitude using GeoCoder API.
     * @param latitude The latitude of the user.
     * @param longitude The longitude of the user.
     * @return The address of the user.
     */
    public String getAddressFromGeocoder(double latitude, double longitude) {
        String address = "";
        try {
            List<Address> addressPost = geocoder.getFromLocation(longitude, latitude, 1);
            if (addressPost.size() > 0) {
                address = addressPost.get(0).getAddressLine(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }
}