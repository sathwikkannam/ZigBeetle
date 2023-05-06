package hkr.wireless.zigbeetleapp.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread{

    private final BluetoothServerSocket mmServerSocket;
    private final BluetoothAdapter bluetoothAdapter;
    private final Activity activity;



    @RequiresApi(api = Build.VERSION_CODES.S)
    public AcceptThread(Activity activity) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;


        BluetoothServerSocket tmp = null;
        try {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
            }
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(Constants.NAME, Constants.MY_UUID);
        } catch (IOException e) {
            Toast.makeText(activity, "Socket's listen() method failed", Toast.LENGTH_SHORT).show();
        }
        mmServerSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket;
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Toast.makeText(activity, "Socket's accept() method failed", Toast.LENGTH_SHORT).show();
                break;
            }

            if (socket != null) {
                cancel();
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Toast.makeText(activity, "Could not close the connect socket", Toast.LENGTH_SHORT).show();

        }
    }
}
