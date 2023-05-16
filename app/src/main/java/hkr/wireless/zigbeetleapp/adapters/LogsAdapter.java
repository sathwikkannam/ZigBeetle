package hkr.wireless.zigbeetleapp.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import hkr.wireless.zigbeetleapp.MyLog;
import hkr.wireless.zigbeetleapp.R;

public class LogsAdapter extends ArrayAdapter<MyLog> {

    private final int resource;

    public LogsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MyLog> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }


    @RequiresApi(api = Build.VERSION_CODES.S)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyLog log = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    resource, parent, false);
        }

        TextView time = convertView.findViewById(R.id.time);
        TextView logView = convertView.findViewById(R.id.log);


        time.setText(String.format("%s-%s | ", log.getDate(), log.getTime()));
        logView.setText(String.format("%s", log.getLog()));


        return convertView;
    }
}
