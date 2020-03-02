package com.strogger.strogger;


import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class CurrentRunActivity extends AppCompatActivity {
    private Chronometer chronometer;
    private boolean running;

    private DatabaseReference mDatabase;

    private long lastPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_run);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        startActivity(new Intent(CurrentRunActivity.this, HomeActivity.class));
    }

}
