package hkr.wireless.zigbeetleapp.log;

import android.os.Build;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MyLog implements Comparable<MyLog>{

    private String date;
    private final String log;
    private String time;



    public MyLog(String log){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.date = LocalDate.now().toString();
            this.time = LocalTime.parse(LocalTime.now().toString(), DateTimeFormatter.ofPattern("H:mm:ss")).toString();
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
