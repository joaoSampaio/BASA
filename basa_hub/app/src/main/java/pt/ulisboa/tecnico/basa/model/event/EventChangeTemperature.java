package pt.ulisboa.tecnico.basa.model.event;



public class EventChangeTemperature extends Event {

    private int targetTemperature;

    public EventChangeTemperature(int targetTemperature) {
        super(CHANGE_TEMPERATURE);
        this.targetTemperature = targetTemperature;
    }

    public int getTargetTemperature() {
        return targetTemperature;
    }
}
