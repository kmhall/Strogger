package com.strogger.strogger.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@IgnoreExtraProperties
public class Run {

    public String startTime;
    public String  elapsedTime;
    public double distanceMiles;

    public ArrayList<DeviceReading> readings;

    public Run() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Run(String startTime, String elapsedTime, double distanceMiles, ArrayList readings) {
        this.startTime = startTime;
        this.elapsedTime = elapsedTime;
        this.distanceMiles = distanceMiles;
        this.readings = readings;
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
    public ArrayList<DeviceReading> getReadings() {
        return readings;
    }

    public void setReadings(ArrayList<DeviceReading> readings) {
        this.readings = readings;
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