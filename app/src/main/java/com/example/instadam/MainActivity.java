package com.example.instadam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.instadam.auth.LoginActivity;
import com.example.instadam.camera.CameraActivity;
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.notification.AlarmReceiver;
import com.example.instadam.user.User;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * The MainActivity shows a splash screen while checking if the user is logged in.
 * If the user is logged in, the MainActivity redirects to the FeedActivity. If the user is not logged in, the MainActivity redirects to the LoginActivity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disable dark mode

        showAnimatedLogo();

        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        MainActivity.this.startActivity(intent);
       /*  new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                triggerDailyNotification();
                checkAccount();
            }
        }, 3000); */
    }

    private void showAnimatedLogo() {
        ImageView logo = findViewById(R.id.splash_logo);
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.appear));
    }

    /**
     * Check if the user is logged in using a possible refresh token set in the SharedPreferences.
     * If the user is logged in, this method sets the User object and redirects to the FeedActivity.
     * If the user is not logged in, the MainActivity redirects to the LoginActivity.
     */
    public void checkAccount() {
        SharedPreferences sh = getSharedPreferences(String.valueOf(R.string.SP_USER), MODE_PRIVATE);
        String refreshToken = sh.getString("refreshToken", null);

        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        request.makeRequest(Request.Method.POST, "/v1/auth/me", headers, body, response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONObject user = jsonResponse.getJSONObject("user");
                        JSONObject tokens = jsonResponse.getJSONObject("tokens");
                        JSONObject accessToken = tokens.getJSONObject("access");
                        JSONObject refreshToken1 = tokens.getJSONObject("refresh");

                        if (tokens.has("access")) {
                            User.getInstance(MainActivity.this).setId(user.getString("id"));
                            User.getInstance(MainActivity.this).setEmail(user.getString("email"));
                            User.getInstance(MainActivity.this).setUsername(user.getString("name"));
                            User.getInstance(MainActivity.this).setAccessToken(accessToken.getString("token"));
                            User.getInstance(MainActivity.this).setRefreshToken(refreshToken1.getString("token"));

                            redirectToFeedActivity();
                        } else {
                            redirectToLoginActivity();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        redirectToLoginActivity();
                    }
                }, error -> {
                    error.printStackTrace();
                    redirectToLoginActivity();
                }
        );
    }

    public void redirectToFeedActivity() {
        Intent intent = new Intent(MainActivity.this, FeedActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void redirectToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(intent);
    }

    /**
     * Set a daily notification at 19:00.
     */
    public void triggerDailyNotification() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 19);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTime().compareTo(new Date()) < 0) calendar.add(Calendar.HOUR_OF_DAY, 0);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

}