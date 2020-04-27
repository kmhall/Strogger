package com.strogger.strogger;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import static com.strogger.strogger.GlobalVariables.audioPopupSwitch;
import static com.strogger.strogger.GlobalVariables.bluetoothPopupSwitch;

public class SettingsActivity extends AccountActivity {

    private Switch bluetoothPopup;
    private Switch audioPopup;

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
        
    }
}
