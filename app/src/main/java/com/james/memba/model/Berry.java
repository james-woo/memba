package com.james.memba.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public class Berry {
    @NonNull
    private final String mId;

    @NonNull
    private final String mUsername;

    @NonNull
    private final String mLocation;

    @NonNull
    private final Bitmap mImage;

    @Nullable
    private final String mDescription;

    @NonNull
    private final Date mDate;

    public Berry(@NonNull String id, @NonNull String username, @NonNull String location, @NonNull Bitmap image, @Nullable String description, @NonNull Date date) {
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
    public String getLocation() {
        return mLocation;
    }

    @NonNull
    public Bitmap getImage() {
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
