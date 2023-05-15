package hkr.wireless.zigbeetleapp;

public class Sensor {
    private final String name;
    private final String status;
    public static final String OFF = "OFF";
    public static final String ON = "ON";
    private String parameterValue = null;
    private String parameter = null;
    private final String panID;


    public Sensor(String name, String status, String panID){
        this.name = name;
        this.status = status;
        this.panID = panID;

    }

    public Sensor(String name, String status, String panID, String parameter){
        this.name = name;
        this.status = status;
        this.panID = panID;
        this.parameter = parameter;

    }


    public String getName() {
        return this.name;
    }


    public String getStatus() {
        return this.status;

    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getPanID() {
        return this.panID;
    }


    public String getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue;
    }

}
