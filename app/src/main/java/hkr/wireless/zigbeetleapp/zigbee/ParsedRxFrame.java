package hkr.wireless.zigbeetleapp.zigbee;

public class ParsedRxFrame{
    private final byte[] source16;
    private final byte[] rfData;
    private final boolean isChecksumValid;

    public ParsedRxFrame(byte[] source64, byte[] rfData, boolean isChecksumValid) {
        this.source16 = source64;
        this.rfData = rfData;
        this.isChecksumValid = isChecksumValid;
    }

    public boolean isChecksumValid() {
        return isChecksumValid;
    }

    public byte[] getSource16() {
        return this.source16;
    }

    public String getRfData() {
        return new String(this.rfData).replaceAll(" ", "").toLowerCase();
    }

}
