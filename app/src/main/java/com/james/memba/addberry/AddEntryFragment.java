package com.james.memba.addberry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.james.memba.R;
import com.james.memba.adapters.EntryAdapter;
import com.james.memba.model.Berry;
import com.james.memba.model.Entry;
import com.james.memba.utils.DateUtil;
import com.james.memba.utils.LocationUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

public class AddEntryFragment extends Fragment {

    private static final String ADD_TO_BERRY = "ADD_TO_EXISTING";

    private AddEntryListener mAddEntryListener;

    private TextView mUsernameTV;
    private TextView mDateTV;
    private EditText mTitleET;
    private ImageView mImageIV;
    private EditText mTextET;
    private TextView mLocationTV;
    private ListView mBerryList;
    private EntryAdapter mListAdapter;

    private String mImagePath;

    private Berry mBerry;

    public AddEntryFragment() {
        // Required empty public constructor
    }

    public static AddEntryFragment newInstance(Berry berry) {
        AddEntryFragment fragment = new AddEntryFragment();
        Bundle args = new Bundle();
        args.putSerializable(ADD_TO_BERRY, berry);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mBerry = (Berry) getArguments().getSerializable(ADD_TO_BERRY);
            mListAdapter = new EntryAdapter(mBerry.getEntries(), getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.add_fragment, container, false);
        setHasOptionsMenu(true);

        mUsernameTV = (TextView) root.findViewById(R.id.username);
        mUsernameTV.setText(mBerry.getUsername());
        mDateTV = (TextView) root.findViewById(R.id.date);
        mDateTV.setText(DateUtil.longToDate(Long.parseLong(mBerry.getUpdateDate())));
        mTitleET = (EditText) root.findViewById(R.id.title);
        mImageIV = (ImageView) root.findViewById(R.id.image);
        mImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });
        mTextET = (EditText) root.findViewById(R.id.description);
        mLocationTV = (TextView) root.findViewById(R.id.location);
        mLocationTV.setText(LocationUtil.getAddress(getContext(), mBerry.getLocation()));
        mBerryList = (ListView) root.findViewById(R.id.berryList);
        mBerryList.setAdapter(mListAdapter);

        addEntryLoaded();

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
                updateBerry(new Entry(title, String.valueOf(new Date().getTime()), mImagePath, text));
                Toast.makeText(getActivity(), "Added new memory", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addImage() {
        Intent intent = CropImage.activity()
                .getIntent(getContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void addEntryLoaded() {
        if (mAddEntryListener != null) {
            mAddEntryListener.onAddEntryLoaded();
        }
    }

    private void updateBerry(Entry entry) {
        if (mAddEntryListener != null) {
            mAddEntryListener.onUpdateBerry(mBerry.getId(), entry);
        }
    }

    private void handleCropResult(CropImageView.CropResult result) {
        if (result.getError() == null) {
            mImagePath = result.getUri().getEncodedPath();
            Bitmap bitmap = BitmapFactory.decodeFile(mImagePath);
            mImageIV.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getActivity(), "Image crop failed: " + result.getError().getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            handleCropResult(result);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddEntryListener) {
            mAddEntryListener = (AddEntryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CreateBerryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAddEntryListener = null;
    }

    public interface AddEntryListener {
        void onAddEntryLoaded();
        void onUpdateBerry(String berryId, Entry entry);
    }
}
