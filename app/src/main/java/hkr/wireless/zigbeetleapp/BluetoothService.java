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
import java.util.UUID;
import java.util.concurrent.Executors;

import hkr.wireless.zigbeetleapp.log.MyLog;

public class BluetoothService{
    private final BluetoothAdapter adapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream receivingStream;
    private OutputStream sendingStream;
    private final Activity activity;
    private final int REQUEST_ENABLE_BT = 1;
    private final MyLog log;
    public static BluetoothService bluetoothService;
    private final UUID SPP_PROFILE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final Data data;
    public final int SPP_MODE = 1;
    public final int NORMAL_MODE = 0;

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
        if(this.isConnected()){
            try {
                this.receivingStream = bluetoothSocket.getInputStream();
                this.sendingStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Error with getting streams", Toast.LENGTH_SHORT).show());
            }
        }
    }


    public void send(byte[] msg){
        if(this.sendingStream == null || !this.isConnected()){
            return;

        }

        Executors.newSingleThreadExecutor().execute(() ->{
            try {
                this.sendingStream.write(msg);
                log.add(String.format("%s %s %s %s", "Sent", new String(msg), "to", Utils.getName(this.bluetoothSocket.getRemoteDevice())));
                activity.runOnUiThread(() -> Toast.makeText(activity, "Sent message", Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> Toast.makeText(activity, "ERROR", Toast.LENGTH_SHORT).show());

            }

        });
    }


    public byte[] read(){
        if(this.receivingStream == null || !this.isConnected()){
            return null;
        }

        byte[] response = new byte[1024];

        Executors.newSingleThreadExecutor().execute(() ->{
            try {
                this.receivingStream.read(response);

                if(!new String(response).equals("")){
                    log.add(String.format("%s %s %s %s", "Received", new String(response), "from", Utils.getName(bluetoothSocket.getRemoteDevice())));
                }

            } catch (IOException e) {
                activity.runOnUiThread(() -> Toast.makeText(activity, "Error receiving data", Toast.LENGTH_SHORT).show());
            }
        });

        return response;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public void connect(BluetoothDevice device, int mode){
        if((this.isConnected() && this.bluetoothSocket.getRemoteDevice() == device) || device == null){
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        adapter.cancelDiscovery();


        if(mode == this.SPP_MODE){
            try{
                this.connectSocket(device, SPP_PROFILE);
                return;
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        ArrayList<UUID> uuids = this.getDeviceUUIDS();

        Executors.newSingleThreadExecutor().execute(() -> {
            for (UUID uuid : uuids){
                try{

                    connectSocket(device, uuid);

                }catch(IOException | NullPointerException f){
                    f.printStackTrace();
                }
            }


            if(!this.isConnected()){
                activity.runOnUiThread(() -> Toast.makeText(activity, "Can't Connect to " + Utils.getName(device), Toast.LENGTH_SHORT).show());
                return;
            }
            activity.runOnUiThread(() -> Toast.makeText(activity, "Connected to " + Utils.getName(device), Toast.LENGTH_SHORT).show());

        });
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

    public BluetoothDevice getRemoteDevice(){
        return (this.bluetoothSocket == null)? null : this.bluetoothSocket.getRemoteDevice();
    }


    private ArrayList<UUID> getDeviceUUIDS() {
        ParcelUuid[] parcelUuids = new ParcelUuid[0];
        ArrayList<UUID> uuids = new ArrayList<>();

        try {
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            parcelUuids = (ParcelUuid[]) getUuidsMethod.invoke(this.adapter, null);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (parcelUuids != null) {
            for (ParcelUuid parcelUuid : parcelUuids) {
                uuids.add(parcelUuid.getUuid());
            }
        }

        return uuids;
    }


    public void close(){
        try{
            bluetoothSocket.close();
        }catch (IOException | NullPointerException e){
            e.printStackTrace();
        }
    }


}
