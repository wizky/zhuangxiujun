package com.campingfun.vacancyhunter.campsitehunter;

/**
 * Created by wayliu on 9/1/2014.
 */

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

final class ImageGridViewAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> urls = new ArrayList<String>();

    public ImageGridViewAdapter(Context context, String[] imageUrls) {
        this.context = context;

        Collections.addAll(urls, imageUrls);
        // Ensure we get a different ordering of images on each run.
//        Collections.addAll(urls, imageUrls);
//        Collections.shuffle(urls);

        // Triple up the list.
//        ArrayList<String> copy = new ArrayList<String>(urls);
//        urls.addAll(copy);
//        urls.addAll(copy);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .placeholder(R.drawable.placeholder) //
                .error(R.drawable.error) //
                .fit() //
                .into(view);

        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}