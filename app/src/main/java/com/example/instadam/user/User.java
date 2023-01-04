package com.example.instadam.user;

import android.content.Context;

public class User {
    private static User INSTANCE;
    Context context;

    String token;

    private User(Context c) {
        this.context = c;
    }

    public static User getInstance(Context c) {
        if (INSTANCE == null) {
            INSTANCE = new User(c);
        }
        return INSTANCE;
    }

}
