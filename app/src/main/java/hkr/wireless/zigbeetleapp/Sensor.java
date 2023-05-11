package hkr.wireless.zigbeetleapp;

public class Sensor {
    private final String name;
    private final String status;
    public static final String OFF = "OFF";
    public static final String ON = "ON";

    public Sensor(String name, String status){
        this.name = name;
        this.status = status;

    }


    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;

    }
}
