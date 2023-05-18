package hkr.wireless.zigbeetleapp.zigbee;

public class ZigbeePacket {
    private final byte[] packet;
    private final byte[] msg;
    private final byte[] destination16;
    private final byte[] destination64;
    private final byte[] frameLength;


    public ZigbeePacket(String msg, byte[] destination16, byte[] destination64){
        this.msg = msg.getBytes();
        this.packet = new byte[ZigbeeConstants.TOTAL_FIELDS_LENGTH + this.msg.length];
        this.destination16 = destination16;
        this.destination64 = destination64;
        this.frameLength = new byte[ZigbeeConstants.FRAME_LENGTH_SIZE];
        int length = ZigbeeConstants.FRAME_DATA_LENGTH_WITHOUT_DATA + msg.getBytes().length;
        this.frameLength[0] = (byte) (length & 0xFF);
        this.frameLength[1] = (byte) ((length >> 8) & 0xFF);

        this.createPacket();

    }


    /**
     * src: <a href="https://www.digi.com/resources/documentation/Digidocs/90001942-13/reference/r_zigbee_frame_examples.htm?TocPath=XBee%20API%20mode%7C_____4">...</a>
     */
    private void createPacket(){
        this.packet[0] = ZigbeeConstants.START_DELIMITER;
        this.packet[1] = this.frameLength[1]; // MSB
        this.packet[2] = this.frameLength[0]; // LSB
        this.packet[3] = ZigbeeConstants.TX_FRAME_TYPE;
        this.packet[4] = ZigbeeConstants.DEFAULT_FRAME_ID;


        // Add 64 bit Destination to packet.
        System.arraycopy(destination64, 0, this.packet, ZigbeeConstants.DESTINATION_64_BYTE_FROM, destination64.length);

        this.packet[13] = this.destination16[0]; // MSB
        this.packet[14] = this.destination16[1]; // LSB
        this.packet[15] = ZigbeeConstants.DEFAULT_BROADCAST_RADIUS;
        this.packet[16] = (byte) (ZigbeeConstants.DEFAULT_TIMEOUT + ZigbeeConstants.DEFAULT_APS_ENCRYPTION + ZigbeeConstants.DEFAULT_DISABLE_RETIRES);

        // Add RF data in big Endian to Packet.
        System.arraycopy(this.msg, 0, this.packet, ZigbeeConstants.RF_DATA_FROM, this.msg.length);

        this.packet[this.packet.length - 1] = this.checksum();
    }



    public byte[] getPacket(){
        return this.packet;
    }


    public static String parse(byte[] receivedMessage){
        int length = receivedMessage.length - 1 - ZigbeeConstants.RF_DATA_FROM;
        byte[] msg =  new byte[length + 1];

        if (receivedMessage.length - 1 >= 0){
            System.arraycopy(receivedMessage, ZigbeeConstants.RF_DATA_FROM, msg, 0, receivedMessage.length - 1);
        }

        return new String(msg);
    }



    private byte checksum(){
        int sum =
                ZigbeeConstants.TX_FRAME_TYPE +
                ZigbeeConstants.DEFAULT_FRAME_ID +
                this.destination64.length +
                this.destination16.length +
                ZigbeeConstants.DEFAULT_BROADCAST_RADIUS +
                ZigbeeConstants.DEFAULT_TIMEOUT +
                ZigbeeConstants.DEFAULT_DISABLE_RETIRES +
                ZigbeeConstants.DEFAULT_APS_ENCRYPTION +
                this.msg.length;

        return (byte) (0xFF - (sum & 0xFF));
    }


}
