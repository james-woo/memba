package com.james.memba.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.model.Entry;
import com.james.memba.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntryAdapter extends BaseAdapter {

    private List<Entry> mEntries;
    private Context mContext;

    public EntryAdapter(List<Entry> entries, Context context) {
        setList(entries);
        mContext = context;
    }

    public void replaceData(List<Entry> entries) {
        setList(entries);
        notifyDataSetChanged();
    }

    private void setList(List<Entry> entries) {
        mEntries = checkNotNull(entries);
    }

    @Override
    public int getCount() {
        return mEntries.size();
    }

    @Override
    public Entry getItem(int i) {
        return mEntries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View rowView = view;
        LayoutInflater inflater;
        if (rowView == null) {
            inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.entry_item, viewGroup, false);

            final Entry e = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(e.getTitle());

            TextView dateTV = (TextView) rowView.findViewById(R.id.date);
            dateTV.setText(DateUtil.longToDate(Long.parseLong(e.getDate())));

            ImageView image = (ImageView) rowView.findViewById(R.id.image);
            Picasso.with(mContext).load(e.getImage()).into(image);

            TextView textTV = (TextView) rowView.findViewById(R.id.text);
            textTV.setText(e.getText());
        }

        return rowView;
    }
}
