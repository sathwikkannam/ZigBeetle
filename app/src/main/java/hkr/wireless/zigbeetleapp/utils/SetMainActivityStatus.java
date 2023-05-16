package hkr.wireless.zigbeetleapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import hkr.wireless.zigbeetleapp.BluetoothService;
import hkr.wireless.zigbeetleapp.Constants;
import hkr.wireless.zigbeetleapp.R;

public class SetMainActivityStatus extends Thread{
    private final TextView statusView;
    private final BluetoothService bluetoothService;
    private boolean state;
    private final Activity activity;
    private String previousState;

    public SetMainActivityStatus(TextView statusView, BluetoothService bluetoothService, Activity activity){
        this.statusView = statusView;
        this.bluetoothService = bluetoothService;
        this.activity = activity;
        state = true;
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void run(){
        while(state){
            String state = this.bluetoothService.getStatus();

            if(!state.equals(previousState)){
                activity.runOnUiThread(() ->{
                    switch (state) {
                        case Constants.CONNECTED:
                            statusView.setText(String.format("%s %s", "Connected to", Common.getName(bluetoothService.getRemoteDevice())));
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.green));
                            break;
                        case Constants.DISCONNECTED:
                            statusView.setText(Constants.DISCONNECTED);
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.red));
                            break;
                        case Constants.CONNECTING:
                            statusView.setText(Constants.CONNECTING);
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.platinum));
                            break;
                        case Constants.NO_DEVICE_FOUND:
                            statusView.setText(Constants.NO_DEVICE_FOUND);
                            statusView.setTextColor(ContextCompat.getColor(activity, R.color.red));
                    }

                });

                previousState = state;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }

        }

    }


    public void setState(boolean start) {
        this.state = start;
    }
}
