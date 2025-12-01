package com.example.cypher_events.ui.admin;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.cypher_events.R;

import java.util.ArrayList;

public class AdminImagesListAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<AdminManageImagesFragment.EventImage> images;
    private int selectedPosition = -1;

    public AdminImagesListAdapter(Context context,
                                  ArrayList<AdminManageImagesFragment.EventImage> images) {
        this.context = context;
        this.images = images;
    }

    public void setSelected(int pos) {
        selectedPosition = pos;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        if (row == null) {
            row = LayoutInflater.from(context)
                    .inflate(R.layout.item_admin_image, parent, false);
        }

        ImageView imageView = row.findViewById(R.id.imageViewAdmin);
        View overlay = row.findViewById(R.id.selectedOverlay);
        ImageView cross = row.findViewById(R.id.imgCross);

        // Load bitmap
        Bitmap bitmap = images.get(position).bitmap;
        imageView.setImageBitmap(bitmap);

        // Show selected UI
        if (position == selectedPosition) {
            overlay.setVisibility(View.VISIBLE);
            cross.setVisibility(View.VISIBLE);
        } else {
            overlay.setVisibility(View.GONE);
            cross.setVisibility(View.GONE);
        }

        return row;
    }
}
