package hkr.wireless.zigbeetleapp.zigbee;

import java.util.Arrays;

public class RxPacket implements Comparable<RxPacket> {
    private final byte[] sourceMac;
    private final byte[] rfData;

    public RxPacket(byte[] source64, byte[] rfData) {
        this.sourceMac = source64;
        this.rfData = rfData;
    }

    public byte[] getSourceMac() {
        return this.sourceMac;
    }

    public String getRfData() {
        return new String(this.rfData);
    }



    @Override
    public int compareTo(RxPacket rxPacket) {
        if(Arrays.equals(this.getSourceMac(), rxPacket.getSourceMac()) && this.getRfData().equals(rxPacket.getRfData())){
            return  1;
        }

        return 0;
    }
}
