package com.example.instadam.geolocation;

public class Geolocation {
    private double latitude;
    private double longitude;

    public Geolocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Latitude = " + latitude +
                "\nLongitude = " + longitude;
    }

}
