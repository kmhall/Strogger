package com.strogger.strogger;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
//import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

<<<<<<< HEAD
import androidx.annotation.RequiresApi;
=======
import static com.strogger.strogger.SettingsActivity.bluetoothPopupEnable;
>>>>>>> 83b9fcb173b576552e3f9b65dd1dec7e418d0a8e

public class HomeActivity extends AccountActivity implements View.OnClickListener{

    private Button newRunButton;

    private BluetoothAdapter bluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 777;
    private TextView BLE_Message;
    private BluetoothDevice bluetoothDevice;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private final String tag = "Karson";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TextView mTitle = findViewById(R.id.toolbar_title);
        mTitle.setText("Home");

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.home, null, false);
        super.dl.addView(contentView, 0);

        newRunButton = findViewById(R.id.new_run_button);
        newRunButton.setOnClickListener(this);

        Log.d(tag, "Starting ble stuff");

        BLE_Message = findViewById(R.id.BLE_Message);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(tag, "bad device");
            BLE_Message.setText("Device does not support Bluetooth LE connections");
            BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            Log.d(tag, "good device");
            BLE_Message.setText("");

            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            //if ((bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) && bluetoothPopupEnable) {
            if ((bluetoothAdapter == null || !bluetoothAdapter.isEnabled())) {
            Log.d(tag, "blu off");
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            } else {
                BLE_Message.setText("ble already on");
                BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.white));
            }

            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
            /*
            if (!bluetoothAdapter.getBondedDevices().iterator().hasNext()) {
                Log.d(tag, "nonono");
                BLE_Message.setText("No paired devices");
                BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                Log.d(tag, "pair found");
                bluetoothDevice = bluetoothAdapter.getBondedDevices().iterator().next();
                Log.d(tag, "a");
                bluetoothGatt = bluetoothDevice.connectGatt(this, false, gattCallback);
                Log.d(tag, "b");
                bluetoothGatt.discoverServices();

                Log.d(tag, "serching");
                bluetoothGatt.getServices();
                Log.d(tag, Integer.toString(bluetoothGatt.getServices().size()));
                //bluetoothGatt.getServices().get(0);
                Log.d(tag, "x");
                //bluetoothGattCharacteristic = bluetoothGatt.getServices().get(0).getCharacteristics().get(0);
                Log.d(tag, "c");
                //bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic,true);
            }*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BLE_Message.setText(Integer.toString(characteristic.getIntValue(0,0)));
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        BLE_Message.setText("Bluetooth off");
                        BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_purple));
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        BLE_Message.setText("Turning Bluetooth off...");
                        BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BLE_Message.setText("Bluetooth on");
                        BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        BLE_Message.setText("Turning Bluetooth on...");
                        BLE_Message.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                        break;
                }
            }
        }
    };


}