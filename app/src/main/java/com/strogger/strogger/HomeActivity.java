package com.strogger.strogger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AccountActivity implements View.OnClickListener{

    private Button newRunButton;

    private BluetoothAdapter bluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 777;
    private TextView BLE_Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView mTitle =  findViewById(R.id.toolbar_title);
        mTitle.setText("Home");

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.home, null, false);
        super.dl.addView(contentView, 0);

        newRunButton = findViewById(R.id.new_run_button);
        newRunButton.setOnClickListener(this);

        BLE_Message = findViewById(R.id.BLE_Message);
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            BLE_Message.setText("Device does not support Bluetooth LE connections");
            BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            BLE_Message.setText("");
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            BLE_Message.setText("Bluetooth is turned off");
            BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            BLE_Message.setText("Enabled");
            BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }
   }


    @Override
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.new_run_button:
                startActivity(new Intent(HomeActivity.this, CurrentRunActivity.class));
        }
    }


}
