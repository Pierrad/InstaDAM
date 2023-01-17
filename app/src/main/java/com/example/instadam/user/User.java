package com.example.instadam.user;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.instadam.R;
import com.example.instadam.components.Post;

import java.util.List;

public class User {
    private static User INSTANCE;
    Context context;

    String id = "";
    String accessToken = "";
    String refreshToken = "";
    String username = "";
    String email = "";
    List<Post> posts;

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

    public List<Post> getPosts() { return posts; }

    public void setPosts(List<Post> posts) { this.posts = posts; }
}
