package pt.ulisboa.tecnico.basa.rest.Pojo;

/**
 * Created by Sampaio on 08/09/2016.
 */
public class ArduinoChangeTemperature {

    public static final int HOT = 0;
    public static final int COLD = 1;
    public static final int SPEED1 = 2;
    public static final int SPEED3 = 3;
    public int ch1; //cold
    public int ch2; //hot
    public int ch3; //speed 1
    public int ch4; //speed 3

    public void turnOff(){
        this.ch1 = 0;
        this.ch2 = 0;
        this.ch3 = 0;
        this.ch4 = 0;
    }

    public void turnOnCold(int speed){
        this.ch1 = 1;
        this.ch2 = 0;
        this.ch3 = speed == SPEED1? 1 : 0 ;
        this.ch4 = speed == SPEED3? 1 : 0 ;
    }

    public void turnOnHot(int speed){
        this.ch1 = 0;
        this.ch2 = 1;
        this.ch3 = speed == SPEED1? 1 : 0 ;
        this.ch4 = speed == SPEED3? 1 : 0 ;
    }

    public boolean isOff(){
        return ch1 == 0 && ch2 == 0 && ch3 == 0 && ch4 == 0;
    }


}
