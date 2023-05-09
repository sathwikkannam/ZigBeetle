package hkr.wireless.zigbeetleapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executors;

import hkr.wireless.zigbeetleapp.log.MyLog;

public class BluetoothService {
    private final BluetoothAdapter adapter;
    private BluetoothSocket bluetoothSocket;
    private final BluetoothDevice bluetoothDevice;
    private InputStream receivingStream;
    private OutputStream sendingStream;
    // Standard SPP profile.
    private final UUID uuid = UUID.fromString("0000111f-0000-1000-8000-00805f9b34fb");
    private final String serviceName = "ZIGBEEAPP";
    private final Activity activity;
    private final int REQUEST_ENABLE_BT = 1;
    private final String name;
    private final MyLog log;
    public static BluetoothService bluetoothService;

    @RequiresApi(api = Build.VERSION_CODES.S)
    private BluetoothService(Activity activity, BluetoothDevice device) {
        this.bluetoothDevice = device;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
        this.log = MyLog.getInstance();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        this.name = (bluetoothDevice.getName() == null || bluetoothDevice.getName().equals(""))? bluetoothDevice.getAddress() : bluetoothDevice.getName();
    }


    public static BluetoothService getInstance(Activity activity, BluetoothDevice device){
        if(bluetoothService == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothService = new BluetoothService(activity, device);
            }
        }
        return bluetoothService;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public void connect() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }
        adapter.cancelDiscovery();

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                bluetoothSocket = this.bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                bluetoothSocket.connect();
                createStreams();

            } catch (
                    IOException e) {
                throw new RuntimeException(e);
            }

            activity.runOnUiThread(() ->{
                if(bluetoothSocket.isConnected()){
                    Toast.makeText(activity, "Connected to " + name, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "Error connecting to " + name, Toast.LENGTH_SHORT).show();
                }
            });

        });
    }

    private void createStreams(){
        if(bluetoothSocket.isConnected()){
            try {
                receivingStream = bluetoothSocket.getInputStream();
                sendingStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                activity.runOnUiThread(() ->{
                    Toast.makeText(activity, "Error with getting streams", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }


    public void send(byte[] msg){

        if(sendingStream != null){

            Executors.newSingleThreadExecutor().execute(() ->{
                try {
                    sendingStream.write(msg);
                    log.add(String.format("%s %s %s %s", "Sent", new String(msg), "to", name));

                } catch (IOException e) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Error sending message" + name, Toast.LENGTH_SHORT).show());

                }

            });

        }
    }


    public byte[] read(){
        byte[] response = new byte[1024];

        if(receivingStream != null){

            Executors.newSingleThreadExecutor().execute(() ->{
                try {
                    receivingStream.read(response);

                    if(!new String(response).equals("")){
                        log.add(String.format("%s %s %s %s", "Received", new String(response), "from", name));
                    }

                } catch (IOException e) {
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Error receiving data", Toast.LENGTH_SHORT).show());
                }
            });
        }

        return response;
    }


    private ArrayList<ParcelUuid> getUUIDs() {
        ParcelUuid[] uuids = new ParcelUuid[0];
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return (ArrayList<ParcelUuid>) Arrays.asList(uuids);
    }

}
