package hkr.wireless.zigbeetleapp.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Sensor;

public class SensorAdapter extends ArrayAdapter<Sensor> implements View.OnClickListener {

    private final int resource;
    private final BluetoothService bluetoothService;

    public SensorAdapter(@NonNull Context context, BluetoothService bluetoothService, int resource, @NonNull ArrayList<Sensor> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.bluetoothService = bluetoothService;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Sensor sensor = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    resource, parent, false);
        }


        TextView name = convertView.findViewById(R.id.sensor_name);
        TextView status = convertView.findViewById(R.id.status);
        TextView panID = convertView.findViewById(R.id.PAN_ID);
        Button on = convertView.findViewById(R.id.turn_ON);
        Button off = convertView.findViewById(R.id.turn_OFF);
        TextView parameterValue = convertView.findViewById(R.id.parameter_value);
        TextView parameterName = convertView.findViewById(R.id.parameter_name);

        if(sensor.getParameterValue() != null && sensor.getParameter() != null){
            parameterValue.setText(sensor.getParameterValue());
            parameterName.setText(String.format("%s:", sensor.getParameter()));

            parameterValue.setVisibility(View.VISIBLE);
            parameterName.setVisibility(View.VISIBLE);
        }

        on.setOnClickListener(this);
        off.setOnClickListener(this);

        name.setText(sensor.getName());
        status.setText(sensor.getStatus());
        panID.setText(sensor.getPanID());

        return convertView;
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.turn_OFF){

        }else if(view.getId() == R.id.turn_ON) {

        }


    }

}
