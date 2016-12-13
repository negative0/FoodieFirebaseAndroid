package com.fireblaze.evento.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fireblaze.evento.EventDetailsActivity;
import com.fireblaze.evento.R;
import com.fireblaze.evento.models.Event;

/**
 * Created by chait on 8/27/2016.
 */

public class EventViewHolder extends RecyclerView.ViewHolder {

    private TextView title;
    private TextView subtitle;
    private ImageView imageView;
    public EventViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.event_image);
        title = (TextView) itemView.findViewById(R.id.text_title);
        subtitle = (TextView) itemView.findViewById(R.id.text_subtitle);

    }
    public void bindToPost(final Context context, final Event event){
        title.setText(event.getName());
        Glide.with(context).load(event.getImage()).into(imageView);
        subtitle.setText(event.getDuration());
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDetailsActivity.navigate(context, event.getEventID());
            }
        });
    }
}
