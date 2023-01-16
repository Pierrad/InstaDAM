package com.example.instadam.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.instadam.R;

/**
 * User represents a user.
 * It implements the Singleton pattern to ensure that only one instance of the User class exists.
 */
public class User {
    private static User INSTANCE;
    Context context;

    String id = "";
    String accessToken = "";
    String refreshToken = "";
    String username = "";
    String email = "";

    private User(Context c) {
        this.context = c;
    }

    public static User getInstance(Context c) {
        if (INSTANCE == null) {
            INSTANCE = new User(c);
        }
        return INSTANCE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    /**
     * Sets the refresh token and saves it in SharedPreferences.
     * @param refreshToken the refresh token
     */
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;

        SharedPreferences sharedPreferences = context.getSharedPreferences(String.valueOf(R.string.SP_USER), Context.MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("refreshToken", refreshToken);
        myEdit.apply();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
