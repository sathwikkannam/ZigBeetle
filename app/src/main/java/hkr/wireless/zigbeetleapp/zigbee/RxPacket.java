package hkr.wireless.zigbeetleapp.zigbee;

import java.util.Arrays;

public class RxPacket implements Comparable<RxPacket> {
    private final byte[] source16;
    private final byte[] rfData;

    public RxPacket(byte[] source64, byte[] rfData) {
        this.source16 = source64;
        this.rfData = rfData;
    }

    public byte[] getSource16() {
        return this.source16;
    }

    public String getRfData() {
        return new String(this.rfData);
    }



    @Override
    public int compareTo(RxPacket rxPacket) {
        if(Arrays.equals(this.getSource16(), rxPacket.getSource16()) && this.getRfData().equals(rxPacket.getRfData())){
            return  1;
        }

        return 0;
    }
}
