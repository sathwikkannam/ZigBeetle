package hkr.wireless.zigbeetleapp.zigbee;

import java.util.Arrays;

public class ParsedRxFrame implements Comparable<ParsedRxFrame> {
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
        return new String(this.rfData);
    }



    @Override
    public int compareTo(ParsedRxFrame parsedRxFrame) {
        if(Arrays.equals(this.getSource16(), parsedRxFrame.getSource16()) && this.getRfData().equals(parsedRxFrame.getRfData())){
            return  1;
        }

        return 0;
    }
}
