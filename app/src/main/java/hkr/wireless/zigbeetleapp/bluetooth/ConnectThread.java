package hkr.wireless.zigbeetleapp.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.IOException;

public class ConnectThread extends Thread{

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter bluetoothAdapter;
    private final Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.S)
    public ConnectThread(BluetoothDevice device, Activity activity) {

        this.activity = activity;
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
            }
            tmp = device.createRfcommSocketToServiceRecord(Constants.MY_UUID);
        } catch (IOException e) {
            Toast.makeText(activity, "Socket's create() method failed", Toast.LENGTH_SHORT).show();
        }
        mmSocket = tmp;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void run() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, Constants.REQUEST_ENABLE_BT);
        }
        bluetoothAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            cancel();
        }

    }


    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Toast.makeText(activity, "Could not close the client socket", Toast.LENGTH_SHORT).show();
        }
    }
}
