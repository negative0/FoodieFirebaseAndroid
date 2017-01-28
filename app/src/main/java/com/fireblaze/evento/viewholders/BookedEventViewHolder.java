package com.fireblaze.evento.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fireblaze.evento.R;
import com.fireblaze.evento.UserOperations;
import com.fireblaze.evento.activities.EventDetailsActivity;
import com.fireblaze.evento.models.BookedEvent;
import com.fireblaze.evento.models.Event;

/**
 * Created by fireblaze on 4/1/17.
 */

public class BookedEventViewHolder extends RecyclerView.ViewHolder {

    private TextView eventId, eventName;
    private Button btnCancel;
    private View rootView;
    public BookedEventViewHolder(View itemView) {
        super(itemView);
        eventId =  (TextView) itemView.findViewById(R.id.booked_event_id);
        eventName = (TextView) itemView.findViewById(R.id.booked_event_name);
        btnCancel = (Button) itemView.findViewById(R.id.btn_cancel);
        rootView = itemView;


    }
    public void bindToPost(final Context context, final BookedEvent bookedEvent, final Event event){
        eventId.setText(bookedEvent.getEventID());
        eventName.setText(event.getName());
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventDetailsActivity.navigate(context,bookedEvent.getEventID());
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.booked(UserOperations.getUid());
            }
        });

    }
}
