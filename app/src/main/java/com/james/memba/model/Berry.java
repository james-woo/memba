package com.james.memba.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Berry implements Serializable
{
    // TODO: Add public/private functionality

    @SerializedName("_id") @Expose private String mId;
    @SerializedName("userId") @Expose private String mUserId;
    @SerializedName("userName") @Expose private String mUsername;
    @SerializedName("entries") @Expose private List<Entry> mEntries = null;
    @SerializedName("createDate") @Expose private String mCreateDate;
    @SerializedName("updateDate") @Expose private String mUpdateDate;
    @SerializedName("location") @Expose private Location mLocation;
    private final static long serialVersionUID = -2715039531261762861L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Berry() {
    }

    /**
     *
     * @param id
     * @param location
     * @param userId
     * @param username
     * @param entries
     * @param createDate
     */
    public Berry(String id, String userId, String username, List<Entry> entries, String createDate, String updateDate, Location location) {
        super();
        this.mId = id;
        this.mUserId = userId;
        this.mUsername = username;
        this.mEntries = entries;
        this.mCreateDate = createDate;
        this.mUpdateDate = updateDate;
        this.mLocation = location;
    }

    public Berry(String id, String userId, String username, Entry entry, String createDate, String updateDate, Location location) {
        super();
        this.mId = id;
        this.mUserId = userId;
        this.mUsername = username;
        this.mEntries = new ArrayList<Entry>();
        this.mEntries.add(entry);
        this.mCreateDate = createDate;
        this.mUpdateDate = updateDate;
        this.mLocation = location;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Berry withId(String id) {
        this.mId = id;
        return this;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }

    public Berry withUsername(String username) {
        this.mUsername = username;
        return this;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        this.mUserId = userId;
    }

    public Berry withUserId(String userId) {
        this.mUserId = userId;
        return this;
    }

    public List<Entry> getEntries() {
        return mEntries;
    }

    public void setEntries(List<Entry> entries) {
        this.mEntries = entries;
    }

    public Berry withEntries(List<Entry> entries) {
        this.mEntries = entries;
        return this;
    }

    public void addEntry(Entry entry) {
        if (mEntries != null) {
            mEntries.add(entry);
        } else {
            mEntries = new ArrayList<>();
            mEntries.add(entry);
        }
    }

    public String getUpdateDate() {
        return mUpdateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.mUpdateDate = updateDate;
    }

    public Berry withUpdateDate(String updateDate) {
        this.mUpdateDate = updateDate;
        return this;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(String createDate) {
        this.mCreateDate = createDate;
    }

    public Berry withCreateDate(String createDate) {
        this.mCreateDate = createDate;
        return this;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        this.mLocation = location;
    }

    public void setLocation(android.location.Location location) {
        this.mLocation = new Location(location);
    }

    public Berry withLocation(Location location) {
        this.mLocation = location;
        return this;
    }

    public static Berry parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(response, Berry.class);
    }

    public static Berry createBerry(Entry entry) {
        String date = String.valueOf(new Date().getTime());
        return new Berry(null, null, null, entry, date, date, null);
    }
}
