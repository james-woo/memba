package com.james.memba.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public class Berry implements Serializable {
    @NonNull
    private final String mId;

    @NonNull
    private final String mUsername;

    @NonNull
    private final Location mLocation;

    @NonNull
    private final String mImage;

    @Nullable
    private final String mDescription;

    @NonNull
    private final Date mDate;

    public Berry(@NonNull String id, @NonNull String username, @NonNull String image, @Nullable String description, @NonNull Date date, @NonNull Location location) {
        mId = id;
        mUsername = username;
        mLocation = location;
        mImage = image;
        mDescription = description;
        mDate = date;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getUsername() {
        return mUsername;
    }

    @NonNull
    public Location getLocation() {
        return mLocation;
    }

    @NonNull
    public String getImage() {
        return mImage;
    }

    @NonNull
    public String getDescription() {
        return mDescription;
    }

    @NonNull
    public String getDate() {
        return mDate.toString();
    }
}
