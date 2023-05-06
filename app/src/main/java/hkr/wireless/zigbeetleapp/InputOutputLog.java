package hkr.wireless.zigbeetleapp;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class InputOutputLog {

    private LocalDate date;
    private final String log;
    private LocalTime time;

    private final ArrayList<InputOutputLog> logs = new ArrayList<>();

    private static InputOutputLog inputOutputLog;

    private InputOutputLog(String log){
        this.log = log;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now();
            this.time = LocalTime.now();
        }

        logs.add(this);
    }


    private void addLog(String log){
        
    }



    public static InputOutputLog getInstance(String log){
        if(inputOutputLog == null){
            inputOutputLog = new InputOutputLog(log);
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

        for (InputOutputLog log : this.logs) {
            logs.append(String.format("%s - %s: %s\n", log.getDate().toString(), log.getTime().toString(), log.getLog()));
        }

        return logs.toString();
    }
}
