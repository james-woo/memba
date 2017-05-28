package com.james.memba.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.model.Berry;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link Berry}s.
 */
public class HomeFragment extends Fragment implements HomeContract.View {
    private HomeContract.Presenter mPresenter;

    private BerryAdapter mListAdapter;

    private View mNoBerriesView;

    private ImageView mNoBerryIcon;

    private TextView mNoBerryMainView;

    private LinearLayout mBerriesView;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListAdapter = new BerryAdapter(new ArrayList<Berry>(0));
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(@NonNull HomeContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        ListView listView = (ListView) root.findViewById(R.id.berry_list);
        listView.setAdapter(mListAdapter);
        mBerriesView = (LinearLayout) root.findViewById(R.id.berryLL);

        mNoBerriesView = root.findViewById(R.id.noBerries);
        mNoBerryIcon = (ImageView) root.findViewById(R.id.noBerriesIcon);
        mNoBerryMainView = (TextView) root.findViewById(R.id.noBerriesMain);

        mPresenter.loadBerries(true);

        return root;
    }

    @Override
    public void showLoadingBerriesError() {
        showMessage("Error while loading berries");
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showBerries(List<Berry> berries) {
        mListAdapter.replaceData(berries);

        mBerriesView.setVisibility(View.VISIBLE);
        mNoBerriesView.setVisibility(View.GONE);
    }

    @Override
    public void showNoBerries() {
        mBerriesView.setVisibility(View.GONE);
        mNoBerriesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showAddBerry() {
        //Intent intent = new Intent(getContext(), AddBerryActivity.class);
        //startActivityForResult(intent, AddBerryActivity.REQUEST_ADD_BERRY);
    }

    @Override
    public void showMap() {
        //Intent intent = new Intent(getContext(), ViewMapActivity.class);
        //startActivity(intent);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private static class BerryAdapter extends BaseAdapter {
        private List<Berry> mBerries;

        public BerryAdapter(List<Berry> berries) {
            setList(berries);
        }

        public void replaceData(List<Berry> berries) {
            setList(berries);
            notifyDataSetChanged();
        }

        private void setList(List<Berry> berries) {
            mBerries = checkNotNull(berries);
        }

        @Override
        public int getCount() {
            return mBerries.size();
        }

        @Override
        public Berry getItem(int i) {
            return mBerries.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.berry_item, viewGroup, false);
            }

            final Berry berry = getItem(i);

            TextView usernameTV = (TextView) rowView.findViewById(R.id.username);
            usernameTV.setText(berry.getUsername());

            TextView locationTV = (TextView) rowView.findViewById(R.id.location);
            locationTV.setText(berry.getLocation());

            ImageView image = (ImageView) rowView.findViewById(R.id.image);
            image.setImageBitmap(berry.getImage());

            TextView descriptionTV = (TextView) rowView.findViewById(R.id.description);
            descriptionTV.setText(berry.getDescription());

            TextView dateTV = (TextView) rowView.findViewById(R.id.date);
            dateTV.setText(berry.getDate());

            return rowView;
        }
    }
}
