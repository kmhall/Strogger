package com.strogger.strogger.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@IgnoreExtraProperties
public class Run {

    public String startTime;
    public String  elapsedTime;
    public double distanceMiles;


    public Run() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Run(String startTime, String elapsedTime, double distanceMiles) {
        this.startTime = startTime;
        this.elapsedTime = elapsedTime;
        this.distanceMiles = distanceMiles;
    }


    public String getStartTime() {
        return startTime;
    }
    public String  getElapsedTime() {
        return elapsedTime;
    }
    public double getDistanceMiles() {
        return distanceMiles;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public void setDistanceMiles(double distanceMiles) {
        this.distanceMiles = distanceMiles;
    }
}