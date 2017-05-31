package com.james.memba.map;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.james.memba.R;

public class ViewMapFragment extends Fragment {
    private OnViewMapLoadedListener mViewMapLoadedListener;

    public ViewMapFragment() {
        // Required empty public constructor
    }

    public static ViewMapFragment newInstance() {
        ViewMapFragment fragment = new ViewMapFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.view_map_fragment, container, false);

        mapLoaded();

        return root;
    }

    private void mapLoaded() {
        if (mViewMapLoadedListener != null) {
            mViewMapLoadedListener.onViewMapLoaded();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnViewMapLoadedListener) {
            mViewMapLoadedListener = (OnViewMapLoadedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnViewMapLoadedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewMapLoadedListener = null;
    }

    public interface OnViewMapLoadedListener {
        void onViewMapLoaded();
    }
}
