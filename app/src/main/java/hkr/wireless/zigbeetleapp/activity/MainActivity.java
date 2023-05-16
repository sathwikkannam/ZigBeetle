package hkr.wireless.zigbeetleapp.activity;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Sensor;
import hkr.wireless.zigbeetleapp.utils.SetMainActivityStatus;
import hkr.wireless.zigbeetleapp.utils.Common;
import hkr.wireless.zigbeetleapp.adapters.SensorAdapter;


public class MainActivity extends AppCompatActivity{
    private LinearLayout toBluetooth, toSettings;
    private BluetoothService bluetoothService;
    private ListView sensorsListView;
    private ArrayList<Sensor> sensors;
    private SensorAdapter sensorAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice previousConnectedDevice;
    private TextView status;
    private Data data;
    private SetMainActivityStatus setMainActivityStatus;
    private Activity activity;
    private final ArrayList<String> broadcastReceiverActions = new ArrayList<>(Arrays.asList(
            Constants.INCOMING_DATA
    ));

    private final BroadcastReceiver broadcastReceiver =  new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals(Constants.INCOMING_DATA)){
                byte[] data = intent.getByteArrayExtra(Constants.DATA);
                Toast.makeText(activity, "Test", Toast.LENGTH_SHORT).show();
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));


        /*
            -----------------------VIEWS---------------------
         */
        toBluetooth = findViewById(R.id.toBluetooth);
        toSettings = findViewById(R.id.toSettings);
        sensorsListView = findViewById(R.id.sensors_listview);
        status = findViewById(R.id.connected_device);


        /*
            -----------------------OBJECTS---------------------
         */
        data = Data.getInstance(this);
        bluetoothService = BluetoothService.getInstance(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setMainActivityStatus = new SetMainActivityStatus(status, bluetoothService, this);
        sensors = (data.getSensors() != null && !data.getSensors().isEmpty())? data.getSensors() : createSensors();
        registerActions();


        toSettings.setOnClickListener(View -> Common.startActivity(this, Settings_Activity.class));
        toBluetooth.setOnClickListener(View -> Common.startActivity(this, Bluetooth_Discovery_Activity.class));

    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onStart() {
        super.onStart();

        Common.checkBluetoothPermission(this);

        sensorAdapter = new SensorAdapter(this, bluetoothService, R.layout.sensor_item, sensors);
        sensorsListView.setAdapter(sensorAdapter);

    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onResume() {
        super.onResume();

        bluetoothService.setActivity(this);

        if (!bluetoothService.isConnected() && data.getMac() != null && !data.getMac().equals("")) {

            if (!bluetoothAdapter.isEnabled()) {
                Common.checkBluetoothPermission(this);
                bluetoothAdapter.enable();
            }

            previousConnectedDevice = bluetoothAdapter.getRemoteDevice(data.getMac());
            connectToPreviousConnectedDevice();
        }


        if (!setMainActivityStatus.isAlive()) {
            setMainActivityStatus.start();
        }


    }


    @Override
    public void onStop() {
        super.onStop();
        data.storeSensors(sensors);
        setMainActivityStatus.setState(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothService.close();
        unregisterReceiver(broadcastReceiver);
    }


    /**
     * @return A list of sensor objects
     */
    public ArrayList<Sensor> createSensors() {
        return new ArrayList<>(Arrays.asList(
                new Sensor("Temperature", Sensor.OFF, Constants.TEMPERATURE_SENSOR_PAN_ID, "Temperature"),
                new Sensor("Fan", Sensor.OFF, Constants.FAN_SENSOR_PAN_ID),
                new Sensor("Heater", Sensor.OFF, Constants.HEATER_SENSOR_PAN_ID)
        ));

    }


    /**
     * Automatically connects to a device that was already connected to before.
     */
    public void connectToPreviousConnectedDevice(){

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if(previousConnectedDevice != null){
                    bluetoothService.connect(previousConnectedDevice);
                }
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }


    public void registerActions(){
        final IntentFilter intentFilter = new IntentFilter();
        broadcastReceiverActions.forEach(intentFilter::addAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }

}