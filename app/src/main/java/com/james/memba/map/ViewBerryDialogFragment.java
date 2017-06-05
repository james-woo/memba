package com.james.memba.map;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.model.Berry;
import com.james.memba.model.Entry;
import com.james.memba.model.Location;
import com.james.memba.utils.DateUtil;
import com.james.memba.utils.LocationUtil;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ViewBerryDialogFragment extends DialogFragment {

    private ViewBerryListener mViewBerryListener;

    private TextView mUserNameTV;
    private TextView mLocationTV;
    private LinearLayout mEntriesLL;
    private TextView mDateTV;
    private TextView mAddTV;

    private Berry mBerry;

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
        View view = inflater.inflate(R.layout.view_berry_fragment, container);

        mUserNameTV = (TextView) view.findViewById(R.id.username);
        mLocationTV = (TextView) view.findViewById(R.id.location);
        mEntriesLL = (LinearLayout) view.findViewById(R.id.entry_list);
        mDateTV = (TextView) view.findViewById(R.id.date);

        // Fetch arguments from bundle and set title
        mBerry = (Berry) getArguments().getSerializable("BERRY");

        mUserNameTV.setText(mBerry.getUsername());
        mLocationTV.setText(LocationUtil.getAddress(getActivity(), mBerry.getLocation()));
        mDateTV.setText(DateUtil.longToDate(Long.parseLong(mBerry.getUpdateDate())));

        insertEntries(mEntriesLL, inflater, container, mBerry.getEntries());

        // Allow user to add new entry to berry
        mAddTV = (TextView) view.findViewById(R.id.add);
        mAddTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntryTo(mBerry);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Make dialog full screen
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make dialog full screen
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
            image.setImageBitmap(null);
            String path = e.getImage();
            if (path != null && !path.isEmpty()) {
                Picasso.with(getActivity()).load(path).into(image);
            }

            TextView textTV = (TextView) child.findViewById(R.id.text);
            textTV.setText(e.getText());

            ll.addView(child);
        }
    }

    // Callback when "ADD" is clicked
    private void addEntryTo(Berry berry) {
        if (mViewBerryListener != null) {
            mViewBerryListener.onAddEntryTo(berry);
        }
        getDialog().dismiss();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewBerryListener) {
            mViewBerryListener = (ViewBerryListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ViewBerryListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewBerryListener = null;
    }

    public interface ViewBerryListener {
        void onAddEntryTo(Berry berry);
    }
}
