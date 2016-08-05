package pt.ulisboa.tecnico.mybasaclient.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;

/**
 * Created by sampaio on 28-06-2016.
 */
public class BasaDevice {

    private String id;
    private String url;
    private String name;
    private String description;
    private int numLights;
    private List<Boolean> lights;
    private double latestTemperature;
    private int changeTemperature;
    private List<String> beaconUuids;
    private List<String> macAddress;
    private boolean supportsFirebase;

    public BasaDevice() {
        this.beaconUuids = new ArrayList<>();
        this.macAddress = new ArrayList<>();
        this.lights = new ArrayList<>();
    }

    public BasaDevice(String url, String name, String description) {
        this.url = url;
        this.name = name;
        this.description = description;
        this.beaconUuids = new ArrayList<>();
        this.macAddress = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.latestTemperature = 25;
        this.numLights = 1;
        this.changeTemperature = 20;
    }

    public BasaDevice(String url, String name, String description, double latestTemperature) {
        this.url = url;
        this.name = name;
        this.description = description;
        this.beaconUuids = new ArrayList<>();
        this.macAddress = new ArrayList<>();
        this.latestTemperature = latestTemperature;
        this.numLights = 1;
        this.lights = new ArrayList<>();
        this.changeTemperature = 20;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChangeTemperature() {
        return changeTemperature;
    }

    public void setChangeTemperature(int changeTemperature) {
        this.changeTemperature = changeTemperature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getBeaconUuids() {
        return beaconUuids;
    }

    public List<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(List<String> macAddress) {
        this.macAddress = macAddress;
    }

    public void setBeaconUuids(List<String> beaconUuids) {
        this.beaconUuids = beaconUuids;
    }

    public double getLatestTemperature() {
        return latestTemperature;
    }

    public void setLatestTemperature(double latestTemperature) {
        this.latestTemperature = latestTemperature;
    }


    public List<Boolean> getLights() {
        return lights;
    }

    public void setLights(List<Boolean> lights) {
        this.lights = lights;
    }

    public int getNumLights() {
        return numLights;
    }

    public void setNumLights(int numLights) {
        this.numLights = numLights;
    }

    public boolean isAnyLightOn(){

        Log.d("json", "isAnyLightOn:" + new Gson().toJson(this.getLights()));
        for(boolean l : this.getLights()){
            if(l){
                return true;
            }
        }
        return false;
    }

    public static BasaDevice getCurrentDevice(){
        try {
            String current =  new ModelCache<String>().loadModel(new TypeToken<String>() {
            }.getType(), Global.DATA_CURRENT_DEVICE);


            for(BasaDevice d : AppController.getInstance().getCurrentZone().getDevices()){
                if(d.getId().equals(current))
                    return d;

            }


        } catch (Exception e) {

        }
        return null;
    }


}
