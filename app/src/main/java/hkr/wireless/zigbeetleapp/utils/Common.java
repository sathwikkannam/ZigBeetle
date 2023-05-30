package hkr.wireless.zigbeetleapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.MyLog;

public class Common {

    // Maps MAC address to a custom alias if BluetoothDevice has no name.
    private static final HashMap<String, String> myDevices = new HashMap<String, String>(){{
       put(Constants.ZIGBEE_BLUETOOTH_MODULE_MAC, "Zigbee bluetooth module");
    }};


    @SuppressLint("MissingPermission")
    public static String getName(BluetoothDevice device) {
        String deviceName = device.getName();
        String deviceMac = device.getAddress();
        String myAlias;

        myAlias = myDevices.get(deviceMac);

        if(myAlias != null){
            return myAlias;
        }

        if(deviceName == null || deviceName.equals("")){
            return deviceMac;
        }

        return deviceName;
    }



    public static void addLog(Data data, MyLog myLog){
        ArrayList<MyLog> storedLogs = data.getLogs();

        if(storedLogs != null && !storedLogs.isEmpty()){
            storedLogs.add(myLog);
            data.storeLogs(storedLogs);
        }else{
            data.storeLogs(new ArrayList<>(Collections.singletonList(myLog)));
        }
    }


    public static <T> void startActivity(Activity from, Class<T> to){
        from.startActivity(new Intent(from, to));
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void checkBluetoothPermission(Activity activity){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Constants.REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, Constants.REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, Constants.REQUEST_ENABLE_BT);
        }

    }


    public static String byteToString(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder();

        for (byte datum : bytes) {
            stringBuilder.append(String.format("%02X ", datum));
        }
        return  stringBuilder.toString();
    }
}
