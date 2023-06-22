package hkr.wireless.zigbeetleapp;

import java.util.Arrays;

public class Sensor {
    private final String name;
    private Object parameterValue;
    private int  status;
    public static final int OFF = 1;
    public static final int ON = 0;
    private String parameter = null;
    private final byte[] destination16;
    private final byte[] destination64;

    public Sensor(String name, int status, byte[] destination16, byte[] destination64){
        this.name = name;
        this.status = status;
        this.destination16 = destination16;
        this.destination64 = destination64;
    }

    public <T> Sensor(String name, int status, byte[] destination16, byte[] destination64, String parameter, T parameterValue){
        this.name = name;
        this.status = status;
        this.destination16 = destination16;
        this.parameter = parameter;
        this.destination64 = destination64;
        this.parameterValue = (T) parameterValue;
    }

    public byte[] getDestination64() {
        return destination64;
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

    public byte[] getDestination16() {
        return this.destination16;
    }

    public Object getParameterValue() {
        return parameterValue;
    }

    public <T> void setParameterValue(T parameterValue) {
        this.parameterValue = parameterValue;
    }

    public boolean hasParameter(){
        return this.parameter != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof byte[]) {
            return (Arrays.equals(this.destination16, (byte[]) obj) || Arrays.equals(this.destination64, (byte[]) obj));
        }
        return false;
    }

}
