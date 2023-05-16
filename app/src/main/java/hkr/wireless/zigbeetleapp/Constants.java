package hkr.wireless.zigbeetleapp;

public class Constants {
    /*
        COMMON USE
     */
    public static final String TAG = "ZIGBEE_APP";
    public static final int REQUEST_ENABLE_BT = 1;


    /*
        STORAGE KEYS (SharedPreferences keys)
    */
    public static final String PREVIOUS_CONNECTED_DEVICE_MAC = "PREVIOUS_CONNECTED_DEVICE";
    public static final String LOGS_LIST = "LOGS";
    public static final String WORKING_UUID = "WORKING_UUID";
    public static final String STORE_SENSORS = "STORE_SENSORS";


    /*
        ZIGBEE related definitions.
     */
    public static final String ZIGBEE_CONTROLLER_MAC = "00:06:66:4A:67:86";
    public static final String ZIGBEE_CONTROLLER_PIN = "1234";
    public static final String TEMPERATURE_SENSOR_PAN_ID = "null";
    public static final String FAN_SENSOR_PAN_ID = "null";
    public static final String HEATER_SENSOR_PAN_ID = "null";


    /*
        BLUETOOTH ACTIONS
     */
    public static final String INCOMING_DATA = "Data received";
    public static final String CONNECTING = "Connecting...";
    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";
    public static final String DATA = "DATA";
}
