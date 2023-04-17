package hkr.wireless.zigbeetleapp;

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

    public InputOutputLog(String log){
        this.log = log;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now();
            this.time = LocalTime.now();
        }

        logs.add(this);
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
