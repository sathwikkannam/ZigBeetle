package hkr.wireless.zigbeetleapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.log.LogFormat;
import hkr.wireless.zigbeetleapp.log.MyLog;


public class Data {
    private final SharedPreferences.Editor writer;
    private final SharedPreferences reader;
    private static Data data = null;
    private final Gson gson;
    private final String DATA_NAME = "DATA_NAME";

    private Data(Context context) {
        this.gson = new Gson();
        SharedPreferences preferencesWriter = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE);
        this.reader = context.getSharedPreferences(DATA_NAME, Context.MODE_PRIVATE);
        this.writer = preferencesWriter.edit();

    }

    public static Data getInstance(Context context){
        if(data == null){
            data = new Data(context);
        }
        return data;
    }


    public void storeLogs(ArrayList<LogFormat> logs){
        this.writer.putString(StorageKeys.LOGS_LIST, new Gson().toJson(logs)).apply();

    }

    public void clearLogs(){
        this.writer.remove(StorageKeys.LOGS_LIST).apply();
    }


    public ArrayList<LogFormat> getLogs(){
        Type type = new TypeToken<ArrayList<LogFormat>>() {}.getType();
        return gson.fromJson(this.reader.getString(StorageKeys.LOGS_LIST, ""), type);
    }




}