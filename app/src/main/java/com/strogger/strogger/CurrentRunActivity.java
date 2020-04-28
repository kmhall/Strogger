package com.strogger.strogger;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;

import static com.strogger.strogger.GlobalVariables.audioPopupSwitch;
import static com.strogger.strogger.GlobalVariables.bluetoothPopupSwitch;

public class CurrentRunActivity extends AppCompatActivity implements SensorEventListener {
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
    int timerCount = 0;
    int accelMax = 1*100;
    int accelMin = 0;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorGyroscope;
    private long lastUpdate = 0;
    private double last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    double acceleration=0;
    Vibrator v;
    AlertDialog alertDialog;
    Deque<Double> movingAverage = new ArrayDeque<>();
    int averageBox = 10; //decent #, -> 100 for longer feedback gaps.

    //Set everything up with formatting, variables, etc.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_run);
        Log.d("Karson", "onCreate");

        readings = new ArrayList();

        mChart = (LineChart) findViewById(R.id.linechart);
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("");

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setPinchZoom(true);
        YAxis leftAxis = mChart.getAxisLeft();

        leftAxis.setTextColor(0XFFFFFFFF);
        leftAxis.setAxisMaximum(accelMax);
        leftAxis.setAxisMinimum(accelMin);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setTextColor(0XFFFFFFFF);

        XAxis x1 = mChart.getXAxis();
        x1.setAvoidFirstLastClipping(true);
        x1.setEnabled(true);
        x1.setTextColor(0XFFFFFFFF);
        LineData data = new LineData();

        mChart.setData(data);

        startDate = new Date();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE );
        assert v != null;

        AlertDialog.Builder builder = new AlertDialog.Builder(CurrentRunActivity.this);
        builder.setCancelable(true);
        builder.setTitle("Injury Warning!");
        builder.setMessage("Minimize rotation of your upper bodies. Try to keep the arms in a forwards-backwards motion");
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog = builder.create();

        FloatingActionButton mChronometerButton = findViewById(R.id.chronometer_button);
        mChronometerButton.setImageResource(R.drawable.ic_pause);
        mChronometerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeChronometer();
            }
        });

        //Save everything to firebase on end run
        Button mRunButton = findViewById(R.id.end_run_button);
        mRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plotData = false;
                writeToFirebase();
            }
        });

    }

    //Start timer
    @Override
    public void onStart() {
        super.onStart();
        Log.d("Karson", "onStart");

        chronometer = findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                timerCount++;
            }
        });
        chronometer.start();
        running = true;

        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //App actually running
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Karson", "onResume");

        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorGyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        //sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    if (plotData){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("Karson", "too deep");

                                //store and incremento
                                DeviceReading reading = new DeviceReading(acceleration,count);
                                readings.add(reading);
                                addEntry((int)(acceleration*100));
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

    //When play/pause pressed stop time and change icon symbol
    public void changeChronometer() {
        Log.d("Karson", "changeChronometer");
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

    //Called on when END RUN. write data to firebase
    public void writeToFirebase() {
        Log.d("Karson", "writeToFirebase");
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

    //push a reading to local storage
    private void addEntry(int reading){
        Log.d("Karson", "addEntry");
        LineData data = mChart.getData();

        if(data != null){
            ILineDataSet set = data.getDataSetByIndex(0);

            if(set==null){
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry( new Entry(set.getEntryCount(), reading), 0);
            data.notifyDataChanged();

            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(40);
            mChart.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet(){
        Log.d("Karson", "createSet");
        LineDataSet set = new LineDataSet(null, "GRF Readings");
        set.setValueTextColor(0X00000000);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(1f);
        set.setColor(0XFF44B84B);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        Log.d("KARSON", "onSensorChanged");
        count++;

        if(mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            double xa = event.values[0];
            double ya = event.values[1];
            double za = event.values[2];

            Log.d("KARSON", "xa: " + String.valueOf(xa));
            //Log.d("KARSON", String.valueOf(ya));
            //Log.d("KARSON", String.valueOf(za));

            //acceleration = Math.hypot(Math.hypot(xa, ya), za);

            last_x = xa;
            last_y = ya;
            last_z = za;
        }
        else if(mySensor.getType() == Sensor.TYPE_GYROSCOPE) {
            double xg = event.values[0];
            double yg = event.values[1];
            double zg = event.values[2];

            double ratOr = Math.abs(xg / Math.hypot(Math.hypot(xg,yg), zg));
            double rat = Math.abs(xg) / (Math.abs(xg) + Math.abs(yg) + Math.abs(zg));

            Log.d("ratio", "ratav: " + String.valueOf(rat));
            Log.d("ratio", "ratsq: " + String.valueOf(ratOr));

            acceleration = rat;
            movingAverage.addFirst(rat);
            if(averageBox<movingAverage.size()) {
                movingAverage.removeLast();
                double average = 0;
                Iterator myIt = movingAverage.iterator();
                int deb=0;
                while (myIt.hasNext()) {
                    //Log.d("Average", String.valueOf(deb));
                    average += (double) myIt.next();
                    deb++;
                }
                average = average / averageBox;
                Log.d("Average", "avg: " + average);
                movingAverage.clear();

                if (0.5 < average) {
                    Log.d("Karson", "Gyro threshold");

                    if (audioPopupSwitch) {
                        Log.d("Karson", "Audio warning");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(500,
                                    VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(500);
                        }
                    }

                    if (!alertDialog.isShowing() & bluetoothPopupSwitch) {
                        alertDialog.show();
                    }
                }
            }
        }
    }

    //Needs to be implemented, but doesn't need to do anything
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Put nothing here
    }

}
