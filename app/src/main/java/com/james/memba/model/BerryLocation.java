package com.james.memba.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BerryLocation {

    @SerializedName("_id") @Expose private String mId;
    @SerializedName("location") @Expose private Location mLocation;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        this.mLocation = location;
    }

}