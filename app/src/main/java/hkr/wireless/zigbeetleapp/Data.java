package hkr.wireless.zigbeetleapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class Data{
    private final SharedPreferences.Editor writer;
    private final SharedPreferences reader;
    private static Data data = null;
    private final Gson gson;
    private String DATA_NAME = "DATA_NAME";

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


    public void storeLogs(ArrayList<MyLog> logs){
        this.writer.putString(Constants.LOGS_LIST, gson.toJson(logs)).apply();

    }

    public ArrayList<MyLog> getLogs(){
        Type type = new TypeToken<ArrayList<MyLog>>() {}.getType();
        return gson.fromJson(this.reader.getString(Constants.LOGS_LIST, ""), type);
    }


    public void storeSensors(ArrayList<Sensor> sensors){
        this.writer.putString(Constants.STORE_SENSORS, gson.toJson(sensors)).apply();

    }

    public ArrayList<Sensor> getSensors(){
        Type type = new TypeToken<ArrayList<Sensor>>() {}.getType();
        return gson.fromJson(this.reader.getString(Constants.STORE_SENSORS, ""), type);
    }

    public void clearSensors(){
       this.writer.remove(Constants.STORE_SENSORS).apply();
    }



    public void storeUUID(String uuid){
        this.writer.putString(Constants.WORKING_UUID, uuid).apply();
    }

    public String getUUID(){
        return this.reader.getString(Constants.WORKING_UUID, "");
    }


    public void storeMac(String mac){
        this.writer.putString(Constants.PREVIOUS_CONNECTED_DEVICE_MAC, mac).apply();
    }

    public String getMac(){
        return this.reader.getString(Constants.PREVIOUS_CONNECTED_DEVICE_MAC, "");
    }
}