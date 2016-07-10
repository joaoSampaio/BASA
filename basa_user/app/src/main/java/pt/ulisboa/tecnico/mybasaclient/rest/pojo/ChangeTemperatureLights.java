package pt.ulisboa.tecnico.mybasaclient.rest.pojo;


public class ChangeTemperatureLights {

    private int targetTemperature;
    private boolean[] lightsState;

    public ChangeTemperatureLights(boolean[] lightsState, int targetTemperature) {
        this.lightsState = lightsState;
        this.targetTemperature = targetTemperature;
    }

    public int getTargetTemperature() {
        return targetTemperature;
    }

    public void setTargetTemperature(int targetTemperature) {
        this.targetTemperature = targetTemperature;
    }

    public boolean[] getLightsState() {
        return lightsState;
    }

    public void setLightsState(boolean[] lightsState) {
        this.lightsState = lightsState;
    }
}
