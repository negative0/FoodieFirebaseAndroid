package com.fireblaze.evento.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fireblaze.evento.R;
import com.fireblaze.evento.activities.EventAttendeesListActivity;
import com.fireblaze.evento.activities.EventDetailsActivity;
import com.fireblaze.evento.activities.NewEventActivity;
import com.fireblaze.evento.activities.QRCodeScanActivity;
import com.fireblaze.evento.models.Event;

/**
 * Created by chait on 8/27/2016.
 */

public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
        MenuItem.OnMenuItemClickListener
{
    public static final String TAG = EventViewHolder.class.getSimpleName();
    private TextView title;
    private TextView subtitle;
    private ImageView imageView;
    private View itemView;
    private boolean isOrganizer;
    private Event myEvent;
    private Context mContext;
    public EventViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.event_image);
        title = (TextView) itemView.findViewById(R.id.text_title);
        subtitle = (TextView) itemView.findViewById(R.id.text_subtitle);
        this.itemView = itemView;
        isOrganizer = false;

    }

    public void bindToPost(final Context context, final Event event){
        title.setText(event.getName());
        Glide.with(context).load(event.getImage()).into(imageView);
        String subtitleString = event.getScheduleString();
        subtitle.setText(subtitleString);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDetailsActivity.navigate(context, event.getEventID());
            }
        });
        itemView.setTag(event.getEventID());
        if(isOrganizer){
            itemView.setOnCreateContextMenuListener(this);
        }
        myEvent = event;
        mContext = context;

    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        contextMenu.add(0,0,0,"Edit");
        contextMenu.add(1,1,1,"Delete");
        contextMenu.add(2,2,2,"View Attendees");
        contextMenu.add(3,3,3,"Scan QR Code");
        contextMenu.getItem(0).setOnMenuItemClickListener(this);
        contextMenu.getItem(1).setOnMenuItemClickListener(this);
        contextMenu.getItem(2).setOnMenuItemClickListener(this);
        contextMenu.getItem(3).setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                NewEventActivity.navigate(mContext,myEvent.getEventID());
                break;
            case 1:
                Event.deleteEvent(myEvent.getEventID());
                break;
            case 2:
                EventAttendeesListActivity.navigate(mContext,myEvent.getBookings(),myEvent.getPresentMap());
                break;
            case 3:
                QRCodeScanActivity.navigate(mContext,myEvent.getEventID());

                break;

        }
        return false;
    }
}
