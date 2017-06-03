package com.james.memba.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.model.Berry;
import com.james.memba.model.Entry;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class BerryAdapter extends BaseAdapter {
    private List<Berry> mBerries;
    private Context mContext;

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        LayoutInflater inflater;
        if (rowView == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.berry_item, viewGroup, false);

            final Berry berry = getItem(i);

            TextView usernameTV = (TextView) rowView.findViewById(R.id.username);
            usernameTV.setText(berry.getUserId());

            TextView locationTV = (TextView) rowView.findViewById(R.id.location);
            locationTV.setText(berry.getLocation().toString());

            LinearLayout entryLL = (LinearLayout) rowView.findViewById(R.id.entry_list);
            insertEntries(entryLL, inflater, viewGroup, berry.getEntries());

            TextView dateTV = (TextView) rowView.findViewById(R.id.date);
            dateTV.setText(berry.getCreateDate().toString());
        }

        return rowView;
    }

    private void insertEntries(LinearLayout ll, LayoutInflater inflater, ViewGroup viewGroup, List<Entry> entries) {
        for (Entry e : entries) {
            View child = inflater.inflate(R.layout.entry_item, viewGroup, false);
            TextView titleTV = (TextView) child.findViewById(R.id.title);
            titleTV.setText(e.getTitle());

            TextView dateTV = (TextView) child.findViewById(R.id.date);
            dateTV.setText(e.getDate());

            ImageView image = (ImageView) child.findViewById(R.id.image);
            Picasso.with(mContext).load(e.getImage()).into(image);

            TextView textTV = (TextView) child.findViewById(R.id.text);
            textTV.setText(e.getText());

            ll.addView(child);
        }
    }

}