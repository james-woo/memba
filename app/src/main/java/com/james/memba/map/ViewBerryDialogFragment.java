package com.james.memba.map;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.model.Berry;
import com.james.memba.model.Entry;
import com.james.memba.model.Location;
import com.james.memba.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ViewBerryDialogFragment extends DialogFragment {

    TextView mUserNameTV;
    TextView mLocationTV;
    LinearLayout mEntriesLL;
    TextView mDateTV;

    public ViewBerryDialogFragment() {

    }

    public static ViewBerryDialogFragment newInstance(Berry berry) {
        ViewBerryDialogFragment frag = new ViewBerryDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("BERRY", berry);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.berry_item, container);

        mUserNameTV = (TextView) view.findViewById(R.id.username);
        mLocationTV = (TextView) view.findViewById(R.id.location);
        mEntriesLL = (LinearLayout) view.findViewById(R.id.entry_list);
        mDateTV = (TextView) view.findViewById(R.id.date);

        // Fetch arguments from bundle and set title
        Berry berry = (Berry) getArguments().getSerializable("BERRY");

        mUserNameTV.setText(berry.getUsername());
        mLocationTV.setText(getAddress(berry.getLocation()));
        mDateTV.setText(DateUtil.longToDate(Long.parseLong(berry.getUpdateDate())));

        insertEntries(mEntriesLL, inflater, container, berry.getEntries());

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void insertEntries(LinearLayout ll, LayoutInflater inflater, ViewGroup viewGroup, List<Entry> entries) {
        for (Entry e : entries) {
            View child = inflater.inflate(R.layout.entry_item, viewGroup, false);
            TextView titleTV = (TextView) child.findViewById(R.id.title);
            titleTV.setText(e.getTitle());

            TextView dateTV = (TextView) child.findViewById(R.id.date);
            dateTV.setText(DateUtil.longToDate(Long.parseLong(e.getDate())));

            ImageView image = (ImageView) child.findViewById(R.id.image);
            Picasso.with(getActivity()).load(e.getImage()).into(image);

            TextView textTV = (TextView) child.findViewById(R.id.text);
            textTV.setText(e.getText());

            ll.addView(child);
        }
    }

    private String getAddress(Location location) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.lat, location.lng, 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
