package hkr.wireless.zigbeetleapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
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
import hkr.wireless.zigbeetleapp.log.MyLog;
import hkr.wireless.zigbeetleapp.utils.Common;
import hkr.wireless.zigbeetleapp.utils.ToActivity;

public class BluetoothService extends Thread {
    private final UUID SPP_PROFILE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String NAME = "ZIGBEE_APP";
    private final BluetoothAdapter adapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream receivingStream;
    private OutputStream sendingStream;
    private final Activity activity;
    private final int REQUEST_ENABLE_BT = 1;
    public static BluetoothService bluetoothService;
    private final Data data;
    public BluetoothDevice pairedDevice;
    private final ArrayList<UUID> deviceUUIDS;
    private ToActivity toMainActivity;
    private String status;


    /**
     * Constructor
     * @param activity Activity
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    private BluetoothService(Activity activity) {
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        this.activity = activity;
        this.data = Data.getInstance(activity);
        this.deviceUUIDS = this.getDeviceUUIDS();
        this.deviceUUIDS.add(SPP_PROFILE);
        this.pairedDevice = null;
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
     * @param msg A send message to send to the remote device.
     */
    public void send(byte[] msg) {
        if (this.sendingStream == null) {
            Log.d(Constants.TAG, "Sending Stream in NULL");
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                this.sendingStream.flush();
                this.sendingStream.write(msg);
                Common.addLog(this.data, new MyLog(String.format("%s %s %s %s", "Sent", new String(msg), "to", Common.getName(this.bluetoothSocket.getRemoteDevice()))));
                Log.d(Constants.TAG, "Sent message");
            } catch (IOException e) {
                Log.d(Constants.TAG, "Error sending message, and is sending stream null: " + String.valueOf(this.sendingStream == null));

            }

        });
    }


    /**
     * Constantly listens for incoming data.
     */
    @Override
    public void run(){
        byte[] response = new byte[1024];
        int outcome;

        while (isConnected() && this.receivingStream != null){
            try {
                outcome = this.receivingStream.read(response);

                if (outcome != -1) {

                    // CHANGE THIS
                    Common.addLog(this.data, new MyLog(String.format("%s %s %s %s", "Received", new String(response), "from", Common.getName(bluetoothSocket.getRemoteDevice()))));
                    this.toMainActivity.sentData(response);
                }

            } catch (IOException e) {

            }

        }

    }


    /**
     * This attempts to connect to the remote bluetooth device with either only SPP profile or
     * try's all UUIDs until a connection is made.
     *
     * @param device A bluetoothDevice to connect to.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    public void connect(BluetoothDevice device) {
        if (device == null) {
            return;
        }

        // Closes the bluetoothSocket if it is already connected.
        this.close();


        // Requires permission to cancel Discovery.
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        // Discovery drains battery. Very expensive function.
        adapter.cancelDiscovery();

        // Set state to connecting.
        this.status = Constants.CONNECTING;

        // Send a Toast
        sendToast("Connecting to " + Common.getName(device));


        // If there is stored UUID that was successful, then it adds the UUID to the start of array for
        // faster connection.
        if(data.getUUID() != null && !data.getUUID().equals("")){
            this.deviceUUIDS.add(0, UUID.fromString(data.getUUID()));
        }

        // Try to connect to the remote device with the available UUIDs until an exception occurs.
        // Running it on a new Thread as bluetoothSocket.connect() is a blocking call.
        Executors.newSingleThreadExecutor().execute(() -> {
            for (UUID uuid : this.deviceUUIDS) {
                try {
                    connectSocket(device, uuid);
                    this.pairedDevice = device;
                    data.storeUUID(uuid.toString());
                    break;
                } catch (IOException | NullPointerException f) {
                    f.printStackTrace();
                    close();
                }
            }

            // Try the fallback Approach if all UUID's don't work.
            if (!this.isConnected()) {
                try {
                    tryFallBackConnect(device);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | IOException e) {
                    e.printStackTrace();
                    close();
                }
            }


            // Set status, store the mac address of the connected device, so it can automatically connect
            // to the same device the next time you open the app.
            if (this.isConnected()) {
                this.status = Constants.CONNECTED;
                this.sendToast("Connected to " + Common.getName(device));
                this.pairedDevice = device;
                this.data.storeMac(device.getAddress());

                if(!this.isAlive()){
                    //this.start();
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
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
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
     * @return If the bluetooth is connected.
     */
    public boolean isConnected() {
        return this.bluetoothSocket != null && this.bluetoothSocket.isConnected();
    }


    /**
     * @return The bluetooth socket's connected device.
     */
    public BluetoothDevice getRemoteDevice() {
        return (this.bluetoothSocket == null)? null : this.bluetoothSocket.getRemoteDevice();
    }


    /**
     * @return the state of the connection.
     */
    public String getStatus(){
        if(this.status.equals(Constants.CONNECTED) && !this.isConnected()){
            this.status = Constants.DISCONNECTED;
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


    /**
     * Closes the bluetooth socket.
     */
    public void close() {
        if (this.isConnected()) {
            try {
                this.bluetoothSocket.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates Input and Output stream if the connection to the remote device is successful.
     */
    private void createStreams() {
        if (this.isConnected()) {
            try {
                this.receivingStream = bluetoothSocket.getInputStream();
                this.sendingStream = bluetoothSocket.getOutputStream();

                Log.d(Constants.TAG, "Sockets created");
            } catch (IOException e) {

                e.printStackTrace();
                Log.d(Constants.TAG, "Can't create socket streams");
            }
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


}
