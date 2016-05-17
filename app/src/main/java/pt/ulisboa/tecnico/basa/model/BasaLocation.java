package pt.ulisboa.tecnico.basa.model;

import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.util.ModelCache;

/**
 * Created by Sampaio on 25/04/2016.
 */
public class BasaLocation {

    private double latitude;
    private double longitude;

    public BasaLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static BasaLocation load(){
        BasaLocation location = new ModelCache<BasaLocation>().loadModel(new TypeToken<Integer>(){}.getType(), Global.OFFLINE_LOCATION);
        return location;
    }

    public void save(){
        new  ModelCache<BasaLocation>().saveModel(this, Global.OFFLINE_LOCATION);
    }



}
