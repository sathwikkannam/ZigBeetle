package hkr.wireless.zigbeetleapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;

public class bluetooth_discovery extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discovery);

        listView = findViewById(R.id.BluetoothDevices);

        test();
    }


    public void test() {
        BluetoothAdapter BA = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));


        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    pairedDevices.add(device);
                }
            }
        };


        ViewBluetoothAdapter bluetoothAdapter = new ViewBluetoothAdapter(this, R.layout.item, new ArrayList<>(pairedDevices));
        listView.setAdapter(bluetoothAdapter);



    }


    public void startBluetooth() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
        }


        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "Bluetooth permission not granted", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(enableBtIntent);
            Toast.makeText(this, "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
        }


    }


    public ArrayList<BluetoothDevice> startDiscovery() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }

        // Already paired devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // Discovered Devices.
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    pairedDevices.add(device);

                }
            }
        };


        return new ArrayList<>(pairedDevices);
    }








}