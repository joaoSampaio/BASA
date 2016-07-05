package pt.ulisboa.tecnico.basa.model.event;



public class EventTemperature extends Event {

    private double temperature;
    private double humidity;


    public EventTemperature(int type, double temperature) {
        super(type);
        this.temperature = temperature;
    }

    public EventTemperature(int type, double temperature, double humidity) {
        super(type);
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
