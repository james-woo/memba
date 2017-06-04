package com.james.memba.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.adapters.BerryAdapter;
import com.james.memba.model.Berry;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private static final String HOME_BERRIES_LIST = "HOME_BERRIES_LIST";

    private BerryAdapter mListAdapter;
    private View mNoBerriesView;
    private ImageView mNoBerryIcon;
    private TextView mNoBerryMainView;
    private ListView mBerryList;

    private HomeListener mHomeListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListAdapter = new BerryAdapter(new ArrayList<Berry>(0), getContext());
        mListAdapter.setOnAddClickListener(new BerryAdapter.BerryAdapterListener() {
            @Override
            public void onAddClicked(int position) {
                addEntry(mListAdapter.getItem(position));
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.home_fragment, container, false);

        mBerryList = (ListView) root.findViewById(R.id.berry_list);
        mBerryList.setAdapter(mListAdapter);

        mNoBerriesView = root.findViewById(R.id.noBerries);
        mNoBerryIcon = (ImageView) root.findViewById(R.id.noBerriesIcon);
        mNoBerryMainView = (TextView) root.findViewById(R.id.noBerriesMain);

        homeLoaded();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showBerries(ArrayList<Berry> berries) {
        mListAdapter.replaceData(berries);
        mNoBerriesView.setVisibility(View.GONE);
        mBerryList.setVisibility(View.VISIBLE);
    }

    public void showNoBerries() {
        mBerryList.setVisibility(View.GONE);
        mNoBerriesView.setVisibility(View.VISIBLE);
    }

    public void homeLoaded() {
        if (mHomeListener != null) {
            mHomeListener.onHomeLoaded();
        }
    }

    public void addEntry(Berry berry) {
        if (mHomeListener != null) {
            mHomeListener.onAddEntry(berry);
        }
    }

    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeListener) {
            mHomeListener = (HomeListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHomeListener = null;
    }

    public interface HomeListener {
        void onHomeLoaded();
        void onAddEntry(Berry berry);
    }
}
