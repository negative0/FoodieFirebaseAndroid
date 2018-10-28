package com.fireblaze.foodiee.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.fireblaze.foodiee.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class FoodItem {
    private String eventID;
    private String organizerID;
    private String indexCategoryOrganizer;
    private String name;
    private String description;
    private String category;
    private int ratings;
    private String image;


    private double costPrice;

    //private CustomDate dateCreated;





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




    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
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
        result.put("costPrice", costPrice);
        return result;
    }

    public FoodItem(String eventID, String organizerID, String name, String description, String category, String image, double costPrice) {
        this.eventID = eventID;
        this.organizerID= organizerID;
        this.indexCategoryOrganizer = category+"+"+organizerID;
        this.name = name;
        this.description = description;
        this.category = category;
        this.image = image;

        this.costPrice = costPrice;
    }

    public FoodItem() {
        //Important
    }
//    public void booked(String UID){
//        //Change the booked count and add userID and bookingID into the db
//
//        if(bookings.containsKey(UID)){
//            BookedEvent.unBookEvent(bookings.get(UID));
//            bookingsCount -= 1;
//            bookings.remove(UID);
//        } else {
//            String bookingID = BookedEvent.bookEvent(eventID,UID,organizerID);
//            bookingsCount += 1;
//            bookings.put(UID,bookingID);
//        }
//
//    }
//
//    public void book(final String UID){
//        //Book the event
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Constants.FOOD_ITEM_KEYWORD)
//                .child(eventID);
//        ref.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                FoodItem e = mutableData.getValue(FoodItem.class);
//                if(e == null){
//                    return Transaction.success(mutableData);
//                }
//                e.booked(UID);
//                mutableData.setValue(e);
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//        });
//    }


    public static void deleteEvent(final String eventID){
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(Constants.FOOD_ITEM_KEYWORD)
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
                        Log.e("FoodItem", "onCancelled: ",databaseError.toException() );
                    }
                });


            }
        });
    }





    public String getIndexCategoryOrganizer() {
        return indexCategoryOrganizer;
    }

}
