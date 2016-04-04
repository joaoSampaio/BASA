package pt.ulisboa.tecnico.basa.model;



public class EventTemperature extends Event {

    private double temperature;


    public EventTemperature(int type, double temperature) {
        super(type);
        this.temperature = temperature;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
