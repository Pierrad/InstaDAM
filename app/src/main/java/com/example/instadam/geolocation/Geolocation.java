package com.example.instadam.geolocation;

public class Geolocation {
    private double latitude;
    private double longitude;
    private String address = "";

    public Geolocation(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Latitude = " + latitude +
                "\nLongitude = " + longitude;
    }

}
