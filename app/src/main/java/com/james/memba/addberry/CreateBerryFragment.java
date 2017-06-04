package com.james.memba.addberry;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
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

public class CreateBerryFragment extends Fragment {

    private CreateBerryListener mCreateBerryListener;

    private EditText mTitleET;
    private ImageView mImageIV;
    private EditText mTextET;

    private String mImagePath;

    public CreateBerryFragment() {
        // Required empty public constructor
    }

    public static CreateBerryFragment newInstance() {
        CreateBerryFragment fragment = new CreateBerryFragment();
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

        root.findViewById(R.id.berryList).setVisibility(View.GONE);

        createBerryLoaded();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        item = menu.findItem(R.id.action_sign_out);
        item.setVisible(false);
        inflater.inflate(R.menu.create_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                String title = mTitleET.getText().toString();
                String text = mTextET.getText().toString();
                createBerry(new Entry(title, mImagePath, text));
                Toast.makeText(getActivity(), "Added new memory", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createBerryLoaded() {
        if (mCreateBerryListener != null) {
            mCreateBerryListener.onCreateBerryLoaded();
        }
    }

    private void createBerry(Entry entry) {
        if (mCreateBerryListener != null) {
            mCreateBerryListener.onCreateBerry(Berry.createBerry(entry));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CreateBerryListener) {
            mCreateBerryListener = (CreateBerryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CreateBerryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCreateBerryListener = null;
    }

    public interface CreateBerryListener {
        void onCreateBerryLoaded();
        void onCreateBerry(Berry berry);
    }
}
