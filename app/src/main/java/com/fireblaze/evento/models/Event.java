package com.fireblaze.evento.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fireblaze.evento.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class Event {
    private String eventID;
    private String organizerID;
    private String indexCategoryOrganizer;
    private String name;
    private String description;
    private String category;
    private int ratings;
    private String image;
    private String venue;
    private String schedule;

    private double participationFees;
    private double prizeAmount;
    //private CustomDate dateCreated;
    private String duration;
    private Map<String, String> bookings = new HashMap<>();
    private Map<String, Boolean> presentMap = new HashMap<>();
    private int presentCount=0;
    private int bookingsCount=0;
    private long dateScheduleStartTimestamp;

    public long getDateScheduleEndTimestamp() {
        return dateScheduleEndTimestamp;
    }

    public void setDateScheduleEndTimestamp(long dateScheduleEndTimestamp) {
        this.dateScheduleEndTimestamp = dateScheduleEndTimestamp;
    }

    private long dateScheduleEndTimestamp;
    private long dateCreatedTimestamp;

    public Map<String, Boolean> getPresentMap() {
        return presentMap;
    }

    public int getPresentCount() {
        return presentCount;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }



    public double getParticipationFees() {
        return participationFees;
    }

    public void setParticipationFees(double participationFees) {
        this.participationFees = participationFees;
    }

    public double getPrizeAmount() {
        return prizeAmount;
    }

    public void setPrizeAmount(double prizeAmount) {
        this.prizeAmount = prizeAmount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Map<String, String> getBookings() {
        return bookings;
    }

    public int getBookingsCount() {
        return bookingsCount;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    @Exclude
    public Map<String ,Object> toMap(){
        Map<String,Object> result = new HashMap<>();
        result.put("eventID",eventID);
        result.put("organizerID",organizerID);
        result.put("name",name);
        result.put("description",description);
        result.put("category",category);
        result.put("ratings",ratings);
        result.put("image",image);
        result.put("venue",venue);
        result.put("schedule",schedule);
        result.put("participationFees",participationFees);
        result.put("prizeAmount",prizeAmount);
        result.put("duration",duration);
        return result;
    }

    public Event(String eventID, String organizerID, String name, String description, String category, String image, String venue, String schedule, double participationFees, double prizeAmount, String duration) {
        this.eventID = eventID;
        this.organizerID= organizerID;
        this.indexCategoryOrganizer = category+"+"+organizerID;
        this.name = name;
        this.description = description;
        this.category = category;
        this.image = image;
        this.venue = venue;
        this.schedule = schedule;
        this.participationFees = participationFees;
        this.prizeAmount = prizeAmount;
        this.duration = duration;
        dateCreatedTimestamp = System.currentTimeMillis();
    }

    public Event() {
        //Important
    }
    public void booked(String UID){
        //Change the booked count and add userID and bookingID into the db

        if(bookings.containsKey(UID)){
            BookedEvent.unBookEvent(bookings.get(UID));
            bookingsCount -= 1;
            bookings.remove(UID);
        } else {
            String bookingID = BookedEvent.bookEvent(eventID,UID,organizerID);
            bookingsCount += 1;
            bookings.put(UID,bookingID);
        }

    }

    public void book(final String UID){
        //Book the event
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.EVENTS_KEYWORD)
                .child(eventID);
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if(e == null){
                    return Transaction.success(mutableData);
                }
                e.booked(UID);
                mutableData.setValue(e);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }


    public static void deleteEvent(final String eventID){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(Constants.EVENTS_KEYWORD)
                .child(eventID).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Query query = ref.child(Constants.BOOKED_EVENTS).orderByChild("eventID")
                        .equalTo(eventID);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            snapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Event", "onCancelled: ",databaseError.toException() );
                    }
                });


            }
        });
    }

    public long getDateScheduleStartTimestamp() {
        return dateScheduleStartTimestamp;
    }

    public void setDateScheduleStartTimestamp(long dateScheduleStartTimestamp) {
        this.dateScheduleStartTimestamp = dateScheduleStartTimestamp;
    }

    public long getDateCreatedTimestamp() {
        return dateCreatedTimestamp;
    }

    public void setDateCreatedTimestamp(long dateCreatedTimestamp) {
        this.dateCreatedTimestamp = dateCreatedTimestamp;
    }

    public String getIndexCategoryOrganizer() {
        return indexCategoryOrganizer;
    }

    @Exclude
    public String getCreatedDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy | HH:mm", Locale.ENGLISH);
        return simpleDateFormat.format(new Date(dateCreatedTimestamp));
    }

    @Exclude
    public void present(String uid){
        if(bookings.containsKey(uid)){
            if(!presentMap.containsKey(uid)){
                presentCount+=1;
                presentMap.put(uid,true);
            } else {
                presentCount -=1;
                presentMap.remove(uid);
            }
        }
    }
    @Exclude
    public static void markPresent(String eventID, final String uid){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(Constants.EVENTS_KEYWORD).child(eventID).runTransaction(
                new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Event event = mutableData.getValue(Event.class);
                        if(event == null){
                            return Transaction.success(mutableData);
                        }
                        event.present(uid);
                        mutableData.setValue(event);
                        return Transaction.success(mutableData);

                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                }
        );

    }
}
