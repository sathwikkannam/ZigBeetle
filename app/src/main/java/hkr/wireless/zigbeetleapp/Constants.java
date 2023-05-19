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

    // Green:   00-13-A2-00-12-14-B6-C3
    // Black:   00-13-A2-00-41-B7-75-70
    // Red:     00-13-A2-00-41-C7-20-68
    // FM:      00-13-A2-00-41-C7-20-1C
    public static final byte[] TEMPERATURE_SENSOR_MAC = {0x00, 0x13, (byte) 0xA2, 0x00, 0x12, 0x14, (byte) 0xB6, (byte) 0xC3};
    public static final byte[] FAN_SENSOR_MAC = {0x00, 0x13, (byte) 0xA2, 0x00, 0x41, (byte) 0xB7, 0x75, 0x70};
    public static final byte[] HEATER_SENSOR_MAC = {0x00, 0x13, (byte) 0xA2, 0x00, 0x41, (byte) 0xC7, 0x20, 0x68};
    public static final byte[] OTHER_SENSOR_MAC = {0x00, 0x13, (byte) 0xA2, 0x00, 0x41, (byte) 0xC7, 0x20, 0x1C};

    // Common PAN ID for all devices in the same network.
    public static final byte[] PAN_ID = {(byte) 0xAC, (byte) 0xAB};


    /*
        BLUETOOTH actions
     */
    public static final String CONNECTING = "Connecting...";
    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";
    public static final String NO_DEVICE_FOUND = "Can't find device";
    public static final int ZIGBEE_PACKET_MTU = 1024;


    /*
        MainActivity Handler actions.
     */
    public static final int INCOMING_DATA = 1;
    public static final int WRITE_MESSAGE = 2;
}
