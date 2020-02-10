package com.roomiegh.roomie.adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.roomiegh.roomie.R;
import com.roomiegh.roomie.models.FeedItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KayO on 16/08/2016.
 */
public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.MyViewHolder> {
    ArrayList<FeedItem>feedItems;
    Context context;

    public AdsAdapter(Context context, ArrayList<FeedItem>feedItems) {
        this.feedItems = feedItems;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.custom_row_ad_item,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        YoYo.with(Techniques.FadeIn).playOn(holder.cardView);
        final FeedItem currentItem = feedItems.get(position);
        holder.title.setText(currentItem.getTitle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            holder.description.setText(currentItem.getStringDescription());
        }else{
            holder.description.setText(currentItem.getDescription());
            holder.description.setMovementMethod(LinkMovementMethod.getInstance());
            holder.description.setLinksClickable(true);
        }
        holder.date.setText(currentItem.getPubDate());
        //Picasso.with(context).load(currentItem.getThumbnailUrl()).into(holder.thumbnail);
        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailIntent = new Intent(context, PostDetails.class);
                detailIntent.putExtra("link",currentItem.getLink());
                context.startActivity(detailIntent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return feedItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, date;
        ImageView thumbnail;
        CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_text);
            description = (TextView) itemView.findViewById(R.id.description_text);
            date = (TextView) itemView.findViewById(R.id.date_text);
            //thumbnail = (ImageView) itemView.findViewById(R.id.item_image);
            cardView = (CardView) itemView.findViewById(R.id.adsCardView);
        }
    }
}
