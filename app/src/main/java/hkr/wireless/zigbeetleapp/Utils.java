package hkr.wireless.zigbeetleapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;

import hkr.wireless.zigbeetleapp.activity.MainActivity;
import hkr.wireless.zigbeetleapp.log.LogFormat;
import hkr.wireless.zigbeetleapp.log.MyLog;

public class Utils {

    // Maps MAC address to a custom Alias/Name if BluetoothDevice as no name.
    private static final HashMap<String, String> myDevices = new HashMap<String, String>(){{
       put(MainActivity.zigbeeControllerMac, "Zigbee Controller");
    }};


    @SuppressLint("MissingPermission")
    public static String getName(BluetoothDevice device) {
        String deviceName = device.getName();
        String deviceMac = device.getAddress();
        String myAlias = null;

        if(myDevices != null){
            myAlias = myDevices.get(deviceMac);
        }

        if(myAlias != null){
            return myAlias;
        }

        if(deviceName == null || deviceName.equals("")){
            return deviceMac;
        }

        return deviceName;
    }



    public static void replaceLogs(Data data, MyLog myLog){
        ArrayList<LogFormat> storedLogs = data.getLogs();
        ArrayList<LogFormat> tempLogs = myLog.getLogs();

    }

    public static <T> void startActivity(Activity from, Class<T> to){
        from.startActivity(new Intent(from, to));
    }
}
