package hkr.wireless.zigbeetleapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;

public class Settings_Activity extends AppCompatActivity {
    LinearLayout toHome, toBluetooth;
    TextView connectedTo, deviceMac;
    Data data;
    String pairedDevice, macAddress;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        data = Data.getInstance(getApplicationContext());

        pairedDevice = data.getPairedDeviceName();
        macAddress = data.getPairedDeviceMAC();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Bluetooth_Discovery_Activity.REQUEST_ENABLE_BT);
        }

        connectedTo.setText(String.format("%s: %s", "Connected to", (pairedDevice == null)? "None" : ((pairedDevice.equals("")? "Unknown Name" : pairedDevice))));
        deviceMac.setText(String.format("%s: %s", "MAC Address", (macAddress == null)? "" : macAddress));

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.platinum));

        toHome = findViewById(R.id.toHome);
        toBluetooth = findViewById(R.id.toBluetooth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toHome.setOnClickListener(View -> startActivity(new Intent(this, MainActivity.class)));
            toBluetooth.setOnClickListener(View -> startActivity(new Intent(this, Bluetooth_Discovery_Activity.class)));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove Paired device.
    }
}