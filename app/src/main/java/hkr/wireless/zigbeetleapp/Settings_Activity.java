package hkr.wireless.zigbeetleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Settings_Activity extends AppCompatActivity {
    LinearLayout toHome, toBluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toHome = findViewById(R.id.toHome);
        toBluetooth = findViewById(R.id.toBluetooth);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            toHome.setOnClickListener(View -> startActivity(new Intent(this, MainActivity.class)));
            toBluetooth.setOnClickListener(View -> startActivity(new Intent(this, Bluetooth_Discovery_Activity.class)));
        }

    }
}