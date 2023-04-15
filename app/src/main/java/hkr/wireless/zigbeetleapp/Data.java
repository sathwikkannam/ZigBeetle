package hkr.wireless.zigbeetleapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;


public class Data {
    private final SharedPreferences.Editor writer;
    private final SharedPreferences reader;
    private static Data data = null;

    private final Gson gson;
    private final String DATA_NAME = "DATA_NAME";

    Data(Context context) {
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


    public void storeToConnect(BluetoothDevice device){
        this.writer.putString("ToCONNECT", gson.toJson(device)).apply();


    }

    public BluetoothDevice getToConnect(){
        @SuppressLint("MissingPermission") Type type = new TypeToken<BluetoothDevice>() {}.getType();
        return gson.fromJson(this.reader.getString("ToCONNECT", ""), type);
    }



}