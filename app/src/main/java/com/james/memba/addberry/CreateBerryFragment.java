package com.james.memba.addberry;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.james.memba.R;
import com.james.memba.model.Berry;
import com.james.memba.model.Entry;
import com.james.memba.utils.DateUtil;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Date;

public class CreateBerryFragment extends Fragment {

    private CreateBerryListener mCreateBerryListener;

    private TextView mUsernameTV;
    private TextView mDateTV;
    private EditText mTitleET;
    private ImageView mImageIV;
    private EditText mTextET;
    private TextView mLocationTV;

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
        View root = inflater.inflate(R.layout.add_fragment, container, false);
        setHasOptionsMenu(true);

        mUsernameTV = (TextView) root.findViewById(R.id.username);
        mDateTV = (TextView) root.findViewById(R.id.date);
        mTitleET = (EditText) root.findViewById(R.id.title);
        mImageIV = (ImageView) root.findViewById(R.id.image);
        mTextET = (EditText) root.findViewById(R.id.description);
        mLocationTV = (TextView) root.findViewById(R.id.location);

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
                createBerry(new Entry(title, String.valueOf(new Date().getTime()), mImagePath, text));
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

    public void showBerryHeader(String username, String location) {
        mLocationTV.setText(location);
        mUsernameTV.setText(username);
        mDateTV.setText(DateUtil.longToDate(new Date().getTime()));
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
