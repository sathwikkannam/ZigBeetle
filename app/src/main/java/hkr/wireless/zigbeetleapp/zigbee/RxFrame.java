package hkr.wireless.zigbeetleapp.zigbee;

import java.util.Arrays;

public class RxFrame implements Comparable<RxFrame> {
    private final byte[] source16;
    private final byte[] rfData;
    private final boolean checksumValid;

    public RxFrame(byte[] source64, byte[] rfData, boolean checksumValid) {
        this.source16 = source64;
        this.rfData = rfData;
        this.checksumValid = checksumValid;
    }

    public boolean isChecksumValid() {
        return checksumValid;
    }

    public byte[] getSource16() {
        return this.source16;
    }

    public String getRfData() {
        return new String(this.rfData);
    }



    @Override
    public int compareTo(RxFrame rxFrame) {
        if(Arrays.equals(this.getSource16(), rxFrame.getSource16()) && this.getRfData().equals(rxFrame.getRfData())){
            return  1;
        }

        return 0;
    }
}