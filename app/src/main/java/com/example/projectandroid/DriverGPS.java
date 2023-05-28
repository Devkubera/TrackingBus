package com.example.projectandroid;

public class DriverGPS {

    public String address;
    public double lat;
    public double lng;
    public String locality;

    public DriverGPS() {

    }

    public DriverGPS(double lat, double lng, String local, String address) {
        this.lat = lat;
        this.lng = lng;
        this.locality = local;
        this.address = address;
    }

    public DriverGPS(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
