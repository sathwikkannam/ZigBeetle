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
import android.widget.TextView;

import hkr.wireless.zigbeetleapp.R;

public class Settings_Activity extends AppCompatActivity {
    LinearLayout toHome, toBluetooth;
    TextView connectedTo, deviceMac;
    BluetoothDevice device;

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
        device = Bluetooth_Discovery_Activity.pairedDevice;

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

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}