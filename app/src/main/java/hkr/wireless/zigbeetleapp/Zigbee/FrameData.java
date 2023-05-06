package hkr.wireless.zigbeetleapp.Zigbee;

public class FrameData {
    private byte frameType;
    private byte frameID;
    private byte[] destination64;
    private byte[] destination16;
    private byte broadcastRadius;
    private Options  options;
    private String data;


    public FrameData(byte[] destination16, String data) {
        this.destination16 = destination16;
        this.data = data;
        this.options = new Options(defaults.DISABLE_RETRIES, defaults.AES_ENCRYPTION, defaults.TIMEOUT);
        this.frameType = defaults.TX_FRAME_TYPE;
        this.frameID = defaults.FRAME_ID;

    }

    public byte getFrameType() {
        return frameType;
    }

    public byte getFrameID() {
        return frameID;
    }

    public byte[] getDestination64() {
        return destination64;
    }

    public byte[] getDestination16() {
        return destination16;
    }

    public byte getBroadcastRadius() {
        return broadcastRadius;
    }

    public Options getOptions() {
        return options;
    }

    public String getData() {
        return data;
    }

    public byte sum(){
        return (byte) (this.frameType + this.frameID + Utils.sum(destination16) + this.broadcastRadius + Utils.sum(data.getBytes()) + this.options.sum());

    }
}
