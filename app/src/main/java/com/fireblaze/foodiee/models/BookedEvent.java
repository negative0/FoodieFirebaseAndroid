package com.fireblaze.foodiee.models;

import com.fireblaze.foodiee.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fireblaze on 15/12/16.
 */

public class BookedEvent {
    private String eventID;
    private String userID;
    private String organizerID;


    public BookedEvent(String eventID, String userID, String organizerID) {
        this.eventID = eventID;
        this.userID = userID;
        this.organizerID = organizerID;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("eventID",eventID);
        result.put("userID",userID);
        result.put("organizerID",organizerID);
        return result;
    }

    public BookedEvent(){
        //Important
    }

    public static String bookEvent(String eventID, String UID, String organizerID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.BOOKED_EVENTS);
        String bookingID = ref.push().getKey();
        ref.child(bookingID).setValue(new BookedEvent(eventID, UID, organizerID));
        return bookingID;

    }

    public static void unBookEvent(String bookingID){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.BOOKED_EVENTS);
        ref.child(bookingID).setValue(null);

    }

    public String getEventID() {
        return eventID;
    }
    public String getOrganizerID(){
        return organizerID;
    }
    public String getUserID(){
        return userID;
    }


}
