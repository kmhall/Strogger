package com.strogger.strogger;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.strogger.strogger.firebase.Run;

import java.util.ArrayList;

import static com.strogger.strogger.GlobalVariables.bluetoothPopupSwitch;

//import android.support.annotation.Nullable;

public class HomeActivity extends AccountActivity implements View.OnClickListener{

    private Button newRunButton;
    ListView mListView;
    private DatabaseReference mDatabase;
    private String myUserId;

    ArrayList<String> runTimes = new ArrayList<>();
    ArrayList<String> runDates = new ArrayList<>();

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

        // Grab runs info //////////
        myUserId = super.mAuth.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("runs").child(myUserId);
        mListView = (ListView) findViewById(R.id.ListView);

        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, runTimes);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Run run = postSnapshot.getValue(Run.class);
                    runTimes.add(run.getElapsedTime());
                    runDates.add(run.getStartTime());
                }
                CustomAdaptor customAdaptor = new CustomAdaptor();
                mListView.setAdapter(customAdaptor);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
        // Grab runs info //////////

        ///Bluetooth Initialization///
        Log.d(tag, "Starting ble stuff");

        BLE_Message = findViewById(R.id.BLE_Message);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(tag, "bad device");
            BLE_Message.setText("Device doesn't support Bluetooth LE");
            BLE_Message.setTextSize(20);
            BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_red));
        } else {
            Log.d(tag, "good device");
            BLE_Message.setText("");

            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();

            SharedPreferences sharedpreferences = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
            bluetoothPopupSwitch = sharedpreferences.getBoolean("bluetoothPopupSwitch", true);

            if ((bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) && bluetoothPopupSwitch) {
                Log.d(tag, "blu off");
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            } else {
                BLE_Message.setText("Bluetooth Enabled");
                BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_grey));
            }

            IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mReceiver, filter);
            // PLACE OF COMMENTED CODE AT BOTTOM OF PAGE
        }
        ///Bluetooth Initialization///
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            BLE_Message.setText("Please Enable Bluetooth!");
            BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_red));
        } else {
            BLE_Message.setText("Bluetooth Enabled");
            BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_grey));
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
                        BLE_Message.setText("Please Enable Bluetooth!");
                        BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_red));
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        BLE_Message.setText("Turning Bluetooth off...");
                        BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_red));
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BLE_Message.setText("Bluetooth Enabled");
                        BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_grey));
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        BLE_Message.setText("Turning Bluetooth on...");
                        BLE_Message.setBackgroundColor(getResources().getColor(R.color.button_back_grey));
                        break;
                }
            }
        }
    };

    /* CODE WAS IN ONCREATE FUNCTION AT SPECIFIED LOCATION
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

    class CustomAdaptor extends BaseAdapter{

        @Override
        public int getCount() {
            return runDates.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View convertview, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View view = getLayoutInflater().inflate(R.layout.customlayout, null);

            TextView mTimeTextView = view.findViewById(R.id.runTime);
            TextView mDateTextView = view.findViewById(R.id.runDate);

            String date = runDates.get(i).substring(0,10);

            mTimeTextView.setText(runTimes.get(i));
            mDateTextView.setText(date);

            return view;
        }
    }
}