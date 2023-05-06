package hkr.wireless.zigbeetleapp.Zigbee;

public class Utils {

    public static byte sum(byte[] bytes){
        byte sum = 0x00;

        for (byte aByte : bytes) {
            sum += aByte;
        }

        return sum;
    }

}
