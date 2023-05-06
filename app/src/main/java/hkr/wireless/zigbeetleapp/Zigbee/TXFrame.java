package hkr.wireless.zigbeetleapp.Zigbee;

public class TXFrame {
    private byte delimeter;
    private byte[] length;
    private FrameData frameData;
    private byte checksum;


    public TXFrame(String data, byte[] destination16){
        this.frameData = new FrameData(destination16, data);
        this.length = new byte[2];
        this.checksum = (byte) (0xFF - ((delimeter + Utils.sum(length) + this.frameData.sum()) & 0xFF));
    }

    public byte getDelimeter() {
        return delimeter;
    }

    public byte[] getLength() {
        return length;
    }

    public FrameData getFrameData() {
        return frameData;
    }

    public byte getChecksum() {
        return checksum;
    }
}
