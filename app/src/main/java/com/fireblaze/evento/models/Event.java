package com.fireblaze.evento.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class Event {
    public String eventID;
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

    public Event(String eventID, String name, String description, String category, int ratings, String image, String venue, String schedule, Map<String, Boolean> volunteers, double participationFees, double prizeAmount, String duration) {
        this.eventID = eventID;
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
}
