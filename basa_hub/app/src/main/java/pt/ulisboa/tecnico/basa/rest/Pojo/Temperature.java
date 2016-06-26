package pt.ulisboa.tecnico.basa.rest.Pojo;

/**
 * Created by Sampaio on 27/05/2016.
 */
public class Temperature {

    private double temperature;
    private double humidity;

    public Temperature(double temperature, double humidity) {
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

    public boolean isValid(){
        return (temperature > 0 && temperature < 60) && (humidity >= 0 && humidity <= 100 );
    }
}
