package com.example.instadam.components;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.instadam.geolocation.Geolocation;

/**
 * Post represents a post in the feed.
 */
public class Post {

    private String id;
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

    public Post(String id, String name, String image, Geolocation geolocation) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.geolocation = geolocation;
    }

    public String getId() {
        return id;
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

    /**
     * Returns the image of the post as a Bitmap.
     * @return the image of the post as a Bitmap
     */
    public Bitmap getImage() {
        // Decode the base64 string into a byte array
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);

        // Use the BitmapFactory to create a Bitmap from the byte array
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
