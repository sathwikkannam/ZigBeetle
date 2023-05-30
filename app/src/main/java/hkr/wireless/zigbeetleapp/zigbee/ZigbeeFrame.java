package hkr.wireless.zigbeetleapp.zigbee;

public class ZigbeeFrame {

    /**
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
        byte[] frame = new byte[ZigbeeConstants.TOTAL_FIELDS_LENGTH + msgBytes.length];
        int frameLength = ZigbeeConstants.FRAME_DATA_LENGTH_WITHOUT_DATA + msgBytes.length;

        frame[0] = ZigbeeConstants.START_DELIMITER;
        frame[1] = (byte) ((frameLength >> 8) & 0xFF); // MSB
        frame[2] = (byte) (frameLength & 0xFF); // LSB
        frame[3] = ZigbeeConstants.TX_FRAME_TYPE_64;
        frame[4] = ZigbeeConstants.DEFAULT_FRAME_ID;


        // Add the 64-bit Destination to the frame.
        System.arraycopy(destination64, 0, frame, ZigbeeConstants.DESTINATION_64_BYTE_INDEX_FROM, destination64.length);

        // Options
        frame[13] = (byte) (ZigbeeConstants.DEFAULT_TIMEOUT | ZigbeeConstants.DEFAULT_APS_ENCRYPTION | ZigbeeConstants.DEFAULT_DISABLE_RETRIES);

        // Add RF data in big Endian to Packet.
        System.arraycopy(msgBytes, 0, frame, ZigbeeConstants.TX_RF_DATA_INDEX_FROM, msgBytes.length);

        frame[frame.length - 1] = checksum(frame);

        return frame;
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
     * @param receivedFrame bytes from bluetooth.
     * @return an ParsedRxFrame containing 16-bit source address and RF data.
     */
    public static ParsedRxFrame parseRxFrame(byte[] receivedFrame) {
        if (receivedFrame == null) {
            return null;
        }

        byte[] text = new byte[receivedFrame.length - ZigbeeConstants.RX_RF_DATA_INDEX_FROM - 1];
        byte[] source = new byte[]{receivedFrame[ZigbeeConstants.ADDRESS_16_INDEX_FROM], receivedFrame[ZigbeeConstants.ADDRESS_16_INDEX_FROM + 1]};
        System.arraycopy(receivedFrame, ZigbeeConstants.RX_RF_DATA_INDEX_FROM, text, 0, receivedFrame.length - ZigbeeConstants.RX_RF_DATA_INDEX_FROM - 1);

        return new ParsedRxFrame(source, text, checksum(receivedFrame) == receivedFrame[receivedFrame.length - 1]);
    }


    /**
     * src: <a href="https://www.digi.com/resources/documentation/Digidocs/90002002/Content/Tasks/t_calculate_checksum.htm?TocPath=API%20Operation%7CAPI%20frame%20format%7C_____1">...</a>
     * Frame[i < 3] is frame delimiter, and 2 bytes of frame length.
     * @return Hash sum of frame excluding frame delimiter, and frame length
     */
    private static byte checksum(byte[] frame){
        int sum = 0;

        for (int i = 3; i < frame.length; i++) {
            sum += frame[i];
        }

        // No need to map the value to a byte by (sum & 0xFF), casting it to (byte) works.
        return (byte) (0xFF - sum);
    }


}
