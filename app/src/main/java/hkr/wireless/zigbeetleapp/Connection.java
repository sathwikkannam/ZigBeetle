package hkr.wireless.zigbeetleapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executors;

public class Connection {
    private final BluetoothAdapter bluetoothAdapter;
    private final Context context;
    private final UUID MY_UUID = UUID.fromString("1223");
    private final String NAME = "ZigBeetleApp";
    private final BluetoothDevice deviceToConnect;
    private InputStream inputStream;
    private final AcceptThread acceptThread;
    private final ConnectThread connectThread;

    public Connection(BluetoothAdapter bluetoothAdapter, Context context, BluetoothDevice deviceToConnect){
        this.bluetoothAdapter = bluetoothAdapter;
        this.context = context;
        this.deviceToConnect = deviceToConnect;
        this.acceptThread = new AcceptThread();
        this.connectThread = new ConnectThread();

    }


    public void sendMessage(String message){
        Executors.newSingleThreadExecutor().execute(() ->{
            try {
                connectThread.mmSocket.getOutputStream().write(message.getBytes());
                InputOutputLog.getInstance(String.format("Sent %s", message));
            } catch (IOException e) {
                Toast.makeText(context, "Error sending data", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public InputStream getResponseStream(){
        Executors.newSingleThreadExecutor().execute(() ->{
            try {
                inputStream = (connectThread.mmSocket.getInputStream());
            } catch (IOException e) {
                Toast.makeText(context, "Error receiving data", Toast.LENGTH_SHORT).show();
            }
        });

        return inputStream;
    }


    class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        @SuppressLint("MissingPermission")
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            } catch (IOException e) {
                Toast.makeText(context, "Socket's listen() method failed", Toast.LENGTH_SHORT).show();
            }
            mmServerSocket = tmp;
        }

        @Override
        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Toast.makeText(context, "Socket's accept() method failed", Toast.LENGTH_SHORT).show();
                    break;
                }

                if (socket == null) {
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
                Toast.makeText(context, "Could not close the connect socket", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        @SuppressLint("MissingPermission")
        public ConnectThread() {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = deviceToConnect.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Toast.makeText(context, "Socket's create() method failed", Toast.LENGTH_SHORT).show();
            }
            mmSocket = tmp;

        }


        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bluetoothAdapter.cancelDiscovery();
            }

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                cancel();
            }

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Toast.makeText(context, "Could not close the client socket", Toast.LENGTH_SHORT).show();
            }
        }
    }



}

