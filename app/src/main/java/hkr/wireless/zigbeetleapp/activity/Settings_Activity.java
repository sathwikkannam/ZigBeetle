package hkr.wireless.zigbeetleapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Utils;
import hkr.wireless.zigbeetleapp.adapters.LogsAdapter;
import hkr.wireless.zigbeetleapp.log.LogFormat;
import hkr.wireless.zigbeetleapp.log.MyLog;

public class Settings_Activity extends AppCompatActivity {
    private LinearLayout toHome, toBluetooth;
    private TextView connectedTo, deviceMac;
    private BluetoothDevice device;
    private ListView logsListView;
    public static LogsAdapter logsAdapter;
    private Data data;
    private ArrayList<LogFormat> storedLogs;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.platinum));

        connectedTo = findViewById(R.id.ConnectedTO);
        deviceMac = findViewById(R.id.MAC);
        toHome = findViewById(R.id.toHome);
        toBluetooth = findViewById(R.id.toBluetooth);
        logsListView = findViewById(R.id.logs);
        device = Bluetooth_Discovery_Activity.pairedDevice;
        data = Data.getInstance(this);
        storedLogs = data.getLogs();
        logsAdapter =  new LogsAdapter(this, R.layout.log_item, storedLogs);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Bluetooth_Discovery_Activity.REQUEST_ENABLE_BT);
        }

        if(device != null){
            connectedTo.setText(String.format("%s: %s", "Connected to", (device.getName().equals(""))? "Unknown" : device.getName()));
            deviceMac.setText(String.format("%s: %s", "MAC Address", device.getAddress()));
            connectedTo.setTextColor(ContextCompat.getColor(this, R.color.green));
            deviceMac.setTextColor(ContextCompat.getColor(this, R.color.green));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toHome.setOnClickListener(View -> startActivity(new Intent(this, MainActivity.class)));
            toBluetooth.setOnClickListener(View -> startActivity(new Intent(this, Bluetooth_Discovery_Activity.class)));
        }

        if(storedLogs != null){
            if(!storedLogs.isEmpty()){
                logsListView.setAdapter(logsAdapter);
                logsListView.setFocusable(storedLogs.size());
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.replaceLogs(data, storedLogs, MyLog.getInstance().getLogs());
    }
}