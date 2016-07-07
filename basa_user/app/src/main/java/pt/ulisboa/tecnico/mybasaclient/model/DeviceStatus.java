package pt.ulisboa.tecnico.mybasaclient.model;

public class DeviceStatus {

    private double temperature;
    private boolean[] lights;

    public DeviceStatus(boolean[] lights, double temperature) {
        this.lights = lights;
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public boolean[] getLights() {
        return lights;
    }

    public void setLights(boolean[] lights) {
        this.lights = lights;
    }
}
