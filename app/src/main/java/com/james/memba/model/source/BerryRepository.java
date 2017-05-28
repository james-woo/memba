package com.james.memba.model.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.james.memba.model.Berry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class BerryRepository implements BerryDataSource {

    private static BerryRepository INSTANCE = null;

    private final BerryDataSource mBerryRemoteDataSource;

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Berry> mCachedBerries;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty = false;

    // Prevent direct instantiation.
    private BerryRepository(@NonNull BerryDataSource berryRemoteDataSource) {
        mBerryRemoteDataSource = checkNotNull(berryRemoteDataSource);
    }

    /**
     * Returns the single instance of this class, creating it if necessary.
     *
     * @param berryRemoteDataSource the backend data source
     * @return the {@link BerryRepository} instance
     */
    public static BerryRepository getInstance(BerryDataSource berryRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new BerryRepository(berryRemoteDataSource);
        }
        return INSTANCE;
    }

    /**
     * Used to force {@link #getInstance(BerryDataSource)} to create a new instance
     * next time it's called.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public void getBerries(@NonNull LoadBerriesCallback callback) {
        checkNotNull(callback);

        // Respond immediately with cache if available and not dirty
        if (mCachedBerries != null && !mCacheIsDirty) {
            callback.onBerriesLoaded(new ArrayList<>(mCachedBerries.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            getBerriesFromRemoteDataSource(callback);
        }
    }

    @Override
    public void getBerry(@NonNull String berryId, @NonNull final GetBerryCallback callback) {
        checkNotNull(berryId);
        checkNotNull(callback);

        Berry cachedBerry = getBerryWithId(berryId);

        // Respond immediately with cache if available
        if (cachedBerry != null) {
            callback.onBerryLoaded(cachedBerry);
            return;
        }

        // Load from server/persisted if needed.
        mBerryRemoteDataSource.getBerry(berryId, new GetBerryCallback() {
            @Override
            public void onBerryLoaded(Berry berry) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedBerries == null) {
                    mCachedBerries = new LinkedHashMap<>();
                }
                mCachedBerries.put(berry.getId(), berry);
                callback.onBerryLoaded(berry);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void saveBerry(@NonNull Berry berry) {
        checkNotNull(berry);
        mBerryRemoteDataSource.saveBerry(berry);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedBerries == null) {
            mCachedBerries = new LinkedHashMap<>();
        }
        mCachedBerries.put(berry.getId(), berry);
    }

    @Override
    public void refreshBerries() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllBerries() {
        mBerryRemoteDataSource.deleteAllBerries();

        if (mCachedBerries == null) {
            mCachedBerries = new LinkedHashMap<>();
        }
        mCachedBerries.clear();
    }

    @Override
    public void deleteBerry(@NonNull String berryId) {
        mBerryRemoteDataSource.deleteBerry(checkNotNull(berryId));

        mCachedBerries.remove(berryId);
    }

    private void getBerriesFromRemoteDataSource(@NonNull final LoadBerriesCallback callback) {
        mBerryRemoteDataSource.getBerries(new LoadBerriesCallback() {
            @Override
            public void onBerriesLoaded(List<Berry> berries) {
                refreshCache(berries);
                callback.onBerriesLoaded(new ArrayList<>(mCachedBerries.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshCache(List<Berry> berries) {
        if (mCachedBerries == null) {
            mCachedBerries = new LinkedHashMap<>();
        }
        mCachedBerries.clear();
        for (Berry berry : berries) {
            mCachedBerries.put(berry.getId(), berry);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private Berry getBerryWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedBerries == null || mCachedBerries.isEmpty()) {
            return null;
        } else {
            return mCachedBerries.get(id);
        }
    }
}
