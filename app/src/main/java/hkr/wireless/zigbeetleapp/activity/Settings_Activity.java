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
import hkr.wireless.zigbeetleapp.utils.Common;
import hkr.wireless.zigbeetleapp.adapters.LogsAdapter;
import hkr.wireless.zigbeetleapp.MyLog;

public class Settings_Activity extends AppCompatActivity {
    private LinearLayout toHome, toBluetooth;
    private ListView logsListView;
    public static LogsAdapter logsAdapter;
    private Data data;
    private ArrayList<MyLog> storedLogs;

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
        logsAdapter =  new LogsAdapter(this, R.layout.log_item, storedLogs);

        toHome.setOnClickListener(View -> Common.startActivity(this, MainActivity.class));
        toBluetooth.setOnClickListener(View -> Common.startActivity(this, Bluetooth_Discovery_Activity.class));

    }

    @Override
    public void onStart() {
        super.onStart();

    }


    /**
     * Sets adapter to logsListView in onResume() as the storedLogs might be changed in other activities.
     */
    @Override
    public void onResume() {
        super.onResume();

        storedLogs = data.getLogs();
        if(storedLogs != null && !storedLogs.isEmpty()){
            logsAdapter =  new LogsAdapter(this, R.layout.log_item, storedLogs);
            logsListView.setAdapter(logsAdapter);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}