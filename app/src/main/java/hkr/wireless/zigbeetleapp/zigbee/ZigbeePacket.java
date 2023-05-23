package hkr.wireless.zigbeetleapp.zigbee;

public class ZigbeePacket {
    private final byte[] packet;
    private final byte[] msg;
    private final byte[] destination16;
    private final byte[] destination64;
    private final int frameLength;

    public ZigbeePacket(String msg, byte[] destination16, byte[] destination64){
        this.msg = msg.getBytes();
        this.destination16 = destination16;
        this.destination64 = destination64;
        this.packet = new byte[ZigbeeConstants.TOTAL_FIELDS_LENGTH + this.msg.length];
        this.frameLength = ZigbeeConstants.FRAME_DATA_LENGTH_WITHOUT_DATA + this.msg.length;

        this.createPacket();

    }


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
     *     DESTINATION 16   |   2
     *     BROADCAST RADIUS |   1
     *     OPTIONS          |   1
     *     RF DATA          |   Up to 255 bytes
     *     CHECKSUM         |   1
     * }
     */
    private void createPacket(){
        this.packet[0] = ZigbeeConstants.START_DELIMITER;
        this.packet[1] = (byte) ((frameLength >> 8) & 0xFF); // MSB
        this.packet[2] = (byte) (frameLength & 0xFF); // LSB
        this.packet[3] = ZigbeeConstants.TX_FRAME_TYPE;
        this.packet[4] = ZigbeeConstants.DEFAULT_FRAME_ID;


        // Add the 64-bit Destination to the packet.
        System.arraycopy(destination64, 0, this.packet, ZigbeeConstants.DESTINATION_64_BYTE_INDEX_FROM, destination64.length);

        this.packet[13] = this.destination16[0]; // MSB
        this.packet[14] = this.destination16[1]; // LSB
        this.packet[15] = ZigbeeConstants.DEFAULT_BROADCAST_RADIUS;
        this.packet[16] = (byte) (ZigbeeConstants.DEFAULT_TIMEOUT | ZigbeeConstants.DEFAULT_APS_ENCRYPTION | ZigbeeConstants.DEFAULT_DISABLE_RETRIES);

        // Add RF data in big Endian to Packet.
        System.arraycopy(this.msg, 0, this.packet, ZigbeeConstants.TX_RF_DATA_INDEX_FROM, this.msg.length);

        this.packet[this.packet.length - 1] = this.checksum();
    }



    public byte[] getBytes(){
        return this.packet;
    }


    public static RxPacket parse(byte[] receivedMessage){
        if(receivedMessage == null){
            return  null;
        }

        byte[] address = new byte[ZigbeeConstants.ADDRESS_64_SIZE];
        byte[] text = new byte[receivedMessage.length - ZigbeeConstants.RX_RF_DATA_INDEX_FROM - 1];

        System.arraycopy(receivedMessage, ZigbeeConstants.RX_ADDRESS_INDEX_FROM, address, 0, ZigbeeConstants.ADDRESS_64_SIZE);

        if (receivedMessage.length - 1 - ZigbeeConstants.RX_ADDRESS_INDEX_FROM >= 0){
            System.arraycopy(receivedMessage, ZigbeeConstants.RX_RF_DATA_INDEX_FROM, text, 0, receivedMessage.length - 1 - ZigbeeConstants.RX_RF_DATA_INDEX_FROM);
        }

        return new RxPacket(address, text);
    }



    /**
     * src: <a href="https://www.digi.com/resources/documentation/Digidocs/90002002/Content/Tasks/t_calculate_checksum.htm?TocPath=API%20Operation%7CAPI%20frame%20format%7C_____1">...</a>
     * Packet[i < 3] is frame delimiter, and 2 bytes of frame length.
     * @return Hash sum of packet excluding frame delimiter, and frame length
     */
    private byte checksum(){
        int sum = 0;

        for (int i = 3; i < packet.length; i++) {
            sum += packet[i];
        }

        // No need to map the value to a byte by (sum & 0xFF), casting it to (byte) works.
        return (byte) (0xFF - sum);
    }


}
