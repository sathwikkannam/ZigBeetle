package hkr.wireless.zigbeetleapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.UUID;

import hkr.wireless.zigbeetleapp.log.MyLog;


public class Data{
    private SharedPreferences.Editor writer;
    private SharedPreferences reader;
    private static Data data = null;
    private Gson gson;
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
        this.writer.putString(Constants.LOGS_LIST, new Gson().toJson(logs)).apply();

    }

    public void clearLogs(){
        this.writer.remove(Constants.LOGS_LIST).apply();
    }

    public ArrayList<MyLog> getLogs(){
        Type type = new TypeToken<ArrayList<MyLog>>() {}.getType();
        return gson.fromJson(this.reader.getString(Constants.LOGS_LIST, ""), type);
    }


    public void storeSensors(ArrayList<Sensor> sensors){
        this.writer.putString(Constants.STORE_SENSORS, new Gson().toJson(sensors)).apply();

    }

    public ArrayList<Sensor> getSensors(){
        Type type = new TypeToken<ArrayList<Sensor>>() {}.getType();
        return gson.fromJson(this.reader.getString(Constants.STORE_SENSORS, ""), type);
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



    public String getCharacteristicUUID() {
        return this.reader.getString(Constants.STORE_CHARACTERISTIC_UUID, "");
    }

    public String getServiceUUID() {
        return this.reader.getString(Constants.STORE_SERVICE_UUID, "");
    }

    public void storeCharacteristicUUID(String uuid) {
        this.writer.putString(Constants.STORE_CHARACTERISTIC_UUID, uuid).apply();
    }

    public void storeServiceUUID(String uuid) {
        this.writer.putString(Constants.STORE_SERVICE_UUID, uuid).apply();
    }
}