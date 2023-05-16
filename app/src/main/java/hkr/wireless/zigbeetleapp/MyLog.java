package hkr.wireless.zigbeetleapp;

import android.os.Build;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class MyLog implements Comparable<MyLog>{

    private String date;
    private final String log;
    private String time;


    public MyLog(String log){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now().toString();
            this.time = LocalTime.now().toString();
        }

        this.log = log;
    }

    public String getDate() {
        return date;
    }

    public String getLog() {
        return log;
    }

    public String getTime() {
        return time;
    }


    @NonNull
    @Override
    public String toString(){
        return String.format("%s %s | %s", getDate(), getTime(), getLog());
    }

    @Override
    public int compareTo(MyLog logFormat) {
        if(this.toString().equals(logFormat.toString())){
            return 1;
        }

        return  0;
    }
}
