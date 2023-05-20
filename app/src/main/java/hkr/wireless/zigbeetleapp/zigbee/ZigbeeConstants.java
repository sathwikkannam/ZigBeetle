package hkr.wireless.zigbeetleapp.zigbee;

public class ZigbeeConstants {

    public static byte START_DELIMITER = 0x7E;
    public static byte TX_FRAME_TYPE = 0x10;
    public static int DESTINATION_64_BYTE_INDEX_FROM = 5;
    public static int TX_RF_DATA_INDEX_FROM = 17;
    public static int TOTAL_FIELDS_LENGTH = 18;
    public static int FRAME_LENGTH_SIZE = 2;
    public static int FRAME_DATA_LENGTH_WITHOUT_DATA = 14;
    public static byte DEFAULT_DISABLE_RETRIES = 0x00;
    public static byte DEFAULT_APS_ENCRYPTION = 0x00;
    public static byte DEFAULT_TIMEOUT = 0x00;
    public static byte DEFAULT_FRAME_ID = 0x00;
    public static byte DEFAULT_BROADCAST_RADIUS = 0x00;


    // RX packet
    public static int ADDRESS_64_SIZE = 8;
    public static int RX_ADDRESS_INDEX_FROM = 4;
    public static int RX_RF_DATA_INDEX_FROM = 21;

}
