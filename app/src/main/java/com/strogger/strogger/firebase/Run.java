package com.strogger.strogger.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Run {

    public String startTime;
    public Date date;
    public String duration;

    public Run() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Run(String startTime, Date date, String duration) {
        this.startTime = startTime;
        this.date = date;
        this.duration = duration;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}