package pt.ulisboa.tecnico.basa.model.weather;

/**
 * Created by Sampaio on 26/04/2016.
 */
public class HourlyForecast {

    private FCTTime FCTTIME;
    private Temp temp;
    private String condition;
    private String icon;
    private int humidity;


    public FCTTime getFCTTIME() {
        return FCTTIME;
    }

    public void setFCTTIME(FCTTime FCTTIME) {
        this.FCTTIME = FCTTIME;
    }

    public Temp getTemp() {
        return temp;
    }

    public void setTemp(Temp temp) {
        this.temp = temp;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getIcon() {

        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}