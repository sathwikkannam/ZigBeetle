package hkr.wireless.zigbeetleapp;

public class BlueToothDevice {

    private final String deviceName;
    private final String MAC;


    public BlueToothDevice(String deviceName, String MAC){

        this.deviceName = deviceName;
        this.MAC = MAC;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getMAC() {
        return MAC;
    }
}
