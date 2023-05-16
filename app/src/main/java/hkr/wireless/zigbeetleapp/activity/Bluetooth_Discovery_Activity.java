package hkr.wireless.zigbeetleapp.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

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
    private final ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>();
    private ViewBluetoothAdapter viewBluetoothAdapter;
    private LinearLayout toHome, toSettings;
    private Data data;
    private BluetoothService bluetoothService;
    private final Activity activity = this;
    private final ArrayList<String> actions = new ArrayList<>(Arrays.asList(
            BluetoothDevice.ACTION_FOUND,
            BluetoothDevice.ACTION_PAIRING_REQUEST,
            BluetoothAdapter.ACTION_STATE_CHANGED,
            BluetoothAdapter.ACTION_SCAN_MODE_CHANGED

    ));

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);

                if (!discoveredDevices.contains(device)) {
                    viewBluetoothAdapter.add(device);
                    viewBluetoothAdapter.notifyDataSetChanged();
                }
            }


            if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
                Common.checkBluetoothPermission(activity);

                // Custom pin for the Zigbee controller.
                if(device.getAddress().equals(Constants.ZIGBEE_CONTROLLER_MAC)){
                    device.setPin(Constants.ZIGBEE_CONTROLLER_PIN.getBytes());
                }

            }
        }
    };


    /**
     * This activity shows the discovered devices.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_discovery);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.platinum));

        /*
            Views
         */

        listView = findViewById(R.id.BluetoothDevices);
        toHome = findViewById(R.id.toHome);
        toSettings = findViewById(R.id.toSettings);

        /*
            Objects
         */
        data = Data.getInstance(this);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothService = BluetoothService.getInstance(this);
        listView.setOnItemClickListener(this);


        // Register actions to the bluetoothReceiver.
        setBluetoothActions();


        // Set onClick for navigation.
        toHome.setOnClickListener(View -> Common.startActivity(this, MainActivity.class));
        toSettings.setOnClickListener(View -> Common.startActivity(this, Settings_Activity.class));
    }

    @Override
    public void onStart() {
        super.onStart();

        viewBluetoothAdapter = new ViewBluetoothAdapter(getApplicationContext(), this, R.layout.bluetooth_device_item, discoveredDevices);
        listView.setAdapter(viewBluetoothAdapter);

    }


    @Override
    public void onResume() {
        super.onResume();

        setBluetooth(true);
        setDiscoverability();
        discoverDevices();

        bluetoothService.setActivity(this);
    }


    /**
     * Stops discovery upon leaving the activity.
     */
    @Override
    public void onStop() {
        super.onStop();
        Common.checkBluetoothPermission(activity);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
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

        Common.checkBluetoothPermission(activity);

        bluetoothAdapter.cancelDiscovery();
        bluetoothService.connect(bluetoothAdapter.getRemoteDevice(discoveredDevices.get(i).getAddress()));

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
            Common.checkBluetoothPermission(activity);

            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));

        } else if (!enable && bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();

        }

    }


    /**
     * Makes the devices discoverable.
     */
    public void setDiscoverability() {
        Common.checkBluetoothPermission(activity);

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));

        }

    }

    /**
     * Starts and stops bluetooth discovery.
     */
    public void discoverDevices() {
        Common.checkBluetoothPermission(activity);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();

    }


    public void setBluetoothActions(){
        IntentFilter intentFilter =  new IntentFilter();
        actions.forEach(intentFilter::addAction);
        registerReceiver(bluetoothReceiver, intentFilter);

    }

}

