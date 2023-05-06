package hkr.wireless.zigbeetleapp.Zigbee;

public class Options {
    private final byte disableEntries;
    private final byte APSEncryption;
    private final byte timeout;

    public Options(byte disableEntires, byte APSEncryption, byte timeout) {
        this.disableEntries = disableEntires;
        this.APSEncryption = APSEncryption;
        this.timeout = timeout;
    }


    public byte getDisableEntries() {
        return disableEntries;
    }

    public byte getAPSEncryption() {
        return APSEncryption;
    }

    public byte getTimeout() {
        return timeout;
    }


    public byte sum(){
        return (byte) (disableEntries + timeout + APSEncryption);

    }
}
