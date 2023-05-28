package hkr.wireless.zigbeetleapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
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

import hkr.wireless.zigbeetleapp.activity.Bluetooth_Discovery_Activity;
import hkr.wireless.zigbeetleapp.utils.Common;

public class BluetoothService extends Thread {
    private final UUID SPP_PROFILE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream receivingStream;
    private OutputStream sendingStream;
    private Activity activity;
    public static BluetoothService bluetoothService;
    private final Data data;
    private final ArrayList<UUID> deviceUUIDS;
    private String status;
    private Handler handler;
    private byte[] response;


    /**
     * Constructor
     * @param activity Activity for sending Toasts.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private BluetoothService(Activity activity) {
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
        this.data = Data.getInstance(activity);
        this.deviceUUIDS = this.getDeviceUUIDS();
        this.deviceUUIDS.add(SPP_PROFILE);
        this.status = Constants.DISCONNECTED;
    }


    /**
     * BluetoothService is a Singleton Object, so I can interact with the connected device from any
     * Activity.
     * @param activity Activity for sending Toasts.
     * @return BluetoothService object
     */
    public static BluetoothService getInstance(Activity activity) {
        if (bluetoothService == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothService = new BluetoothService(activity);
            }
        }
        return bluetoothService;
    }


    /**
     * @param msg A message to send to the remote device.
     */
    public void send(byte[] msg) {
        if (!this.isConnected()) {
            Log.d(Constants.TAG, "Sending Stream in NULL");
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                this.sendingStream.flush();
                this.sendingStream.write(msg);
            } catch (IOException e) {
                e.printStackTrace();

            }

        });
    }


    /**
     * Constantly listens for incoming data in a separate thread.
     */
    @Override
    public void run(){
        this.response = new byte[Constants.ZIGBEE_PACKET_MTU];
        int sum = 0;
        while (isConnected() && handler != null){
            try {

                synchronized (this){
                    this.receivingStream.read(response);

                    if(!new String(response).isEmpty() && response[0] == 0x7E){
                        this.join();
                        handler.obtainMessage(Constants.INCOMING_DATA, this.response).sendToTarget();
                        response = new byte[Constants.ZIGBEE_PACKET_MTU];
                    }


                }

            } catch (IOException | InterruptedException e) {
                Toast.makeText(activity, "Error sending message", Toast.LENGTH_SHORT).show();
            }
        }

    }


    /**
     * This attempts to connect to the remote bluetooth device with either only SPP profile or
     * tries all UUIDs until a connection is made.
     *
     * @param mac Address of the remote device.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void connect(String mac) {
        BluetoothDevice device;

        if (mac == null) {
            return;
        }

        // Find device
        try{
            device = this.bluetoothAdapter.getRemoteDevice(mac);
        }catch (IllegalArgumentException e){
            this.status = Constants.NO_DEVICE_FOUND;
            return;
        }


        // If the selected device is already connected.
        if(this.isConnected() && this.getRemoteDevice().getAddress().equals(mac)){
            sendToast("Already connected to " + Common.getName(device));
            status = Constants.CONNECTED;
            return;
        }

        // Closes the bluetoothSocket if it is already connected.
        this.close();

        // Requires permission to cancel Discovery.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        // Discovery drains battery. Very expensive function.
        bluetoothAdapter.cancelDiscovery();

        // Set state to connecting.
        this.status = Constants.CONNECTING;

        // Send a Toast
        sendToast("Connecting to " + Common.getName(device));


        // If there is stored UUID that was successful, then it adds the UUID to the start of array for
        // faster connection.
        if(!data.getUUID().isEmpty()){
            this.deviceUUIDS.add(0, UUID.fromString(data.getUUID()));
        }

        // Tries to connect to the remote device with the available UUIDs until an exception occurs.
        // Running it on a new Thread as bluetoothSocket.connect() is a blocking call.
        Executors.newSingleThreadExecutor().execute(() -> {
            for (UUID uuid : this.deviceUUIDS) {
                try {
                    connectSocket(device, uuid);
                    data.storeUUID(uuid.toString());
                    break;
                } catch (IOException | NullPointerException f) {
                    close();
                }
            }

            // Try the fallback approach if all UUID's don't work.
            if (!this.isConnected()) {
                try {
                    tryFallBackConnect(device);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
                    close();
                }
            }


            // Set status, store the mac address of the connected device, so it can automatically connect
            // to the same device the next time you open the app.
            if (this.isConnected()) {
                this.status = Constants.CONNECTED;
                this.sendToast("Connected to " + Common.getName(device));
                this.data.storeMac(device.getAddress());

                if(!this.isAlive() && this.receivingStream != null){
                    this.start();
                }

                Common.addLog(this.data, new MyLog(String.format("%s %s", "Connected to", Common.getName(device))));

            } else {
                this.status = Constants.DISCONNECTED;
                this.sendToast("Can't connect to " + Common.getName(device));
            }


        });

    }


    /**
     * @param device A bluetoothDevice to connect.
     * @param uuid This is either device's UUID or SSP profile.
     * @throws IOException If the socket is not connected.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void connectSocket(BluetoothDevice device, UUID uuid) throws IOException {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        this.bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);
        this.bluetoothSocket.connect();
        createStreams();
    }


    /**
     * src: <a href="https://stackoverflow.com/questions/24573755/android-bluetooth-socket-connect-fails">...</a>
     * Sometimes the UUID causes problems with 'bluetoothSocket.connect()'
     *
     * @param device A bluetoothDevice to connect.
     * @throws NoSuchMethodException If the method doesn't even exist.
     * @throws InvocationTargetException
     * @throws IllegalAccessException The the fallback method is deprecated.
     * @throws IOException If the connection is not successful.
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.S)
    private void tryFallBackConnect(BluetoothDevice device) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
        for (int port = 1; port <= 3; port++) {
            Method m = device.getClass().getMethod("createRfcommSocket", int.class);
            this.bluetoothSocket = (BluetoothSocket) m.invoke(device, port);
            this.bluetoothSocket.connect();
            createStreams();
        }
    }


    /**
     * @return If the bluetooth socket is connected.
     */
    public boolean isConnected() {
        return this.bluetoothSocket != null && this.bluetoothSocket.isConnected();
    }


    /**
     * @return The bluetooth socket's connected device.
     */
    public BluetoothDevice getRemoteDevice() {
        return (!this.isConnected())? null : this.bluetoothSocket.getRemoteDevice();
    }


    /**
     * @return the state of the connection.
     */
    public String getStatus(){
        if(this.status.equals(Constants.CONNECTED)){
            this.status = (this.isConnected())? Constants.CONNECTED : Constants.DISCONNECTED;
        }
        return this.status;
    }


    /**
     * @return A list containing device's UUIDs.
     */
    private ArrayList<UUID> getDeviceUUIDS() {
        ParcelUuid[] parcelUuids = new ParcelUuid[0];
        ArrayList<UUID> uuids = new ArrayList<>();

        try {
            Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);
            parcelUuids = (ParcelUuid[]) getUuidsMethod.invoke(this.bluetoothAdapter, null);

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


    /**
     * Closes the bluetooth socket.
     */
    public void close() {
        if(!this.isConnected()){
            return;
        }

        try {
            this.bluetoothSocket.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        this.bluetoothSocket = null;
        status = Constants.DISCONNECTED;

    }

    /**
     * Creates Input and Output stream if the connection to the remote device is successful.
     */
    private void createStreams() {
        if(!this.isConnected()){
            return;
        }

        try {
            this.receivingStream = bluetoothSocket.getInputStream();
            this.sendingStream = bluetoothSocket.getOutputStream();
            Log.d(Constants.TAG, "Sockets created");
        } catch (IOException e) {
            Log.d(Constants.TAG, "Can't create socket streams");
        }
    }



    /**
     * Send a toast that is run on UI thread.
     * Only sends a Toast if the BluetoothService object is created Bluetooth_Discovery_Activity
     * to prevent sending Toasts in MainActivity class as it already as a custom status layout.
     * @param msg Message to toast.
     */
    private void sendToast(String msg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if(this.activity.getClass() == Bluetooth_Discovery_Activity.class){
                this.activity.runOnUiThread(() -> Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show());
            }
        }
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setHandler(Handler handler){
        this.handler = handler;
    }

}
