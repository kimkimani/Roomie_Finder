package com.roomiegh.roomie.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.roomiegh.roomie.R;
import com.roomiegh.roomie.models.Hostel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KayO on 30/03/2017.
 */

public class HostelListAdapter extends BaseAdapter {
    private static final String TAG = "HostelListAdapter";
    private Context ctx;
    private ArrayList<Hostel> allHostels;
    private ImageView ivHostelThumbnail;
    //change tvHostelRating to later use a pictorial view with stars
    private TextView tvHostelListName, tvHostelListLocation, tvHostelListRating;

    public HostelListAdapter(Context ctx, ArrayList<Hostel> allHostels) {
        this.ctx = ctx;
        this.allHostels = allHostels;
    }

    @Override
    public int getCount() {
        return allHostels.size();
    }

    @Override
    public Object getItem(int position) {
        return allHostels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return allHostels.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Hostel hostel = (Hostel) getItem(position);
        Log.d(TAG, "getView: " + hostel.getName() + "// position: " + position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //convertView = inflater.inflate(R.layout.custom_hostel_row,null);
            convertView = inflater.inflate(R.layout.custom_hostel_row, parent, false);
            viewHolder.tvHostelName = (TextView) convertView.findViewById(R.id.tvHostelListName);
            viewHolder.tvHostelRating = (TextView) convertView.findViewById(R.id.tvHostelListRating);
            viewHolder.ivHostelThumb = (ImageView) convertView.findViewById(R.id.ivHostelThumbnail);
            viewHolder.rbHostelRating = (RatingBar) convertView.findViewById(R.id.rbHostelRating);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvHostelName.setText(hostel.getName());
        viewHolder.tvHostelRating.setText(String.valueOf(hostel.getRating()));
        if (hostel.getPhotopath() != null) {
            //there is an actual path
            if (!hostel.getPhotopath().equals("")){
                Picasso.with(ctx).load(hostel.getPhotopath())
                        .error(R.drawable.ic_home_black)
                        .placeholder(R.drawable.ic_home_black)
                        .into(viewHolder.ivHostelThumb);
                Log.d(TAG, "Photopath: "+hostel.getPhotopath());
            }else{
                viewHolder.ivHostelThumb.setImageResource(R.drawable.ic_home_black);
            }

        }
        viewHolder.rbHostelRating.setNumStars((int) hostel.getRating());

        return convertView;
    }

    public void setAllHostels(ArrayList<Hostel> allHostels) {
        this.allHostels = allHostels;
    }

    private class ViewHolder {
        ImageView ivHostelThumb;
        TextView tvHostelName, tvHostelRating;
        RatingBar rbHostelRating;
    }
}
