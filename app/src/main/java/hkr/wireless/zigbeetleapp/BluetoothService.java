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
    public final int ALL_UUIDS = 0;
    private final ArrayList<UUID> deviceUUIDS;


    /**
     * Constructor
     * @param activity Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private BluetoothService(Activity activity) {
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
        this.log = MyLog.getInstance();
        this.data = Data.getInstance(activity);
        this.deviceUUIDS = this.getDeviceUUIDS();
    }


    /**
     * BluetoothService is a Singleton Object, so I can interact with the connected device from any
     * Activity.
     * @param activity Activity for sending Toasts.
     * @return BluetoothService object
     */
    public static BluetoothService getInstance(Activity activity){
        if(bluetoothService == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothService = new BluetoothService(activity);
            }
        }
        return bluetoothService;
    }


    /**
     * Creates Input and Output stream if the connection to the remote device is successful.
     */
    private void createStreams(){
        if(this.isConnected()){
            try {
                this.receivingStream = bluetoothSocket.getInputStream();
                this.sendingStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                sendToast("Error creating Streams");
            }
        }
    }


    /**
     * @param msg A send message to send to the remote device.
     */
    public void send(byte[] msg){
        if(this.sendingStream == null || !this.isConnected()){
            return;

        }

        Executors.newSingleThreadExecutor().execute(() ->{
            try {
                this.sendingStream.write(msg);
                log.add(String.format("%s %s %s %s", "Sent", new String(msg), "to", Utils.getName(this.bluetoothSocket.getRemoteDevice())));
                sendToast("Sent message");
            } catch (IOException e) {
                e.printStackTrace();
                sendToast("Error sending message");

            }

        });
    }


    /**
     * @return Received Data
     */
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
                sendToast("Error receiving data");
            }
        });

        return response;
    }


    /**
     * This attempts to connect to the remote bluetooth device with either only SPP profile or
     * try's all UUIDs until a connection is made.
     *
     * @param device A bluetoothDevice to connect to.
     * @param mode Either only SPP profile or all UUID's
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void connect(BluetoothDevice device, int mode){
        if(device == null){
            return;
        }

        close();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        adapter.cancelDiscovery();
        String name = Utils.getName(device);
        sendToast("Connecting to " + name);


        if(mode == this.SPP_MODE){
            try{
                this.connectSocket(device, this.SPP_PROFILE);
                sendToast("Connected to " + name);
                return;
            }catch (IOException e){
                e.printStackTrace();
                sendToast("Error connecting with SPP Profile");
            }
        }

        
        Executors.newSingleThreadExecutor().execute(() -> {
            for (UUID uuid : this.deviceUUIDS){
                try{
                    connectSocket(device, uuid);
                    break;
                }catch(IOException | NullPointerException f){
                    f.printStackTrace();
                }
            }

            if(this.isConnected()){
                this.sendToast("Connected to " + name);
                log.add(String.format("%s %s", "Connected to", name));
            }else{
                this.sendToast("Can't connect to " + name);
            }

        });

    }


    /**
     * @param device A bluetoothDevice to connect to.
     * @param uuid This is either device's UUID or SSP profile.
     * @throws IOException If the socket is not connected.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void connectSocket(BluetoothDevice device, UUID uuid) throws  IOException{
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        this.close();
        this.bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
        this.bluetoothSocket.connect();
        createStreams();

    }


    /**
     * @return If the bluetooth is connected.
     */
    public boolean isConnected(){
        return this.bluetoothSocket != null && this.bluetoothSocket.isConnected();
    }


    /**
     * @return The bluetooth socket's connected device.
     */
    public BluetoothDevice getRemoteDevice(){
        return (this.bluetoothSocket == null)? null : this.bluetoothSocket.getRemoteDevice();
    }


    /**
     * @return A list containing device's UUIDs.
     */
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
        uuids.add(this.SPP_PROFILE);

        return uuids;
    }


    /**
     * Closing the bluetooth socket.
     */
    public void close(){
        if(this.isConnected()){
            try{
                this.bluetoothSocket.close();
            }catch (IOException | NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a toast that is run on UI thread.
     * @param msg Message to toast.
     */
    private void sendToast(String msg){
        this.activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show());
    }


}
