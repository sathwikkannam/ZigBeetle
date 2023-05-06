package hkr.wireless.zigbeetleapp.activity;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import hkr.wireless.zigbeetleapp.R;


public class MainActivity extends AppCompatActivity {
    LinearLayout toBluetooth, toSettings;
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toBluetooth = findViewById(R.id.toBluetooth);
        toSettings = findViewById(R.id.toSettings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toBluetooth.setOnClickListener(View -> startActivity(new Intent(this, Bluetooth_Discovery_Activity.class)));
        }
        toSettings.setOnClickListener(View -> startActivity(new Intent(this, Settings_Activity.class)));




    }





}