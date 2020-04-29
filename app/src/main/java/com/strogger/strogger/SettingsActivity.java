package com.strogger.strogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import static com.strogger.strogger.GlobalVariables.audioPopupSwitch;
import static com.strogger.strogger.GlobalVariables.bluetoothPopupSwitch;
import static com.strogger.strogger.GlobalVariables.runningPopupSwitch;
import static com.strogger.strogger.GlobalVariables.heightValue;
import static com.strogger.strogger.GlobalVariables.weightValue;

public class SettingsActivity extends AccountActivity {

    private Switch bluetoothPopup;
    private Switch audioPopup;
    private Switch runningPopup;
    private EditText heightVal;
    private EditText weightVal;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);

        super.onCreate(savedInstanceState);
        final TextView mTitle =  findViewById(R.id.toolbar_title);
        mTitle.setText("Settings");

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.settings, null, false);
        super.dl.addView(contentView, 0);

        bluetoothPopup = findViewById(R.id.bluetoothPopup);
        bluetoothPopupSwitch = sharedpreferences.getBoolean("bluetoothPopupSwitch", true);
        bluetoothPopup.setChecked(bluetoothPopupSwitch);
        bluetoothPopup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bluetoothPopupSwitch = isChecked;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("bluetoothPopupSwitch", isChecked);
                editor.apply();
            }
        });

        audioPopup = findViewById(R.id.audioPopup);
        audioPopupSwitch = sharedpreferences.getBoolean("audioPopupSwitch", true);
        audioPopup.setChecked(audioPopupSwitch);
        audioPopup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                audioPopupSwitch = isChecked;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("audioPopupSwitch", isChecked);
                editor.apply();
            }
        });

        runningPopup = findViewById(R.id.runningPopup);
        runningPopupSwitch = sharedpreferences.getBoolean("runningPopupSwitch", true);
        runningPopup.setChecked(runningPopupSwitch);
        runningPopup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                runningPopupSwitch = isChecked;

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("runningPopupSwitch", isChecked);
                editor.apply();
            }
        });

        heightVal = findViewById(R.id.heightVal);
        heightValue = sharedpreferences.getInt("heightVal", -1);
        if(heightValue == -1) {
            heightVal.setText("");
        } else {
            heightVal.setText(Integer.toString(heightValue));
        }

        weightVal = findViewById(R.id.weightVal);
        weightValue = sharedpreferences.getInt("weightVal", -1);
        if(weightValue == -1) {
            weightVal.setText("");
        } else {
            weightVal.setText(Integer.toString(weightValue));
        }

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                Log.d("Karson", heightVal.getText().toString());
                if(heightVal.getText().toString().equals("")) {
                    heightValue = -1;
                } else {
                    heightValue = Integer.parseInt(heightVal.getText().toString());
                }

                if(weightVal.getText().toString().equals("")) {
                    weightValue = -1;
                } else {
                    weightValue = Integer.parseInt(weightVal.getText().toString());
                }

                editor.putInt("heightVal", heightValue);
                editor.putInt("weightVal", weightValue);
                editor.apply();
            }
        });


    }

}
