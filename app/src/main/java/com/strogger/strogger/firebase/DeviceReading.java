package com.strogger.strogger.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DeviceReading {

    double force;
    double time;

    public DeviceReading() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public DeviceReading(double force, double time) {
        this.force = force;
        this.time = time;
    }

    public double getForce() {
        return force;
    }
    public void setForce(long force) {
        this.force = force;
    }
    public double getTime() {
        return time;
    }
    public void setTime(double time) {
        this.time = time;
    }
}