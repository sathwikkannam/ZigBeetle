package hkr.wireless.zigbeetleapp.zigbee;

public class ZigbeePacket {
    private final byte[] packet;
    private final byte[] msg;
    private final byte[] destination64;

    private final int length;

    public ZigbeePacket(String msg, byte[] destination64){
        this.msg = msg.getBytes();
        this.packet = new byte[ZigbeeConstants.TOTAL_FIELDS_LENGTH + this.msg.length];
        this.destination64 = destination64;
        this.length = ZigbeeConstants.FRAME_DATA_LENGTH_WITHOUT_DATA + msg.getBytes().length;

        this.createPacket();

    }


    /**
     * src: <a href="https://www.digi.com/resources/documentation/Digidocs/90001942-13/reference/r_zigbee_frame_examples.htm?TocPath=XBee%20API%20mode%7C_____4">...</a>
     */
    private void createPacket(){
        this.packet[0] = ZigbeeConstants.START_DELIMITER;
        this.packet[1] = (byte) ((length >> 8) & 0xFF); // MSB
        this.packet[2] = (byte) (length & 0xFF); // LSB
        this.packet[3] = ZigbeeConstants.TX_FRAME_TYPE;
        this.packet[4] = ZigbeeConstants.DEFAULT_FRAME_ID;


        // Add the 64-bit Destination to the packet.
        System.arraycopy(destination64, 0, this.packet, ZigbeeConstants.DESTINATION_64_BYTE_INDEX_FROM, destination64.length);

        this.packet[13] = (byte) (ZigbeeConstants.DEFAULT_TIMEOUT + ZigbeeConstants.DEFAULT_APS_ENCRYPTION + ZigbeeConstants.DEFAULT_DISABLE_RETRIES);

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
