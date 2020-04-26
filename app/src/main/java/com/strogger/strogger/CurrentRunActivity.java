package com.strogger.strogger;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.strogger.strogger.firebase.DeviceReading;
import com.strogger.strogger.firebase.Run;

import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class CurrentRunActivity extends AppCompatActivity{
    private Chronometer chronometer;
    private boolean running;

    private DatabaseReference mDatabase;

    private long lastPause;

    private DateFormat dateFormat;
    Date startDate = new Date();

    ArrayList readings;
    int count = 0;

    private LineChart mChart;
    private boolean plotData = true;

    int value = 0;
    int lastValue;
    int lowerBound = 1250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_run);

        readings = new ArrayList();

        mChart = (LineChart) findViewById(R.id.linechart);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("GRF Readings");

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMaximum(1750);
        leftAxis.setAxisMinimum(0);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        XAxis x1 = mChart.getXAxis();
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(true);

        LineData data = new LineData();

        mChart.setData(data);

        startDate = new Date();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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
                plotData = false;
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

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (plotData){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int random;
                                if (value < lowerBound){
                                    lastValue = value;
                                    value += 75;
                                }
                                else if (value < 1500){
                                    if (lastValue < lowerBound){
                                        random = new Random().nextInt(100 - 25) + 25;
                                        value += random;
                                    } else {
                                        if (value < 1350){
                                            lowerBound = 1350;
                                        }
                                        random = new Random().nextInt(25) - 75;
                                        value += random;
                                    }
                                }
                                else {
                                    lastValue = value;
                                    random = new Random().nextInt(50);
                                    value = value - random;
                                }
                                DeviceReading reading = new DeviceReading(value,count);
                                readings.add(reading);
                                addEntry(value);
                                count++;
                            }
                        });
                    }
                    try {
                        Thread.sleep(60);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }).start();
    }
/*
    public void runGraph(){
        int value;
        while (plotData){
            value = new Random().nextInt(1750);
            DeviceReading reading = new DeviceReading(value,count);
            readings.add(reading);
            addEntry(value);
            count++;
        }
    }
*/
    public void changeChronometer() {
        FloatingActionButton mChronometerButton = findViewById(R.id.chronometer_button);
        if(!running){
            chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastPause);
            chronometer.start();
            running = true;
            plotData = true;
            mChronometerButton.setImageResource(R.drawable.ic_pause);
        } else{
            lastPause = SystemClock.elapsedRealtime();
            chronometer.stop();
            running = false;
            plotData = false;
            mChronometerButton.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    public void writeToFirebase() {
        final Chronometer c = findViewById(R.id.chronometer);

        long millis = SystemClock.elapsedRealtime() - c.getBase();
        Date result = new Date(millis - 64800000);
        SimpleDateFormat hmsFormat = new SimpleDateFormat("HH:mm:ss");
        String hms = hmsFormat.format(result);

        Run run = new Run(dateFormat.format(startDate),hms,0, readings);

        String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("runs").child(myUserId).push().setValue(run);

        startActivity(new Intent(CurrentRunActivity.this, HomeActivity.class));
    }

    private void addEntry(int reading){
        LineData data = mChart.getData();

        if(data != null){
            ILineDataSet set = data.getDataSetByIndex(0);

            if(set==null){
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry( new Entry(set.getEntryCount(), reading), 0);
            data.notifyDataChanged();

            if (value >= 1250){
                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setAxisMaximum(1600);
                leftAxis.setAxisMinimum(1250);
            }

            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(60);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet(){
        LineDataSet set = new LineDataSet(null, "Sensor Readings");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(3f);
        set.setColor(Color.MAGENTA);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


}
