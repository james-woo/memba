package com.james.memba.addberry;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.james.memba.R;
import com.james.memba.model.Berry;
import com.james.memba.model.Entry;

import java.util.ArrayList;
import java.util.Date;

public class AddBerryFragment extends Fragment {

    private OnAddBerryLoadedListener mAddBerryLoadedListener;
    private OnAddBerryListener mAddBerryListener;

    private EditText mTitleET;
    private ImageView mImageIV;
    private EditText mTextET;

    private String mImagePath;

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
        setHasOptionsMenu(true);

        mTitleET = (EditText) root.findViewById(R.id.title);
        mImageIV = (ImageView) root.findViewById(R.id.image);
        mTextET = (EditText) root.findViewById(R.id.description);

        addBerryLoaded();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        item = menu.findItem(R.id.action_sign_out);
        item.setVisible(false);
        inflater.inflate(R.menu.add_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                String title = mTitleET.getText().toString();
                String text = mTextET.getText().toString();
                addBerry(title, mImagePath, text);
                Toast.makeText(getActivity(), "Added new memory", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addBerryLoaded() {
        if (mAddBerryLoadedListener != null) {
            mAddBerryLoadedListener.onAddBerryLoaded();
        }
    }

    private void addBerry(String title, String image, String text) {
        if (mAddBerryListener != null) {
            mAddBerryListener.onAddBerry(Berry.createBerry(title, image, text));
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

        if (context instanceof OnAddBerryListener) {
            mAddBerryListener = (OnAddBerryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddBerryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddBerryLoadedListener = null;
        mAddBerryListener = null;
    }

    public interface OnAddBerryLoadedListener {
        void onAddBerryLoaded();
    }

    public interface OnAddBerryListener {
        void onAddBerry(Berry berry);
    }
}
