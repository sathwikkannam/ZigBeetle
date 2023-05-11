package hkr.wireless.zigbeetleapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Data;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Utils;
import hkr.wireless.zigbeetleapp.adapters.ViewBluetoothAdapter;
import hkr.wireless.zigbeetleapp.log.MyLog;

@RequiresApi(api = Build.VERSION_CODES.S)
public class Bluetooth_Discovery_Activity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView listView;
    public static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private final Activity activity = this;
    private ViewBluetoothAdapter viewBluetoothAdapter;
    private LinearLayout toHome, toSettings;
    public static BluetoothDevice pairedDevice = null;
    private MyLog myLog;
    private Data data;
    private BluetoothService bluetoothService;


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            switch (action){
                case BluetoothDevice.ACTION_FOUND:{
                    BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);

                    if (!devices.contains(device)) {
                        viewBluetoothAdapter.add(device);
                        viewBluetoothAdapter.notifyDataSetChanged();
                    }

                    break;
                }

                case BluetoothDevice.ACTION_BOND_STATE_CHANGED: {
                    BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);

                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                    }

                    //String name = (device.getName() == null)? device.getAddress() : device.getName();
                    String name = Utils.getName(device);

                    switch (device.getBondState()) {
                        case BluetoothDevice.BOND_BONDED: {
                            Toast.makeText(activity, "Paired to " + name, Toast.LENGTH_SHORT).show();
                            pairedDevice = device;
                            break;
                        }
                        case BluetoothDevice.BOND_BONDING: {
                            Toast.makeText(activity, "Bonding to " + name, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        case BluetoothDevice.BOND_NONE: {
                            Toast.makeText(activity, "Can't bond to " + name, Toast.LENGTH_SHORT).show();
                            break;
                        }
                        default:
                            Toast.makeText(activity, "Already bonded to " + name, Toast.LENGTH_SHORT).show();
                            break;


                    }
                    break;
                }

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discovery);

        listView = findViewById(R.id.BluetoothDevices);
        toHome = findViewById(R.id.toHome);
        toSettings = findViewById(R.id.toSettings);

        myLog = MyLog.getInstance();
        data = Data.getInstance(this);
        bluetoothService = BluetoothService.getInstance(this);
        listView.setOnItemClickListener(this);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.platinum));

        toHome.setOnClickListener(View -> Utils.startActivity(this, MainActivity.class));
        toSettings.setOnClickListener(View -> Utils.startActivity(this, Settings_Activity.class));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_ENABLE_BT);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_BT);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter bondFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        //registerReceiver(receiver, bondFilter);

        viewBluetoothAdapter = new ViewBluetoothAdapter(getApplicationContext(), this, R.layout.bluetooth_device_item, devices);
        listView.setAdapter(viewBluetoothAdapter);

        setBluetooth(true);
        setDiscoverability();
        discoverDevices();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_ENABLE_BT);
        }


        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        //Utils.replaceLogs(data, data.getLogs(), myLog.getLogs());
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_ENABLE_BT);
        }

        bluetoothAdapter.cancelDiscovery();

        Toast.makeText(activity, "Connecting to " + Utils.getName(devices.get(i)), Toast.LENGTH_SHORT).show();
        bluetoothService.connect(devices.get(i), bluetoothService.NORMAL_MODE);

        if(bluetoothService.isConnected()){
            Toast.makeText(activity, "Connected " + Utils.getName(devices.get(i)), Toast.LENGTH_SHORT).show();
            myLog.add(String.format("%s %s", "Connected to", Utils.getName(devices.get(i))));
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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, REQUEST_ENABLE_BT);
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);


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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_ENABLE_BT);
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

        IntentFilter discoverIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, discoverIntent);

    }


}

