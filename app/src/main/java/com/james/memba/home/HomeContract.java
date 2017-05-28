package com.james.memba.home;

import com.james.memba.BasePresenter;
import com.james.memba.BaseView;
import com.james.memba.model.Berry;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface HomeContract {

    interface View extends BaseView<Presenter> {

        void showLoadingBerriesError();

        void showBerries(List<Berry> berries);

        void showNoBerries();

        void showAddBerry();

        void showMap();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadBerries(boolean forceUpdate);

        void addNewBerry();

        void openMap();
    }
}
