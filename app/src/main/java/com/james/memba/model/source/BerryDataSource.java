package com.james.memba.model.source;

import android.support.annotation.NonNull;

import com.james.memba.model.Berry;

import java.util.List;

/**
 * Main entry point for accessing berry data.
 * <p>
 * For simplicity, only getBerries() and getBerry() have callbacks. Consider adding callbacks to other
 * methods to inform the user of network/database errors or successful operations.
 * For example, when a new berry is created, it's synchronously stored in cache but usually every
 * operation on database or network should be executed in a different thread.
 */
public interface BerryDataSource {
    interface LoadBerriesCallback {

        void onBerriesLoaded(List<Berry> berries);

        void onDataNotAvailable();
    }

    interface GetBerryCallback {

        void onBerryLoaded(Berry berry);

        void onDataNotAvailable();
    }

    void getBerries(@NonNull LoadBerriesCallback callback);

    void getBerry(@NonNull String berryId, @NonNull GetBerryCallback callback);

    void saveBerry(@NonNull Berry berry);

    void refreshBerries();

    void deleteAllBerries();

    void deleteBerry(@NonNull String berryId);
}
