package hkr.wireless.zigbeetleapp.activity;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import hkr.wireless.zigbeetleapp.BluetoothLeService;
import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Sensor;
import hkr.wireless.zigbeetleapp.utils.SetMainActivityStatus;
import hkr.wireless.zigbeetleapp.utils.ToActivity;
import hkr.wireless.zigbeetleapp.utils.Common;
import hkr.wireless.zigbeetleapp.adapters.SensorAdapter;


public class MainActivity extends AppCompatActivity implements ToActivity {
    private LinearLayout toBluetooth, toSettings;
    private BluetoothService bluetoothService;
    private ListView listView;
    private ArrayList<Sensor> sensors;
    private SensorAdapter sensorAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice previousConnectedDevice;
    private TextView connectedDevice;
    private Data data;
    private SetMainActivityStatus setMainActivityStatus;
    BluetoothLeService bluetoothLeService;
    private final ArrayList<String> gattUpdateReceiverActions = new ArrayList<>(Arrays.asList(
            Constants.FOUND_SERVICES,
            Constants.CONNECTED,
            Constants.DATA_SENT,
            Constants.INCOMING_DATA,
            Constants.ERROR_DATA_TRANSFER,
            Constants.GATT_CONNECTED,
            Constants.ERROR_DATA_RECEIVE,
            Constants.DISCONNECTING,
            Constants.CONNECTING,
            Constants.SENDING

    ));


    @RequiresApi(api = Build.VERSION_CODES.S)
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();


            if (Constants.CONNECTED.equals(action)) {


            } else if (Constants.DISCONNECTED.equals(action)) {


            } else if (Constants.FOUND_SERVICES.equals(action)) {


            }else if(Constants.INCOMING_DATA.equals(action)){
                byte[] value = bluetoothLeService.getIncomingData();


            }else if(Constants.DATA_SENT.equals(action)){


            }else if(Constants.ERROR_DATA_TRANSFER.equals(action)){


            }else if(Constants.ERROR_DATA_RECEIVE.equals(action)){


            }

            connectedDevice.setText(action);
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
        listView = findViewById(R.id.sensors_listview);
        toSettings = findViewById(R.id.toSettings);
        connectedDevice = findViewById(R.id.connected_device);
        toBluetooth = findViewById(R.id.toBluetooth);
        listView = findViewById(R.id.sensors_listview);
        toSettings = findViewById(R.id.toSettings);
        connectedDevice = findViewById(R.id.connected_device);


        /*
            -----------------------OBJECTS---------------------
         */
        data = Data.getInstance(this);
        sensors = (data.getSensors() != null && !data.getSensors().isEmpty())? data.getSensors() : createSensors();
        bluetoothService = BluetoothService.getInstance(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setMainActivityStatus = new SetMainActivityStatus(connectedDevice, bluetoothService, this);
        sensors = (data.getSensors() != null && !data.getSensors().isEmpty())? data.getSensors() : createSensors();


        /*
            -----------------------TESTS---------------------
         */
        bluetoothLeService = BluetoothLeService.getInstance(this);
        String e = "ED:ED:2C:B8:9A:94";
        bluetoothLeService.connectGatt(e);
        bluetoothService = BluetoothService.getInstance(this);
        setActions();



        toSettings.setOnClickListener(View -> Common.startActivity(this, Settings_Activity.class));
        toBluetooth.setOnClickListener(View -> Common.startActivity(this, Bluetooth_Discovery_Activity.class));

//        if (!bluetoothService.isConnected() && data.getMac() != null && !data.getMac().equals("")) {
//
//            if (!bluetoothAdapter.isEnabled()) {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
//                }
//                bluetoothAdapter.enable();
//            }
//
//            previousConnectedDevice = bluetoothAdapter.getRemoteDevice(data.getMac());
//            connectToPreviousConnectedDevice();
//        }


    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, Constants.REQUEST_ENABLE_BT);
        }


        sensorAdapter = new SensorAdapter(this, bluetoothService, R.layout.sensor_item, sensors);
        listView.setAdapter(sensorAdapter);


    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onResume() {
        super.onResume();


//        if (!setMainActivityStatus.isAlive()) {
//            setMainActivityStatus.start();
//        }


    }


    @Override
    public void onStop() {
        super.onStop();
        //data.storeSensors(sensors);
        setMainActivityStatus.setStart(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            unregisterReceiver(gattUpdateReceiver);
        }
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
            Toast.makeText(this, "MAC ADDRESS IS ILLEGAL", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Here changes sensor data.
     * @param data sent from BluetoothService or data from InputStream.
     */
    @Override
    public void sentData(byte[]  data) {
        Handler handler =  new Handler(Looper.getMainLooper());
        handler.post(() ->{


        });
    }


    public void setActions(){
        final IntentFilter intentFilter = new IntentFilter();
        gattUpdateReceiverActions.forEach(intentFilter::addAction);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            registerReceiver(gattUpdateReceiver, intentFilter);
        }
    }
}