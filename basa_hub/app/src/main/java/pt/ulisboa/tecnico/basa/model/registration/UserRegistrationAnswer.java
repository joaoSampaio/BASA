package pt.ulisboa.tecnico.basa.model.registration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sampaio on 28-06-2016.
 */
public class UserRegistrationAnswer {

    private List<String> uuids;
    private List<String> macAddress;
    private double temperature;
    private int humidity;

    public UserRegistrationAnswer() {
        this.uuids = new ArrayList<>();
        this.macAddress = new ArrayList<>();
        this.temperature = 25;
        this.humidity = 80;


        uuids.add("b9407f30-f5f8-466e-aff9-25556b57fe6d");
    }


    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public List<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(List<String> macAddress) {
        this.macAddress = macAddress;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}
