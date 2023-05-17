package hkr.wireless.zigbeetleapp.activity;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Sensor;
import hkr.wireless.zigbeetleapp.utils.SetMainActivityStatus;
import hkr.wireless.zigbeetleapp.utils.Common;
import hkr.wireless.zigbeetleapp.adapters.SensorAdapter;


public class MainActivity extends AppCompatActivity {
    private LinearLayout toBluetooth, toSettings;
    private BluetoothService bluetoothService;
    private ListView sensorsListView;
    private ArrayList<Sensor> sensors;
    private SensorAdapter sensorAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private String previousConnectedDeviceMac;
    private TextView status;
    private Data data;
    private SetMainActivityStatus setMainActivityStatus;


    // This handler controls sending and receiving data from and to the remote device.
    private final Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == Constants.INCOMING_DATA) {
                byte[] incoming_data = (byte[]) msg.obj;

                //Common.addLog(data, new MyLog(String.format("")));
                //sensorAdapter.addAll(sensors);
                //sensorAdapter.notifyDataSetChanged();
            }else if (msg.what == Constants.WRITE_MESSAGE){
                Sensor sensor = (Sensor) msg.obj;
                int arg = msg.arg1; // ON or OFF.

                if(arg == Sensor.ON){

                }else{

                }
                // Create a ZIGBEE PACKET HERE.

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
        data.clearSensors();
        bluetoothService = BluetoothService.getInstance(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setMainActivityStatus = new SetMainActivityStatus(status, bluetoothService, this);
        sensors = (data.getSensors() != null && !data.getSensors().isEmpty())? data.getSensors() : createSensors();
        sensorAdapter = new SensorAdapter(this, R.layout.sensor_item, sensors, handler);

        toSettings.setOnClickListener(View -> Common.startActivity(this, Settings_Activity.class));
        toBluetooth.setOnClickListener(View -> Common.startActivity(this, Bluetooth_Discovery_Activity.class));
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onStart() {
        super.onStart();

        Common.checkBluetoothPermission(this);

        sensorsListView.setAdapter(sensorAdapter);

    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onResume() {
        super.onResume();

        bluetoothService.setActivity(this);
        bluetoothService.setHandler(handler);

        if (!bluetoothService.isConnected() && !data.getMac().isEmpty()) {

            if (!bluetoothAdapter.isEnabled()) {
                Common.checkBluetoothPermission(this);
                bluetoothAdapter.enable();
            }

            previousConnectedDeviceMac = data.getMac();
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
        setMainActivityStatus.setThreadState(false);
        bluetoothService.setHandler(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        bluetoothService.close();
    }


    /**
     * @return A list of sensor objects
     */
    public ArrayList<Sensor> createSensors() {
        return new ArrayList<>(Arrays.asList(
                new Sensor("Temperature", Sensor.OFF, Constants.TEMPERATURE_SENSOR_PAN_ID.getBytes(), "Temperature: "),
                new Sensor("Fan", Sensor.OFF, Constants.FAN_SENSOR_PAN_ID.getBytes()),
                new Sensor("Heater", Sensor.OFF, Constants.HEATER_SENSOR_PAN_ID.getBytes())
        ));

    }


    /**
     * Automatically connects to a device that was already connected to before.
     */
    public void connectToPreviousConnectedDevice(){

        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

                if(previousConnectedDeviceMac != null){
                    bluetoothService.connect(previousConnectedDeviceMac);
                }
            }
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }

    public int findSensorByName(String name){
        AtomicInteger i = new AtomicInteger();
        i.set(0);

        sensors.forEach(sensor ->{
            if(sensor.getName().equals(name)){
                i.set(sensors.indexOf(sensor));
            }
        });

        return i.get();
    }


}