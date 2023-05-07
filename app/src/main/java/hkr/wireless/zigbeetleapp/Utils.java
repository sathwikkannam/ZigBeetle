package hkr.wireless.zigbeetleapp;

import java.util.ArrayList;
import java.util.Arrays;

import hkr.wireless.zigbeetleapp.log.LogFormat;

public class Utils {

    public static void replaceLogs(Data data, ArrayList<LogFormat> storedLogs, ArrayList<LogFormat> tempLogs){
        data.clearLogs();

        if(storedLogs == null || storedLogs.isEmpty()){
            data.storeLogs(tempLogs);
        }else{
            if(!Arrays.equals(storedLogs.toArray(), tempLogs.toArray())){
                tempLogs.forEach(i ->{
                    if(!storedLogs.contains(i)){
                        storedLogs.add(i);
                    }
                });

                data.storeLogs(storedLogs);
            }
        }
    }
}
