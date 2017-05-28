package com.james.memba.home;

import android.support.annotation.NonNull;

import com.james.memba.model.Berry;
import com.james.memba.model.source.BerryDataSource;
import com.james.memba.model.source.BerryRepository;
import com.james.memba.utils.EspressoIdlingResource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class HomePresenter implements HomeContract.Presenter {

    private final BerryRepository mBerryRepository;

    private final HomeContract.View mHomeView;

    private boolean mFirstLoad = true;

    public HomePresenter(@NonNull BerryRepository berryRepository, @NonNull HomeContract.View homeView) {
        mBerryRepository = checkNotNull(berryRepository, "berryRepository cannot be null");
        mHomeView = checkNotNull(homeView, "homeView cannot be null!");

        mHomeView.setPresenter(this);
    }

    @Override
    public void start() {
        loadBerries(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a task was successfully added, show snackbar
        //if (AddBerryActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            //mHomeView.showSuccessfullySavedMessage();
        //}
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link BerryDataSource}
     */
    public void loadBerries(boolean forceUpdate) {

        if (forceUpdate) {
            mBerryRepository.refreshBerries();
        }

        // The network request might be handled in a different thread so make sure Espresso knows
        // that the app is busy until the response is handled.
        EspressoIdlingResource.increment(); // App is busy until further notice

        mBerryRepository.getBerries(new BerryDataSource.LoadBerriesCallback() {
            @Override
            public void onBerriesLoaded(List<Berry> berries) {
                // This callback may be called twice, once for the cache and once for loading
                // the data from the server API, so we check before decrementing, otherwise
                // it throws "Counter has been corrupted!" exception.
                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
                    EspressoIdlingResource.decrement(); // Set app as idle.
                }

                // The view may not be able to handle UI updates anymore
                if (!mHomeView.isActive()) {
                    return;
                }

                processBerries(berries);
            }

            @Override
            public void onDataNotAvailable() {
                // The view may not be able to handle UI updates anymore
                if (!mHomeView.isActive()) {
                    return;
                }
                mHomeView.showLoadingBerriesError();
            }
        });

        mFirstLoad = false;
    }

    private void processBerries(List<Berry> berries) {
        if (berries.isEmpty()) {
            processEmptyTasks();
        } else {
            // Show the list of berries
            mHomeView.showBerries(berries);
        }
    }

    private void processEmptyTasks() {
        mHomeView.showNoBerries();
    }

    @Override
    public void addNewBerry() {

    }

    @Override
    public void openMap() {

    }
}
