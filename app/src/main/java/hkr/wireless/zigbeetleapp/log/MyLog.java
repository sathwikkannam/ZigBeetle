package hkr.wireless.zigbeetleapp.log;

import java.util.ArrayList;

public class MyLog {
    private final ArrayList<LogFormat> logs = new ArrayList<>();
    private static MyLog myLog;

    private MyLog(){

    }

    public static MyLog getInstance(){
        if(myLog == null){
            myLog =  new MyLog();
        }

        return myLog;
    }


    public void add(String log){
        logs.add(new LogFormat(log));
    }


    public ArrayList<LogFormat> getLogs() {
        return logs;
    }




}
