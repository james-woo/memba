package com.james.memba.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.james.memba.model.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {
    public static String getAddress(Context context, Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.lat, location.lng, 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getAddress(Context context, android.location.Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
