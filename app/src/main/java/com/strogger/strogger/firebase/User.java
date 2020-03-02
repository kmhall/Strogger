package com.strogger.strogger.firebase;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String first;
    public String last;
    public String phone;
    public String dob;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String first, String last, String phone, String dob) {
        this.first = first;
        this.last = last;
        this.phone = phone;
        this.dob = dob;
    }


    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public String getPhone() {
        return phone;
    }

    public String getDob() {
        return dob;
    }
}