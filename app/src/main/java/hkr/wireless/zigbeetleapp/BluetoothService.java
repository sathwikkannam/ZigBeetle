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
import java.util.UUID;
import java.util.concurrent.Executors;

import hkr.wireless.zigbeetleapp.log.MyLog;

public class BluetoothService{
    private final BluetoothAdapter adapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream receivingStream;
    private OutputStream sendingStream;
    private UUID uuid;
    private final UUID SPP_PROFILE_UUID  = UUID.fromString("0000111f-0000-1000-8000-00805f9b34fb");
    private final Activity activity;
    private final int REQUEST_ENABLE_BT = 1;
    private String name;
    private final MyLog log;
    public static BluetoothService bluetoothService;
    private final Data data;

    @RequiresApi(api = Build.VERSION_CODES.S)
    private BluetoothService(Activity activity) {
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
        this.log = MyLog.getInstance();
        this.data = Data.getInstance(activity);
    }


    public static BluetoothService getInstance(Activity activity){
        if(bluetoothService == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothService = new BluetoothService(activity);
            }
        }
        return bluetoothService;
    }


    private void createStreams(){
        if(this.bluetoothSocket.isConnected()){
            try {
                this.receivingStream = bluetoothSocket.getInputStream();
                this.sendingStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Error with getting streams", Toast.LENGTH_SHORT).show());
            }
        }
    }


    public void send(byte[] msg){
        if(this.sendingStream != null){
            Executors.newSingleThreadExecutor().execute(() ->{
                try {
                    this.sendingStream.write(msg);
                    log.add(String.format("%s %s %s %s", "Sent", new String(msg), "to", name));
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Sent message", Toast.LENGTH_SHORT).show());
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show());

                }

            });

        }
    }


    public byte[] read(){
        byte[] response = new byte[1024];

        if(this.receivingStream != null){

            Executors.newSingleThreadExecutor().execute(() ->{
                try {
                    this.receivingStream.read(response);

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


    @RequiresApi(api = Build.VERSION_CODES.S)
    public void connect(BluetoothDevice device){
        if(this.isConnected() && this.bluetoothSocket.getRemoteDevice() == device){
            return;
        }


        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        this.name = (device.getName() == null || device.getName().equals(""))? device.getAddress() : device.getName();


        adapter.cancelDiscovery();

        Executors.newSingleThreadExecutor().execute(() -> {
            try{
                UUID storedUUID = UUID.fromString(data.getUUID());
                connectSocket(device, storedUUID);
            }catch (IllegalArgumentException | NullPointerException | IOException e){
                tryUUIDS(device);
            }


            if(!this.isConnected()){
                activity.runOnUiThread(() -> Toast.makeText(activity, "Can't Connect to " + name, Toast.LENGTH_SHORT).show());
                return;
            }

            if(bluetoothSocket.isConnected()){
                //createStreams();
                if(this.uuid != null){
                    data.storeUUID(this.uuid.toString());
                }

                activity.runOnUiThread(() -> Toast.makeText(activity, "Connected to " + name, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void tryUUIDS(BluetoothDevice device){
        UUID[] uuids = getUUIDs();

        for (UUID uuid : uuids){
            try{
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)) {
                    connectSocket(device, uuid);
                }

                if(bluetoothSocket != null && !bluetoothSocket.isConnected()){
                    this.uuid = uuid; // NEW CHANGE
                    return;
                }
            }catch(IOException | NullPointerException e ){
                continue;
            }
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    private void connectSocket(BluetoothDevice device, UUID uuid) throws  IOException{
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        this.bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
        this.bluetoothSocket.connect();
        createStreams();

    }


    public boolean isConnected(){
        return (this.bluetoothSocket == null)? false : this.bluetoothSocket.isConnected();
    }


    private UUID[] getUUIDs() {
        ParcelUuid[] parcelUuids = new ParcelUuid[0];
        UUID[] uuids = new UUID[0];

        try {
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            parcelUuids = (ParcelUuid[]) getUuidsMethod.invoke(this.adapter, null);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (parcelUuids != null) {
            uuids = new UUID[parcelUuids.length + 1];
            uuids[0] = this.SPP_PROFILE_UUID;

            for (int i = 0; i < parcelUuids.length; i++) {
                uuids[i + 1] = parcelUuids[i].getUuid();
            }
        }

        return uuids;
    }


}
