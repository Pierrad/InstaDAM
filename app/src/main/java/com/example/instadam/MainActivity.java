package com.example.instadam;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.instadam.feed.FeedActivity;
import com.example.instadam.helpers.HTTPRequest;
import com.example.instadam.user.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disable dark mode

        registerElements();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkAccount();
            }
        }, 3000);
    }

    private void registerElements() {
        ImageView logo = findViewById(R.id.splash_logo);
        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.appear));
    }

    public void checkAccount() {
        SharedPreferences sh = getSharedPreferences(String.valueOf(R.string.SP_USER), MODE_PRIVATE);
        String refreshToken = sh.getString("refreshToken", null);

        RequestQueue queue = Volley.newRequestQueue(this);
        HTTPRequest request = new HTTPRequest(queue, getString(R.string.API_URL));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        request.makeRequest(Request.Method.POST, "/v1/auth/me", headers, body, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject user = jsonResponse.getJSONObject("user");
                            JSONObject tokens = jsonResponse.getJSONObject("tokens");
                            JSONObject accessToken = tokens.getJSONObject("access");
                            JSONObject refreshToken = tokens.getJSONObject("refresh");

                            Log.d("response", response);

                            if (tokens.has("access")) {
                                User.getInstance(MainActivity.this).setEmail(user.getString("email"));
                                User.getInstance(MainActivity.this).setUsername(user.getString("name"));
                                User.getInstance(MainActivity.this).setAccessToken(accessToken.getString("token"));
                                User.getInstance(MainActivity.this).setRefreshToken(refreshToken.getString("token"));

                                redirectToFeedActivity();
                            } else {
                                redirectToLoginActivity();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            redirectToLoginActivity();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        redirectToLoginActivity();
                    }
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

}