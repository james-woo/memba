package com.james.memba.model;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location implements Serializable {

    /* Location is a datastructure that only holds latitude and longitude for berries,
     */

    @SerializedName("lat")
    @Expose
    public double lat;
    @SerializedName("lng")
    @Expose
    public double lng;
    private final static long serialVersionUID = 4410056027047520818L;

    /**
     * No args constructor for use in serialization
     */
    public Location() {
    }

    /**
     * @param lng
     * @param lat
     */
    public Location(double lat, double lng) {
        super();
        this.lat = lat;
        this.lng = lng;
    }

    public Location(android.location.Location location) {
        super();
        this.lat = location.getLatitude();
        this.lng = location.getLongitude();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Location withLat(double lat) {
        this.lat = lat;
        return this;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Location withLng(double lng) {
        this.lng = lng;
        return this;
    }
}