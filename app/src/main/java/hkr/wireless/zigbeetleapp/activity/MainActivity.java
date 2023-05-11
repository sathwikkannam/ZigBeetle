package hkr.wireless.zigbeetleapp.activity;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Sensor;
import hkr.wireless.zigbeetleapp.Utils;
import hkr.wireless.zigbeetleapp.adapters.SensorAdapter;


public class MainActivity extends AppCompatActivity {
    private LinearLayout toBluetooth, toSettings;
    private BluetoothService bluetoothService;
    private ListView listView;
    private ArrayList<Sensor> sensors;
    private SensorAdapter sensorAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice zigbeeController;
    public static final String zigbeeControllerMac = Constants.zigbeeControllerMac;
    private TextView deviceConnectedTO;

    Data data;
    int REQUEST_ENABLE_BT = 1;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_BT);
        }


        data = Data.getInstance(this);
        bluetoothService = BluetoothService.getInstance(this);
        toBluetooth = findViewById(R.id.toBluetooth);
        listView = findViewById(R.id.sensors_listview);
        toSettings = findViewById(R.id.toSettings);
        deviceConnectedTO = findViewById(R.id.connected_device);
        sensors = createSensors();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Data.getInstance(this).clearLogs();
        sensorAdapter = new SensorAdapter(this, bluetoothService, R.layout.sensor_item, sensors);
        listView.setAdapter(sensorAdapter);


        toSettings.setOnClickListener(View -> Utils.startActivity(this, Settings_Activity.class));
        toBluetooth.setOnClickListener(View -> Utils.startActivity(this, Bluetooth_Discovery_Activity.class));

        if(!bluetoothService.isConnected() && zigbeeControllerMac != null){
            connectToZigbeeController();

        }else if(bluetoothService.isConnected()){
            deviceConnectedTO.setText(String.format("%s %s", "Connected to", Utils.getName(bluetoothService.getRemoteDevice())));
            deviceConnectedTO.setTextColor(ContextCompat.getColor(this, R.color.green));

        }

    }


    @Override
    public void onStop() {
        super.onStop();
        bluetoothService.close();
        //Utils.replaceLogs(data, myLog);

    }


    public ArrayList<Sensor> createSensors() {
        ArrayList<Sensor> sensors = new ArrayList<>();

        sensors.add(new Sensor("Temperature", Sensor.OFF));
        sensors.add(new Sensor("Fan", Sensor.OFF));
        sensors.add(new Sensor("Heater", Sensor.OFF));

        return sensors;

    }

    public void sendMessage(String msg) {
        bluetoothService.send(msg.getBytes());
    }


    public void connectToZigbeeController(){
        deviceConnectedTO.setText(R.string.connecting);
        deviceConnectedTO.setTextColor(ContextCompat.getColor(this, R.color.platinum));

        zigbeeController = bluetoothAdapter.getRemoteDevice(zigbeeControllerMac);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bluetoothService.connect(zigbeeController, bluetoothService.SPP_MODE);
        }

        if(bluetoothService.isConnected()){
            deviceConnectedTO.setText(String.format("%s %s", "Connected to", Utils.getName(bluetoothService.getRemoteDevice())));
            deviceConnectedTO.setTextColor(ContextCompat.getColor(this, R.color.green));
        }
    }




}