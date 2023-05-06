package hkr.wireless.zigbeetleapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

import hkr.wireless.zigbeetleapp.R;

public class Settings_Activity extends AppCompatActivity {
    LinearLayout toHome, toBluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.platinum));

        toHome = findViewById(R.id.toHome);
        toBluetooth = findViewById(R.id.toBluetooth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toHome.setOnClickListener(View -> startActivity(new Intent(this, MainActivity.class)));
            toBluetooth.setOnClickListener(View -> startActivity(new Intent(this, Bluetooth_Discovery_Activity.class)));
        }

    }
}