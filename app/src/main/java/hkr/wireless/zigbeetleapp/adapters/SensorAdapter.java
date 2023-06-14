package hkr.wireless.zigbeetleapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.R;
import hkr.wireless.zigbeetleapp.Sensor;
import hkr.wireless.zigbeetleapp.utils.Common;

public class SensorAdapter extends ArrayAdapter<Sensor> {

    private final int resource;
    private final Handler handler;

    public SensorAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Sensor> objects, Handler handler) {
        super(context, resource, objects);
        this.resource = resource;
        this.handler = handler;
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.S)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Sensor sensor = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }


        TextView name = convertView.findViewById(R.id.sensor_name);
        TextView status = convertView.findViewById(R.id.status);
        TextView mac = convertView.findViewById(R.id.mac);
        Button on = convertView.findViewById(R.id.turn_ON);
        Button off = convertView.findViewById(R.id.turn_OFF);
        TextView parameterValue = convertView.findViewById(R.id.parameter_value);
        TextView parameterName = convertView.findViewById(R.id.parameter_name);

        if(sensor.hasParameter()){
            parameterValue.setText(sensor.getParameterValue());
            parameterName.setText(String.format("%s", sensor.getParameter()));

            parameterValue.setVisibility(View.VISIBLE);
            parameterName.setVisibility(View.VISIBLE);
            on.setVisibility(View.GONE);
            off.setVisibility(View.GONE);
        }


        name.setText(sensor.getName());

        if(sensor.getStatus() == Sensor.ON){
            status.setText("ON");
            status.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
        }else{
            status.setText("OFF");
        }

        mac.setText(Common.byteToString(Constants.HEX_STRING_WITH_DASH, sensor.getDestination64()).replaceFirst("-",""));


        on.setOnClickListener(view -> {
            if(sensor.getStatus() != Sensor.ON){
                handler.obtainMessage(Constants.WRITE_MESSAGE, Sensor.ON, 0, sensor).sendToTarget();
            }
        });


        off.setOnClickListener(view -> {
            if(sensor.getStatus() != Sensor.OFF){
                handler.obtainMessage(Constants.WRITE_MESSAGE, Sensor.OFF, 0, sensor).sendToTarget();
            }
        });

        return convertView;
    }

}
