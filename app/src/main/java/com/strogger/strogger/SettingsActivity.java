package com.strogger.strogger;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends AccountActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView mTitle =  findViewById(R.id.toolbar_title);
        mTitle.setText("Settings");

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.settings, null, false);
        super.dl.addView(contentView, 0);
    }
}
