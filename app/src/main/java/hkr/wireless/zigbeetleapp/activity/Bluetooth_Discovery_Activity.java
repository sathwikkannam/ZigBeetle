package hkr.wireless.zigbeetleapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.BluetoothLeService;
import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.utils.Common;
import hkr.wireless.zigbeetleapp.adapters.ViewBluetoothAdapter;

@RequiresApi(api = Build.VERSION_CODES.S)
public class Bluetooth_Discovery_Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private ViewBluetoothAdapter viewBluetoothAdapter;
    private LinearLayout toHome, toSettings;
    private Data data;
    private BluetoothService bluetoothService;
    private boolean scanning;
    private BluetoothLeScanner bluetoothLeScanner;
    private Handler handler;
    private Activity activity =this;


    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!devices.contains(result.getDevice())) {
                viewBluetoothAdapter.add(result.getDevice());
                viewBluetoothAdapter.notifyDataSetChanged();
            }
        }
    };


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);

                if (!devices.contains(device)) {
                    viewBluetoothAdapter.add(device);
                    viewBluetoothAdapter.notifyDataSetChanged();
                }
            }
        }
    };


    /**
     * This activity shows the discovered devices.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discovery);

        listView = findViewById(R.id.BluetoothDevices);
        toHome = findViewById(R.id.toHome);
        toSettings = findViewById(R.id.toSettings);

        data = Data.getInstance(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService = BluetoothService.getInstance(this);
        listView.setOnItemClickListener(this);
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        handler =  new Handler();
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.platinum));

        toHome.setOnClickListener(View -> Common.startActivity(this, MainActivity.class));
        toSettings.setOnClickListener(View -> Common.startActivity(this, Settings_Activity.class));
    }

    @Override
    public void onStart() {
        super.onStart();

        viewBluetoothAdapter = new ViewBluetoothAdapter(getApplicationContext(), this, R.layout.bluetooth_device_item, devices);
        listView.setAdapter(viewBluetoothAdapter);

    }


    @Override
    public void onResume() {
        super.onResume();

        setBluetooth(true);
        setDiscoverability();
        discoverDevices();
    }


    /**
     * Stops discovery upon leaving the activity.
     */
    @Override
    public void onStop() {
        super.onStop();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, Constants.REQUEST_ENABLE_BT);
        }


        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    /**
     * @param adapterView Bluetooth_devices adapter
     * @param view bluetooth_device_item
     * @param i Index that points to a BluetoothDevice in devices arraylist.
     * @param l
     *
     * Creates a bond upon clicking on a item in the listview
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, Constants.REQUEST_ENABLE_BT);
        }

        bluetoothAdapter.cancelDiscovery();

        if(devices.get(i).getType() == BluetoothDevice.DEVICE_TYPE_LE){
//            BluetoothLeService e = new BluetoothLeService(this);
//            e.connectGatt(devices.get(i).getAddress());
        }else{
            bluetoothService.connect(bluetoothAdapter.getRemoteDevice(devices.get(i).getAddress()));
        }



    }


    /**
     * @param enable true = on and false = off.
     * Turn bluetooth on or off
     */
    public void setBluetooth(boolean enable) {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enable && !bluetoothAdapter.isEnabled()) {
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
            }

            startActivity(enableBT);
            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(receiver, BTIntent);

        } else if (!enable && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();

        }

    }


    /**
     * Makes the devices discoverable.
     */
    public void setDiscoverability() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, Constants.REQUEST_ENABLE_BT);
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            registerReceiver(receiver, intentFilter);

            startActivity(discoverableIntent);

        }

    }

    /**
     * Starts and stops bluetooth discovery.
     */
    public void discoverDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, Constants.REQUEST_ENABLE_BT);
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, discoverIntent);

        scanLeDevice();
    }


    private void scanLeDevice() {
        if (!scanning) {
            handler.postDelayed(() -> {
                scanning = false;
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, Constants.REQUEST_ENABLE_BT);
                }
                bluetoothLeScanner.stopScan(leScanCallback);
            }, 10000);

            scanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }


}

