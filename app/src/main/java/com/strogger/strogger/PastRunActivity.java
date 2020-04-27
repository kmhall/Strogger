package com.strogger.strogger;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PastRunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*ArrayList<Entry> yValues = new ArrayList<>();

        readings = new ArrayList();
        int[] measurements = {0,10,35,65,65,65,65,70,60,42};
        int value;
        //TEMPORARY USING TO POPULATE DATABASE WITH MOCK READINGS
        for(int i=0; i<10;i++){
            value = measurements[i];
            DeviceReading reading = new DeviceReading(value,i);
            readings.add(reading);
            yValues.add(new Entry(i,value));
        }

        LineDataSet set1 = new LineDataSet(yValues, "Newtons (N)");

        set1.setFillAlpha(110);
        set1.setLineWidth(3f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        */
    }
}
