package hkr.wireless.zigbeetleapp.zigbee;

public class ZigbeeConstants {


    public static byte START_DELIMITER = 0x7E;
    public static byte TX_FRAME_TYPE_64 = 0x00;
    public static byte DEFAULT_DISABLE_RETRIES = 0x00;
    public static byte DEFAULT_APS_ENCRYPTION = 0x00;
    public static byte DEFAULT_TIMEOUT = 0x00;

    /*
        0x00 - No TX (Transmit) Status
     */
    public static byte DEFAULT_FRAME_ID = 0x00;
    public static int DESTINATION_64_BYTE_INDEX_FROM = 5;
    public static int TX_RF_DATA_INDEX_FROM = 14;
    public static int TOTAL_FIELDS_LENGTH = 15;
    public static int FRAME_DATA_LENGTH_WITHOUT_DATA = 11;


    // RX Frame
    public static int ADDRESS_16_INDEX_FROM = 4;
    public static int RX_RF_DATA_INDEX_FROM = 8;
    public static int RX_FRAME_TYPE_INDEX = 3;
    public static byte RX_RECEIVE_TYPE_16 = (byte) 0x81;
    public static int MAX_PAYLOAD_SIZE = 256;
    public static int MTU = MAX_PAYLOAD_SIZE + TOTAL_FIELDS_LENGTH;

}
