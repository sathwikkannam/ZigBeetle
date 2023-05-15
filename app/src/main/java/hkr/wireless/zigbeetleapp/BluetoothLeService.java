package hkr.wireless.zigbeetleapp;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.List;
import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.S)
public class BluetoothLeService {
    private Activity activity;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService service;
    private BluetoothGattCharacteristic characteristic;
    private List<BluetoothGattService> gattServices;
    private final Data data;
    private byte[] incomingData;
    public static BluetoothLeService bluetoothLeService;

    private BluetoothLeService(Activity activity) {
        this.activity = activity;
        data = Data.getInstance(activity);
    }

    public static BluetoothLeService getInstance(Activity activity){
        if(bluetoothLeService == null){
            bluetoothLeService = new BluetoothLeService(activity);
        }

        return bluetoothLeService;
    }


    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);


            if (newState == BluetoothProfile.STATE_CONNECTED) {

                try {
                    Thread.sleep(600);
                } catch (InterruptedException ignored) {

                }

                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
                }

                bluetoothGatt.discoverServices();
                broadcastUpdate(Constants.CONNECTED);


            } else if (newState == BluetoothProfile.STATE_CONNECTING) {
                broadcastUpdate(Constants.CONNECTING);


            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(Constants.DISCONNECTED);

            }else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                broadcastUpdate(Constants.DISCONNECTING);
            }

        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);


            if(status != BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(Constants.ERROR_SERVICES);
                return;
            }


            if (isStoredConnectionDataNotEmpty()) {

                try{
                    service = gatt.getService(UUID.fromString(data.getServiceUUID()));
                    characteristic = service.getCharacteristic(UUID.fromString(data.getCharacteristicUUID()));

                }catch (NullPointerException e){
                    setFirstCharacteristic(gatt);
                }

            } else {

                try{
                    setFirstCharacteristic(gatt);

                }catch (NullPointerException e){
                    e.printStackTrace();

                }

            }

            if(service == null || characteristic == null){
                broadcastUpdate(Constants.ERROR_SERVICES);
                return;
            }

            data.storeServiceUUID(service.getUuid().toString());
            data.storeCharacteristicUUID(characteristic.getUuid().toString());
            broadcastUpdate(Constants.FOUND_SERVICES);

        }


        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);

            if(status != BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(Constants.ERROR_DATA_RECEIVE);
                return;
            }

            incomingData = value;
            broadcastUpdate(Constants.INCOMING_DATA);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            broadcastUpdate((status == BluetoothGatt.GATT_SUCCESS)? Constants.DATA_SENT : Constants.ERROR_DATA_TRANSFER);


        }

        /**
         * Received data.
         *
         * @param gatt Gatt server
         * @param characteristic Receiving Characteristic
         * @param value Received value
         */
        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            super.onCharacteristicChanged(gatt, characteristic, value);
        }

    };



    @RequiresApi(api = Build.VERSION_CODES.S)
    public void connectGatt(String address) {

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        this.disconnect();
        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
        this.bluetoothGatt = device.connectGatt(activity, false, gattCallback);

    }

    public void disconnect(){
        if(this.bluetoothGatt == null){
            return;
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        this.bluetoothGatt.disconnect();
    }



    public void write(byte[] data) {
//        broadcastUpdate(Constants.SENDING);

        this.characteristic.setValue(data);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        this.bluetoothGatt.writeCharacteristic(this.characteristic);
    }


    public List<BluetoothGattService> getGattServices() {
        if (this.bluetoothGatt == null) {
            return null;
        }

        if (this.gattServices == null) {
            this.gattServices = this.bluetoothGatt.getServices();
        }

        return this.gattServices;
    }


    private void setFirstCharacteristic(BluetoothGatt gatt){
        this.service = gatt.getServices().get(0);
        this.characteristic = gatt.getServices().get(0).getCharacteristics().get(0);
    }


    public byte[] getIncomingData() {
        return this.incomingData;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }


    private void broadcastUpdate(final String action) {
        this.activity.sendBroadcast(new Intent(action));

    }

    private boolean isStoredConnectionDataNotEmpty(){
        return data.getServiceUUID() != null && !data.getServiceUUID().equals("") && data.getCharacteristicUUID() != null && !data.getCharacteristicUUID().equals("");
    }



}
