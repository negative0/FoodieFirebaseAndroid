package com.fireblaze.evento.models;

import com.fireblaze.evento.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;
import java.util.Map;


public class Event {
    public String eventID;
    public String organizerID;
    public String name;
    public String description;
    public String category;
    public int ratings;
    public String image;
    public String venue;
    public String schedule;
    public Map<String, Boolean> volunteers = new HashMap<>();
    public double participationFees;
    public double prizeAmount;
    //public Date dateCreated;
    public String duration;
    Map<String, String> bookings = new HashMap<>();
    public int bookingsCount;

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

    public Map<String, Boolean> getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(Map<String, Boolean> volunteers) {
        this.volunteers = volunteers;
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
        result.put("volunteers",volunteers);
        result.put("participationFees",participationFees);
        result.put("prizeAmount",prizeAmount);
        result.put("duration",duration);
        return result;
    }

    public Event(String eventID, String organizerID, String name, String description, String category, int ratings, String image, String venue, String schedule, Map<String, Boolean> volunteers, double participationFees, double prizeAmount, String duration) {
        this.eventID = eventID;
        this.organizerID= organizerID;
        this.name = name;
        this.description = description;
        this.category = category;
        this.ratings = ratings;
        this.image = image;
        this.venue = venue;
        this.schedule = schedule;
        this.volunteers = volunteers;
        this.participationFees = participationFees;
        this.prizeAmount = prizeAmount;
        this.duration = duration;
    }

    public Event() {
        //Important
    }
    public void booked(String UID){
        //Change the booked count and add userID and bookingID into the

        if(bookings.containsKey(UID)){
            BookedEvent.unBookEvent(bookings.get(UID));
            bookingsCount -= 1;
            bookings.remove(UID);
        } else {
            String bookingID = BookedEvent.bookEvent(eventID,UID);
            bookingsCount += 1;
            bookings.put(UID,bookingID);
        }

    }
    public void book(final String UID){
        //Book the event
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.EVENTS_KEYWORD)
                .child(organizerID)
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
}
