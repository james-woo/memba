/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.james.memba.model.source.remote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.james.memba.model.Berry;
import com.james.memba.model.source.BerryDataSource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of the data source that adds a latency simulating network.
 */
public class BerryRemoteDataSource implements BerryDataSource {

    private static BerryRemoteDataSource INSTANCE;

    private static final int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Berry> BERRY_SERVICE_DATA;

    static {
        BERRY_SERVICE_DATA = new LinkedHashMap<>(2);

        URL url = null;
        Bitmap image = null;
        try {
            url = new URL("https://pbs.twimg.com/profile_images/793365788010819584/U67zPvAg_400x400.jpg");
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        addBerry("1", "user1", "los cabos", image, "description", new Date(new Date().getTime()));
        addBerry("2", "user1", "los cabos", image, "description", new Date(new Date().getTime()));
    }

    public static BerryRemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BerryRemoteDataSource();
        }
        return INSTANCE;
    }

    // Prevent direct instantiation.
    private BerryRemoteDataSource() {}

    private static void addBerry(String id, String username, String location, Bitmap image, String description, Date date) {
        Berry newBerry = new Berry(id, username, location, image, description, date);
        BERRY_SERVICE_DATA.put(newBerry.getId(), newBerry);
    }

    /**
     * Note: {@link LoadBerriesCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getBerries(final @NonNull LoadBerriesCallback callback) {
        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onBerriesLoaded(Lists.newArrayList(BERRY_SERVICE_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    /**
     * Note: {@link GetBerryCallback#onDataNotAvailable()} is never fired. In a real remote data
     * source implementation, this would be fired if the server can't be contacted or the server
     * returns an error.
     */
    @Override
    public void getBerry(@NonNull String berryId, final @NonNull GetBerryCallback callback) {
        final Berry berry = BERRY_SERVICE_DATA.get(berryId);

        // Simulate network by delaying the execution.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onBerryLoaded(berry);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveBerry(@NonNull Berry berry) {
        BERRY_SERVICE_DATA.put(berry.getId(), berry);
    }

    @Override
    public void refreshBerries() {
        // Not required because the {@link BerryRepository} handles the logic of refreshing the
        // berries from all the available data sources.
    }

    @Override
    public void deleteAllBerries() {
        BERRY_SERVICE_DATA.clear();
    }

    @Override
    public void deleteBerry(@NonNull String berryId) {
        BERRY_SERVICE_DATA.remove(berryId);
    }
}
