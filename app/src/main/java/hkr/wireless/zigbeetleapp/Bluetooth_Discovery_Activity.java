package hkr.wireless.zigbeetleapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;


@RequiresApi(api = Build.VERSION_CODES.S)
public class Bluetooth_Discovery_Activity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayList<BluetoothDevice> devices;
    private BroadcastReceiver receiver;
    private final Handler handler = new Handler();
    private final int PERMISSION_REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discovery);

        listView = findViewById(R.id.BluetoothDevices);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_REQUEST_CODE);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_REQUEST_CODE);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_REQUEST_CODE);
        }


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Executors.newSingleThreadExecutor().execute(() -> {
                    devices = getDevices();

                    runOnUiThread(() ->{
                        ViewBluetoothAdapter bluetoothAdapter = new ViewBluetoothAdapter(getApplicationContext(), R.layout.item, devices);
                        listView.setAdapter(bluetoothAdapter);
                    });
                });
                handler.postDelayed(this, 1000);
            }
        }, 1000);  //the time is in miliseconds



    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    private ArrayList<BluetoothDevice> getDevices() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));

        }

        // PREVIOUS CONNECTED DEVICES.
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // NEW DEVICE
                    pairedDevices.add(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                }
            }
        };

        return new ArrayList<>(pairedDevices);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        receiver.abortBroadcast();
    }



}