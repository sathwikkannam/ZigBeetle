package hkr.wireless.zigbeetleapp.zigbee;

public class ZigbeeFrame {

    /**
     * src: <a href="https://www.digi.com/resources/documentation/Digidocs/90001942-13/reference/r_zigbee_frame_examples.htm?TocPath=XBee%20API%20mode%7C_____4">...</a>
     * {
     *      Field           |    Size (bytes)
     *     ----------------------------------
     *     START DELIMITER  |   1
     *     FRAME LENGTH     |   2
     *     FRAME TYPE       |   1
     *     FRAME ID         |   1
     *     DESTINATION 64   |   8
     *     OPTIONS          |   1
     *     RF DATA          |   Up to 255 bytes
     *     CHECKSUM         |   1
     * }
     * @param msg RF Data
     * @param destination64 64-bit address of the remote radio module.
     * @return A frame
     */
    public static byte[] build(String msg, byte[] destination64){
        byte[] msgBytes = msg.getBytes();
        byte[] packet = new byte[ZigbeeConstants.TOTAL_FIELDS_LENGTH + msgBytes.length];
        int frameLength = ZigbeeConstants.FRAME_DATA_LENGTH_WITHOUT_DATA + msgBytes.length;

        packet[0] = ZigbeeConstants.START_DELIMITER;
        packet[1] = (byte) ((frameLength >> 8) & 0xFF); // MSB
        packet[2] = (byte) (frameLength & 0xFF); // LSB
        packet[3] = ZigbeeConstants.TX_FRAME_TYPE_64;
        packet[4] = ZigbeeConstants.DEFAULT_FRAME_ID;


        // Add the 64-bit Destination to the packet.
        System.arraycopy(destination64, 0, packet, ZigbeeConstants.DESTINATION_64_BYTE_INDEX_FROM, destination64.length);

        // Options
        packet[13] = (byte) (ZigbeeConstants.DEFAULT_TIMEOUT | ZigbeeConstants.DEFAULT_APS_ENCRYPTION | ZigbeeConstants.DEFAULT_DISABLE_RETRIES);

        // Add RF data in big Endian to Packet.
        System.arraycopy(msgBytes, 0, packet, ZigbeeConstants.TX_RF_DATA_INDEX_FROM, msgBytes.length);

        packet[packet.length - 1] = checksum(packet);

        return packet;
    }


    public static byte getFrameTypeOf(byte[] receivedMessage) throws IndexOutOfBoundsException {
        return receivedMessage[ZigbeeConstants.RX_FRAME_TYPE_INDEX];
    }


    /**
     * 0x81 RX Receive: {
     *      Field           |    Size (bytes)
     *     ----------------------------------
     *     START DELIMITER  |   1
     *     FRAME LENGTH     |   2
     *     FRAME TYPE       |   1
     *     DESTINATION 16   |   2
     *     RSSI             |   1
     *     OPTIONS          |   1
     *     RF DATA          |   Up to 255 bytes
     *     CHECKSUM         |   1
     * }
     * @param receivedMessage bytes from bluetooth.
     * @return an RxPacket containing 16-bit source address and RF data.
     */
    public static RxPacket parseRxFrame(byte[] receivedMessage) {
        if (receivedMessage == null) {
            return null;
        }

        byte[] text = new byte[receivedMessage.length - ZigbeeConstants.RX_RF_DATA_INDEX_FROM - 1];
        byte[] source = new byte[]{receivedMessage[ZigbeeConstants.ADDRESS_16_INDEX_FROM], receivedMessage[ZigbeeConstants.ADDRESS_16_INDEX_FROM + 1]};
        System.arraycopy(receivedMessage, ZigbeeConstants.RX_RF_DATA_INDEX_FROM, text, 0, receivedMessage.length - ZigbeeConstants.RX_RF_DATA_INDEX_FROM - 1);

        return new RxPacket(source, text);
    }


    /**
     * src: <a href="https://www.digi.com/resources/documentation/Digidocs/90002002/Content/Tasks/t_calculate_checksum.htm?TocPath=API%20Operation%7CAPI%20frame%20format%7C_____1">...</a>
     * Packet[i < 3] is frame delimiter, and 2 bytes of frame length.
     * @return Hash sum of packet excluding frame delimiter, and frame length
     */
    private static byte checksum(byte[] packet){
        int sum = 0;

        for (int i = 3; i < packet.length; i++) {
            sum += packet[i];
        }

        // No need to map the value to a byte by (sum & 0xFF), casting it to (byte) works.
        return (byte) (0xFF - sum);
    }


}
