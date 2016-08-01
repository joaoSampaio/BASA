package pt.ulisboa.tecnico.basa.model.registration;

/**
 * Created by Sampaio on 28/07/2016.
 */
public class BasaDeviceLoad {

    private String name;
    private String description;

    private String macList;
    private String BeaconList;

    private String edupLightId;
    private int edupNumLight;

    private int pin;


    private int temperatureChoice;
    private String beaconOrIp;


    public BasaDeviceLoad() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMacList() {
        return macList;
    }

    public void setMacList(String macList) {
        this.macList = macList;
    }

    public String getBeaconList() {
        return BeaconList;
    }

    public void setBeaconList(String macBeacon) {
        this.BeaconList = macBeacon;
    }

    public String getEdupLightId() {
        return edupLightId;
    }

    public void setEdupLightId(String edupLightId) {
        this.edupLightId = edupLightId;
    }

    public int getEdupNumLight() {
        return edupNumLight;
    }

    public void setEdupNumLight(int edupNumLight) {
        this.edupNumLight = edupNumLight;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public int getTemperatureChoice() {
        return temperatureChoice;
    }

    public void setTemperatureChoice(int temperatureChoice) {
        this.temperatureChoice = temperatureChoice;
    }

    public String getBeaconOrIp() {
        return beaconOrIp;
    }

    public void setBeaconOrIp(String beaconOrIp) {
        this.beaconOrIp = beaconOrIp;
    }
}
