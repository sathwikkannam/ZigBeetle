package hkr.wireless.zigbeetleapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.log.MyLog;

public class Common {

    // Maps MAC address to a custom Alias/Name if BluetoothDevice as no name.
    private static final HashMap<String, String> myDevices = new HashMap<String, String>(){{
       put(Constants.zigbeeControllerMac, "Zigbee Controller");
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



}
