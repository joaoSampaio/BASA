package pt.ulisboa.tecnico.basa.model.event;



public class EventTemperature extends Event {

    private int temperature;
    private int humidity;


    public EventTemperature(int type, int temperature) {
        super(type);
        this.temperature = temperature;
    }

    public EventTemperature(int type, int temperature, int humidity) {
        super(type);
        this.temperature = temperature;
        this.humidity = humidity;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}
