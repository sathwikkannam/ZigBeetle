package hkr.wireless.zigbeetleapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ViewBluetoothAdapter extends ArrayAdapter<BluetoothDevice> {
    private final int resource;
    private final Data data;

    public ViewBluetoothAdapter(@NonNull Context context, int resource, @NonNull ArrayList<BluetoothDevice> objects) {
        super(context, resource, objects);
        this.data = new Data(context);
        this.resource = resource;
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothDevice device = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    resource, parent, false);
        }


        TextView deviceName = convertView.findViewById(R.id.device_name);
        TextView MAC = convertView.findViewById(R.id.mac_address);

        deviceName.setText(device.getName());
        MAC.setText(device.getAddress());


        convertView.findViewById(R.id.BluetoothDevices).setOnClickListener(View ->{
            data.storeToConnect(device);


        });

        return convertView;
    }




}
