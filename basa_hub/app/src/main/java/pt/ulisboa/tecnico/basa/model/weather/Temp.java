package pt.ulisboa.tecnico.basa.model.weather;

/**
 * Created by Sampaio on 26/04/2016.
 */
public class Temp {

    private int english;
    private int metric;
//        "english": "66",
//                "metric": "19"
//    },


    public int getEnglish() {
        return english;
    }

    public void setEnglish(int english) {
        this.english = english;
    }

    public int getTemperature() {
        return metric;
    }

    public void setTemperature(int metric) {
        this.metric = metric;
    }
}
