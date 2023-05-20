package hkr.wireless.zigbeetleapp.activity;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.bluetooth.BluetoothAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.MyLog;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Sensor;
import hkr.wireless.zigbeetleapp.utils.SetMainActivityStatus;
import hkr.wireless.zigbeetleapp.utils.Common;
import hkr.wireless.zigbeetleapp.adapters.SensorAdapter;
import hkr.wireless.zigbeetleapp.zigbee.RxPacket;
import hkr.wireless.zigbeetleapp.zigbee.ZigbeePacket;


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
    private final Handler BtCommunication = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String log = null;

            if (msg.what == Constants.INCOMING_DATA) {
                RxPacket rxPacket = ZigbeePacket.parse((byte[]) msg.obj);
                int i = findSensorByMac(rxPacket.getSourceMac());

                if(Arrays.equals(sensors.get(i).getMac(), Constants.TEMPERATURE_SENSOR_MAC)){
                    String temperature = rxPacket.getRfData().replace(sensors.get(i).getName() + " ", "");
                    sensors.get(i).setParameter(temperature);
                    log = "Temperature is at " + temperature;
                    sensors.get(i).setStatus(Sensor.ON);

                }else{
                    boolean status = (rxPacket.getRfData().contains("ON"));
                    sensors.get(i).setStatus(status ? Sensor.ON : Sensor.OFF);
                    log = String.format("%s is %s", sensors.get(i).getName(), (status)? "ON" : "OFF");
                }

                sensorAdapter.clear();
                sensorAdapter.addAll(sensors);
                sensorAdapter.notifyDataSetChanged();

            }else if (msg.what == Constants.WRITE_MESSAGE){
                Sensor sensor = (Sensor) msg.obj;
                int arg = msg.arg1; // ON or OFF.
                ZigbeePacket zigbeePacket;
                String state = (arg == Sensor.ON)? "ON" : "OFF";
                log = String.format("Set %s to %s", sensor.getName(), state);
                zigbeePacket = new ZigbeePacket(String.format("%s %s", sensor.getName(), state), sensor.getPanID(), sensor.getMac());
                bluetoothService.send(zigbeePacket.getBytes());

                // Create a ZIGBEE PACKET HERE.

            }

            if(log != null){
                Common.addLog(data, new MyLog(log));
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
        sensorAdapter = new SensorAdapter(this, R.layout.sensor_item, sensors, BtCommunication);

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
        bluetoothService.setHandler(BtCommunication);

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
                new Sensor("Temperature", Sensor.OFF, Constants.PAN_ID, Constants.TEMPERATURE_SENSOR_MAC, "Temperature: "),
                new Sensor("Fan", Sensor.OFF, Constants.PAN_ID, Constants.FAN_SENSOR_MAC),
                new Sensor("Heater", Sensor.OFF, Constants.PAN_ID, Constants.HEATER_SENSOR_MAC)
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
    public int findSensorByMac(byte[] mac){
        for (int i = 0; i < sensors.size(); i++) {
            if(Arrays.equals(sensors.get(i).getMac(), mac)){
                return i;
            }
        }

        return 0;
    }

}