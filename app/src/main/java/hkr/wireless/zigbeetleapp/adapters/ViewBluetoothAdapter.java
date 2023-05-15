package hkr.wireless.zigbeetleapp.adapters;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Objects;

import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.utils.Common;

public class ViewBluetoothAdapter extends ArrayAdapter<BluetoothDevice> {
    private final int resource;
    private final Activity activity;

    public ViewBluetoothAdapter(@NonNull Context context, Activity activity, int resource, @NonNull ArrayList<BluetoothDevice> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.activity = activity;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
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

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, Constants.REQUEST_ENABLE_BT);
        }

        deviceName.setText((Objects.equals(Common.getName(device), device.getAddress()))? "Unknown Name" : Common.getName(device));
        //deviceName.setText((device.getName() == null)? "Unknown Name": device.getName());
        MAC.setText(device.getAddress());

        return convertView;
    }


}
