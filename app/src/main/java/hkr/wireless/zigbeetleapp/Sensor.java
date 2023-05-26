package hkr.wireless.zigbeetleapp;

public class Sensor {
    private final String name;
    private int  status;
    public static final int OFF = 1;
    public static final int ON = 0;
    private String parameterValue = null;
    private String parameter = null;
    private final byte[] destination16;
    private final byte[] mac;

    public Sensor(String name, int status, byte[] panID, byte[] mac){
        this.name = name;
        this.status = status;
        this.destination16 = panID;
        this.mac = mac;

    }

    public Sensor(String name, int status, byte[] panID, byte[] mac, String parameter){
        this.name = name;
        this.status = status;
        this.destination16 = panID;
        this.parameter = parameter;
        this.mac = mac;

    }

    public byte[] getMac() {
        return mac;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return this.name;
    }


    public int getStatus() {
        return this.status;

    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public byte[] getDestination16() {
        return this.destination16;
    }


    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

    public boolean hasParameter(){
        return this.parameter != null;
    }

    public String macToString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (byte val : this.mac) {
            stringBuilder.append(String.format("-%02X", val));
        }
        return stringBuilder.toString().replaceFirst("-", "");
    }

}
