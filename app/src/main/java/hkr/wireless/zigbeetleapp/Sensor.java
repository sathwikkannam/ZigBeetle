package hkr.wireless.zigbeetleapp;

public class Sensor {
    private final String name;
    private final int  status;
    public static final int OFF = 1;
    public static final int ON = 0;
    private String parameterValue = null;
    private String parameter = null;
    private final byte[] panID;


    public Sensor(String name, int status, byte[] panID){
        this.name = name;
        this.status = status;
        this.panID = panID;

    }

    public Sensor(String name, int status, byte[] panID, String parameter){
        this.name = name;
        this.status = status;
        this.panID = panID;
        this.parameter = parameter;

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

    public byte[] getPanID() {
        return this.panID;
    }


    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

}
