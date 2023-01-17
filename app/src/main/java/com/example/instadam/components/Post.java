package com.example.instadam.components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.instadam.geolocation.Geolocation;

public class Post {
    private String name;
    private String description;
    private String author;
    private Geolocation geolocation;
    private String image;

    public Post(String name, String description, String author, Geolocation geolocation, String image) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.geolocation = geolocation;
        this.image = image;
    }

    public Post(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public Bitmap getImage() {
        // Decode the base64 string into a byte array
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);

        // Use the BitmapFactory to create a Bitmap from the byte array
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @Override
    public String toString() {
        return "FeedPost{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", geolocation=" + geolocation +
                ", image='" + image + '\'' +
                '}';
    }
}
