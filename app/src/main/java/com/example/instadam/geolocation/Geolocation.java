package com.example.instadam.geolocation;

/**
 * Geolocation represents a geolocation.
 * It contains the latitude and longitude of the geolocation and the address calculated using Geocoder API.
 */
public class Geolocation {
    private final double latitude;
    private final double longitude;
    private String address = "";

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
