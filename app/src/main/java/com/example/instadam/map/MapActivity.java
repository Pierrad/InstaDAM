package com.example.instadam.map;

import android.content.Context;
import android.preference.PreferenceManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.example.instadam.R;
import com.example.instadam.user.User;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;


public class MapActivity extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.instadam.R.layout.activity_map);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setCacheMapTileCount((short) 12);
        Configuration.getInstance().setCacheMapTileOvershoot((short) 12);

        map = findViewById(R.id.map);
        map.getTileProvider().clearTileCache();
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(11.0);
        mapController.setCenter(new GeoPoint(User.getInstance(this).getCurrentPosition().getLatitude(), User.getInstance(this).getCurrentPosition().getLongitude()));

        User.getInstance(this).getPosts().forEach(post -> addMarker(post.getGeolocation().getLongitude(), post.getGeolocation().getLatitude(), post.getName()));

        map.invalidate();
    }

    public void addMarker(double longitude, double latitude, String name) {
        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(longitude, latitude));
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.marker, null));
        marker.setTitle(name);
        map.getOverlays().add(marker);
    }
}
