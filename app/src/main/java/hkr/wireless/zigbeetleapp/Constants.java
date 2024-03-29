package hkr.wireless.zigbeetleapp;


public class Constants {
    /*
        COMMON USE
     */
    public static final String TAG = "ZIGBEE_APP";
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int TEMPERATURE_POLLING_DELAY = 30;
    public static final String HEX_STRING = "%02X ";
    public static final String HEX_STRING_WITH_DASH = "-%02X";

    /*
        STORAGE KEYS (SharedPreferences keys)
    */
    public static final String PREVIOUS_CONNECTED_DEVICE_MAC = "PREVIOUS_CONNECTED_DEVICE";
    public static final String LOGS_LIST = "LOGS";
    public static final String WORKING_UUID = "WORKING_UUID";
    public static final String STORE_SENSORS = "STORE_SENSORS";


    /*
        Radio modules - 16 and 64 bit addresses.
     */
    public static final String ZIGBEE_BLUETOOTH_MODULE_MAC = "00:06:66:4A:67:86";
    public static final String ZIGBEE_BLUETOOTH_MODULE_PIN = "1234";

    // Green:   00-13-A2-00-42-14-B6-C3
    // Black:   00-13-A2-00-41-B7-75-70
    // Red:     00-13-A2-00-41-C7-20-68
    // FM :     00-13-A2-00-41-C7-20-1C - Coordinator.
    public static final byte[] TEMPERATURE_DES_64 = {0x00, 0x13, (byte) 0xA2, 0x00, 0x42, 0x14, (byte) 0xB6, (byte) 0xC3};
    public static final byte[] FAN_DES_64 = {0x00, 0x13, (byte) 0xA2, 0x00, 0x41, (byte) 0xB7, 0x75, 0x70};
    public static final byte[] HEATER_DES_64 = {0x00, 0x13, (byte) 0xA2, 0x00, 0x41, (byte) 0xC7, 0x20, 0x68};
    public static final byte[] COORDINATOR_DES_64 = {0x00, 0x13, (byte) 0xA2, 0x00, 0x41, (byte) 0xC7, 0x20, 0x1C};
    public static final byte[] TEMPERATURE_DES_16 = {(byte) 0xAC, (byte) 0xAB};
    public static final byte[] FAN_DES_16 = {(byte) 0xB1, (byte) 0xAC};
    public static final byte[] HEATER_DES_16 = {(byte) 0x13, (byte) 0xED};
    public static final byte[] COORDINATOR_DES_16 = {(byte) 0x0F, (byte) 0x11};



    public static final int TEMPERATURE_THRESHOLD = 10;

    /*
        BLUETOOTH actions
     */
    public static final String CONNECTING = "Connecting...";
    public static final String CONNECTED = "Connected";
    public static final String DISCONNECTED = "Disconnected";
    public static final String NO_DEVICE_FOUND = "Can't find device";


    /*
        MainActivity Handler actions.
     */
    public static final int INCOMING_DATA = 1;
    public static final int WRITE_MESSAGE = 2;
}
