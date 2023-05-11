package hkr.wireless.zigbeetleapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Utils;
import hkr.wireless.zigbeetleapp.adapters.LogsAdapter;
import hkr.wireless.zigbeetleapp.log.LogFormat;
import hkr.wireless.zigbeetleapp.log.MyLog;

public class Settings_Activity extends AppCompatActivity {
    private LinearLayout toHome, toBluetooth;
    private ListView logsListView;
    public static LogsAdapter logsAdapter;
    private Data data;
    private ArrayList<LogFormat> storedLogs;
    private MyLog myLog;

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.platinum));

        toHome = findViewById(R.id.toHome);
        toBluetooth = findViewById(R.id.toBluetooth);
        logsListView = findViewById(R.id.logs);
        data = Data.getInstance(this);
        myLog = MyLog.getInstance();
        storedLogs = data.getLogs();
        logsAdapter =  new LogsAdapter(this, R.layout.log_item, storedLogs);

        toHome.setOnClickListener(View -> Utils.startActivity(this, MainActivity.class));
        toBluetooth.setOnClickListener(View -> Utils.startActivity(this, Bluetooth_Discovery_Activity.class));

        if(storedLogs != null){
            if(!storedLogs.isEmpty()){
                logsListView.setAdapter(logsAdapter);
            }

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Utils.replaceLogs(data, myLog);
    }
}