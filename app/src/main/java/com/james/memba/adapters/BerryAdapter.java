package com.james.memba.adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import static com.google.common.base.Preconditions.checkNotNull;

public class BerryAdapter extends BaseAdapter {
    private List<Berry> mBerries;
    private Context mContext;
    private BerryAdapterListener listener;

    public BerryAdapter(List<Berry> berries, Context context) {
        setList(berries);
        mContext = context;
    }

    public void replaceData(List<Berry> berries) {
        setList(berries);
        notifyDataSetChanged();
    }

    private void setList(List<Berry> berries) {
        mBerries = checkNotNull(berries);
    }

    @Override
    public int getCount() {
        return mBerries.size();
    }

    @Override
    public Berry getItem(int i) {
        return mBerries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        LayoutInflater inflater = null;
        if (rowView == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.berry_item, viewGroup, false);
        } else {
            rowView = view;
        }

        final Berry berry = getItem(i);

        TextView usernameTV = (TextView) rowView.findViewById(R.id.username);
        usernameTV.setText(berry.getUsername());

        TextView locationTV = (TextView) rowView.findViewById(R.id.location);
        locationTV.setText(LocationUtil.getAddress(mContext, berry.getLocation()));

        // Create a layout to insert entries (a "listview" within a listview)
        if (inflater != null) {
            LinearLayout entryLL = (LinearLayout) rowView.findViewById(R.id.entry_list);
            insertEntries(entryLL, inflater, viewGroup, berry.getEntries());
        }

        TextView dateTV = (TextView) rowView.findViewById(R.id.date);
        dateTV.setText(DateUtil.longToDate(Long.parseLong(berry.getUpdateDate())));

        // If a user clicks the add button, let the main activity know to handle it
        TextView addTV = (TextView) rowView.findViewById(R.id.add);
        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAddClicked(i);
                }
            }
        });

        return rowView;
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
                Picasso.with(mContext).load(path).into(image);
            }

            TextView textTV = (TextView) child.findViewById(R.id.text);
            textTV.setText(e.getText());

            ll.addView(child);
        }
    }

    public void setOnAddClickListener(BerryAdapterListener listener) {
        this.listener = listener;
    }

    public interface BerryAdapterListener {
        void onAddClicked(int position);
    }
}