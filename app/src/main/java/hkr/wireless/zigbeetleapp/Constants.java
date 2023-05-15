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
    public static final String STORE_SERVICE_UUID = "STORE_SERVICE_UUID";
    public static final String STORE_CHARACTERISTIC_UUID = "STORE_CHARACTERISTIC_UUID";
    public static final String STORE_SENSORS = "STORE_SENSORS";


    /*
        ZIGBEE related definitions.
     */
    public static final String zigbeeControllerMac = null;
    public static final String TEMPERATURE_SENSOR_PAN_ID = "null";
    public static final String FAN_SENSOR_PAN_ID = "null";
    public static final String HEATER_SENSOR_PAN_ID = "null";


    /*
        BLUETOOTH LOW ENERGY ACTIONS
     */
    public static final String INCOMING_DATA = "Data received";
    public static final String CONNECTING = "Connecting...";
    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";
    public static final String GATT_CONNECTED = "Gatt server connected";
    public static final String FOUND_SERVICES = "Device services found";
    public static final String DATA_SENT = "Successfully sent data";
    public static final String ERROR_DATA_TRANSFER = "Error sending data";
    public static final String ERROR_DATA_RECEIVE = "Error receiving data";
    public static final String ERROR_SERVICES = "Error finding services";
    public static final String DISCONNECTING = "Disconnecting";
    public static final String SENDING = "Sending data";
}
