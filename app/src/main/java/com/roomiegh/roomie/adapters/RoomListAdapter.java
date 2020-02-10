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
import com.roomiegh.roomie.models.Room;
import com.roomiegh.roomie.models.Room;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by KayO on 20/05/2017.
 */

public class RoomListAdapter extends BaseAdapter{
    private static final String TAG = "RoomListAdapter";
    private Context ctx;
    private ArrayList<Room> allRooms;
    private ImageView ivRoomThumbnail;
    //change tvRoomRating to later use a pictorial view with stars
    private TextView tvRoomListName, tvRoomListLocation, tvRoomListRating;

    public RoomListAdapter(Context ctx, ArrayList<Room> allRooms) {
        this.ctx = ctx;
        this.allRooms = allRooms;
    }
    @Override
    public int getCount() {
        return allRooms.size();
    }

    @Override
    public Object getItem(int position) {
        return allRooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return allRooms.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Room room = (Room) getItem(position);
        Log.d(TAG, "getView: " + room.getRoomNum() + "// position: " + position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater =
                    (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //convertView = inflater.inflate(R.layout.custom_hostel_row,null);
            convertView = inflater.inflate(R.layout.custom_room_row, parent, false);
            viewHolder.ivRoomNumBkgrnd = (ImageView) convertView.findViewById(R.id.ivRoomNumBkgrnd);
            viewHolder.tvListRoomNum = (TextView) convertView.findViewById(R.id.tvListRoomNum);
            viewHolder.tvRoomListPrice = (TextView) convertView.findViewById(R.id.tvRoomListPrice);
            viewHolder.tvRoomListType = (TextView) convertView.findViewById(R.id.tvRoomListType);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvRoomListType.setText(room.getType()+"");
        viewHolder.tvRoomListPrice.setText(room.getPrice()+"");
        int[] shapeArray = {R.drawable.color_blue,R.drawable.color_gold,R.drawable.color_green,R.drawable.color_red,R.drawable.color_orange,R.drawable.color_violet};
        //color codes will match room type
        viewHolder.ivRoomNumBkgrnd.setImageResource(shapeArray[room.getType()-1]);
        viewHolder.tvListRoomNum.setText(room.getRoomNum());

        return convertView;
    }

    private class ViewHolder {
        ImageView ivRoomNumBkgrnd;
        TextView tvRoomListType, tvRoomListPrice, tvListRoomNum;
    }

    public void setAllRooms(ArrayList<Room> allRooms) {
        this.allRooms = allRooms;
    }
}
