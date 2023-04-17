package hkr.wireless.zigbeetleapp;

public class Sensor {
    private final String name;
    private final String ID;


    public Sensor(String name, String ID){
        this.ID = ID;
        this.name = name;

    }


    public String getName() {
        return name;
    }

    public String getID() {
        return ID;

    }
}
