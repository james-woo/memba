package com.james.memba.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.model.Berry;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String HOME_BERRIES_LIST = "HOME_BERRIES_LIST";

    private BerryAdapter mListAdapter;
    private View mNoBerriesView;
    private ImageView mNoBerryIcon;
    private TextView mNoBerryMainView;
    private LinearLayout mBerriesView;

    private ArrayList<Berry> mBerries;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(ArrayList<Berry> berries) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putSerializable(HOME_BERRIES_LIST, berries);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListAdapter = new BerryAdapter(new ArrayList<Berry>(0));

        if (getArguments() != null) {
            mBerries = (ArrayList<Berry>) getArguments().getSerializable(HOME_BERRIES_LIST);
        }
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

        if (mBerries.isEmpty()) {
            showNoBerries();
        } else {
            showBerries(mBerries);
        }

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showBerries(List<Berry> berries) {
        mListAdapter.replaceData(berries);

        mBerriesView.setVisibility(View.VISIBLE);
        mNoBerriesView.setVisibility(View.GONE);
    }

    public void showNoBerries() {
        mBerriesView.setVisibility(View.GONE);
        mNoBerriesView.setVisibility(View.VISIBLE);
    }

    public boolean isActive() {
        return isAdded();
    }
}
