package hkr.wireless.zigbeetleapp;

import android.os.Build;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class MyLog {

    private LocalDate date;
    private final String log;
    private LocalTime time;

    private final ArrayList<MyLog> logs = new ArrayList<>();

    private static MyLog inputOutputLog;

    private MyLog(String log){
        this.log = log;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now();
            this.time = LocalTime.now();
        }

        logs.add(this);
    }


    private void addLog(String log){
        
    }

    public static MyLog getInstance(String log){
        if(inputOutputLog == null){
            inputOutputLog = new MyLog(log);
        }

        return inputOutputLog;
    }

    private LocalDate getDate() {
        return date;
    }

    private String getLog() {
        return log;
    }

    private LocalTime getTime() {
        return time;
    }


    @NonNull
    @Override
    public String toString(){
        StringBuilder logs = new StringBuilder();

        for (MyLog log : this.logs) {
            logs.append(String.format("%s - %s: %s\n", log.getDate().toString(), log.getTime().toString(), log.getLog()));
        }

        return logs.toString();
    }
}
