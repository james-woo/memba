package com.james.memba.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.james.memba.R;
import com.james.memba.model.Berry;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
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
        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            rowView = inflater.inflate(R.layout.berry_item, viewGroup, false);
        }

        final Berry berry = getItem(i);

        TextView usernameTV = (TextView) rowView.findViewById(R.id.username);
        usernameTV.setText(berry.getUsername());

        TextView locationTV = (TextView) rowView.findViewById(R.id.location);
        locationTV.setText(berry.getLocation().toString());

        ImageView image = (ImageView) rowView.findViewById(R.id.image);
        Picasso.with(mContext).load(berry.getImage()).into(image);

        TextView descriptionTV = (TextView) rowView.findViewById(R.id.description);
        descriptionTV.setText(berry.getDescription());

        TextView dateTV = (TextView) rowView.findViewById(R.id.date);
        dateTV.setText(berry.getDate());

        return rowView;
    }


}