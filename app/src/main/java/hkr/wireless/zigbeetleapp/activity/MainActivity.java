package hkr.wireless.zigbeetleapp.activity;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;


public class MainActivity extends AppCompatActivity {
    LinearLayout toBluetooth, toSettings;
    BluetoothDevice dev;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Data.getInstance(this).clearLogs();
        toBluetooth = findViewById(R.id.toBluetooth);
        toSettings = findViewById(R.id.toSettings);
        dev = Bluetooth_Discovery_Activity.pairedDevice;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toBluetooth.setOnClickListener(View -> startActivity(new Intent(this, Bluetooth_Discovery_Activity.class)));
        }
        toSettings.setOnClickListener(View -> startActivity(new Intent(this, Settings_Activity.class)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }


    }





}