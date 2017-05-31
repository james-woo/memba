package com.james.memba.addberry;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.james.memba.R;

public class AddBerryFragment extends Fragment {

    private OnAddBerryLoadedListener mAddBerryLoadedListener;

    public AddBerryFragment() {
        // Required empty public constructor
    }

    public static AddBerryFragment newInstance() {
        AddBerryFragment fragment = new AddBerryFragment();
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
        View root = inflater.inflate(R.layout.add_berry_fragment, container, false);

        addBerryLoaded();

        return root;
    }

    private void addBerryLoaded() {
        if (mAddBerryLoadedListener != null) {
            mAddBerryLoadedListener.onAddBerryLoaded();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddBerryLoadedListener) {
            mAddBerryLoadedListener = (OnAddBerryLoadedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddBerryLoadedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddBerryLoadedListener = null;
    }

    public interface OnAddBerryLoadedListener {
        void onAddBerryLoaded();
    }
}
