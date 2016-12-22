package com.fireblaze.evento.models;

import com.fireblaze.evento.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fireblaze on 15/12/16.
 */

public class BookedEvent {
    public String eventID;
    public String userID;


    public BookedEvent(String eventID, String userID) {
        this.eventID = eventID;
        this.userID = userID;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventID",eventID);
        result.put("userID",userID);
        return result;
    }

    public BookedEvent(){
        //Important
    }

    public static String bookEvent(String eventID, String UID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.BOOKED_EVENTS);
        String bookingID = ref.push().getKey();
        ref.child(bookingID).setValue(new BookedEvent(eventID, UID));
        return bookingID;

    }

    public static void unBookEvent(String bookingID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.BOOKED_EVENTS);
        ref.child(bookingID).setValue(null);

    }


}
