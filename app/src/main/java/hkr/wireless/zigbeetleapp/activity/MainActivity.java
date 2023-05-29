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
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import hkr.wireless.zigbeetleapp.zigbee.ZigbeeConstants;
import hkr.wireless.zigbeetleapp.zigbee.ZigbeeFrame;


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
    private final ScheduledExecutorService temperatureThread = Executors.newScheduledThreadPool(5);

    // This handler controls sending and receiving data from and to the remote device.
    private final Handler bluetoothCommunication = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String log = null;

            if (msg.what == Constants.INCOMING_DATA) {
                byte[] data = (byte[]) msg.obj;
                int i;
                Log.d(Constants.TAG, "Raw RX packet: " + Common.byteToString(data));

                try{
                    if(ZigbeeFrame.getFrameTypeOf(data) != ZigbeeConstants.RX_RECEIVE_TYPE_16){
                        return;
                    }
                }catch (IndexOutOfBoundsException e){
                    return;
                }

                RxPacket rxPacket =  ZigbeeFrame.parseRxFrame(data);

                try{
                    i = findSensorByAddress(rxPacket.getSource16());
                }catch (IndexOutOfBoundsException e){
                    return;
                }


                if(Arrays.equals(sensors.get(i).getDestination16(), Constants.TEMPERATURE_DES_16)){
                    sensors.get(i).setParameterValue(rxPacket.getRfData());
                    sensors.get(i).setStatus(Sensor.ON);
                    log = "Temperature is at " + rxPacket.getRfData();

                }else if (rxPacket.getRfData().equalsIgnoreCase("On")){
                    sensors.get(i).setStatus(Sensor.ON);
                    log = String.format("%s is %s", sensors.get(i).getName(), "On");

                }else{
                    sensors.get(i).setStatus(Sensor.OFF);
                    log = String.format("%s is %s", sensors.get(i).getName(), "Off");
                }

                MainActivity.this.runOnUiThread(() -> {
                    sensorAdapter = new SensorAdapter(MainActivity.this, R.layout.sensor_item, sensors, bluetoothCommunication);
                    sensorsListView.setAdapter(sensorAdapter);
                });

            } else if (msg.what == Constants.WRITE_MESSAGE && bluetoothService.isConnected()){
                Sensor sensor = (Sensor) msg.obj;
                int arg = msg.arg1; // ON or OFF.
                String state = (arg == Sensor.ON)? "On" : "Off";
                byte[] frame = ZigbeeFrame.build(String.format("%s %s", sensor.getName(), state), sensor.getDestination64());

                bluetoothService.send(frame);
                Log.d(Constants.TAG, "Raw TX packet: " + Common.byteToString(frame));
                log = String.format("Request to turn %s %s", state, sensor.getName());

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
        bluetoothService = BluetoothService.getInstance(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setMainActivityStatus = new SetMainActivityStatus(status, bluetoothService, this);
        sensors = (data.getSensors() != null && !data.getSensors().isEmpty())? data.getSensors() : createSensors();
        sensorAdapter = new SensorAdapter(this, R.layout.sensor_item, sensors, bluetoothCommunication);

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
        bluetoothService.setHandler(bluetoothCommunication);

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


        // Polling the temperature every 1 minute.
        temperatureThread.scheduleAtFixedRate(() -> {
            byte[] temperatureFrame = ZigbeeFrame.build("Temperature", Constants.TEMPERATURE_DES_64);
            bluetoothService.send(temperatureFrame);
            Log.d(Constants.TAG, "Raw temperature TX packet: " + Common.byteToString(temperatureFrame));
            Common.addLog(data, new MyLog("Requesting temperature"));

        }, 0, Constants.TEMPERATURE_POLLING_DELAY, TimeUnit.MINUTES);



    }


    @Override
    public void onStop() {
        super.onStop();
        data.storeSensors(sensors);
        setMainActivityStatus.setThreadState(false);
        bluetoothService.setHandler(null);

        if(!temperatureThread.isShutdown()){
            temperatureThread.shutdown();
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
                new Sensor("Temperature", Sensor.OFF, Constants.TEMPERATURE_DES_16, Constants.TEMPERATURE_DES_64, "Temperature: "),
                new Sensor("Fan", Sensor.OFF, Constants.FAN_DES_16, Constants.FAN_DES_64),
                new Sensor("Heater", Sensor.OFF, Constants.HEATER_DES_16, Constants.HEATER_DES_64)
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
    public int findSensorByAddress(byte[] destination16) throws IndexOutOfBoundsException{
        for (int i = 0; i < sensors.size(); i++) {
            if(Arrays.equals(sensors.get(i).getDestination16(), destination16)){
                return i;
            }
        }
        throw new IndexOutOfBoundsException();
    }


}