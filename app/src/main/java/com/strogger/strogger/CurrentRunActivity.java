package com.strogger.strogger;


import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.strogger.strogger.firebase.Run;

import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CurrentRunActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private boolean running;

    private DatabaseReference mDatabase;

    private long lastPause;

    private DateFormat dateFormat;
    Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        date = new Date();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        setContentView(R.layout.current_run);

        FloatingActionButton mChronometerButton = findViewById(R.id.chronometer_button);
        mChronometerButton.setImageResource(R.drawable.ic_pause);
        mChronometerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeChronometer();
            }
        });

        Button mRunButton = findViewById(R.id.end_run_button);
        mRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeToFirebase();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
        running = true;
    }

    public void changeChronometer() {
        FloatingActionButton mChronometerButton = findViewById(R.id.chronometer_button);
        if(!running){
            chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            chronometer.start();
            running = true;
            mChronometerButton.setImageResource(R.drawable.ic_pause);
        } else{
            lastPause = SystemClock.elapsedRealtime();
            chronometer.stop();
            running = false;
            mChronometerButton.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    public void writeToFirebase() {
        // refer to MyProfileActivity code for updating firebase
        final Chronometer chronometer = findViewById(R.id.chronometer);
        long millis = SystemClock.elapsedRealtime() - chronometer.getBase();
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

        Run run = new Run(dateFormat.format(date),hms,0);
        String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("runs").child(myUserId).push().setValue(run);

        startActivity(new Intent(CurrentRunActivity.this, HomeActivity.class));
    }

}
