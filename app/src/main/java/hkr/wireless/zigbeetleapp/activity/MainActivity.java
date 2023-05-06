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
import android.widget.TextView;

import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.activity.Bluetooth_Discovery_Activity;
import hkr.wireless.zigbeetleapp.activity.Settings_Activity;


public class MainActivity extends AppCompatActivity {
    LinearLayout toBluetooth, toSettings;
    TextView connectedTo;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothDevice pairedDevice = getIntent().getExtras().getParcelable("device");

        toBluetooth = findViewById(R.id.toBluetooth);
        toSettings = findViewById(R.id.toSettings);
        connectedTo = findViewById(R.id.ConnectedTO);

        if (pairedDevice == null) {
            connectedTo.setText(String.format("%s: %s", "Connected to", "None"));

        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Bluetooth_Discovery_Activity.REQUEST_ENABLE_BT);
            }

            connectedTo.setText(String.format("%s: %s", "Connected to", pairedDevice.getName()));
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toBluetooth.setOnClickListener(View -> startActivity(new Intent(this, Bluetooth_Discovery_Activity.class)));
        }
        toSettings.setOnClickListener(View -> startActivity(new Intent(this, Settings_Activity.class)));




    }





}