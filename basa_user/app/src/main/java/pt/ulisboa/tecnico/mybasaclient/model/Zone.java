package pt.ulisboa.tecnico.mybasaclient.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;

/**
 * Created by sampaio on 29-06-2016.
 */
public class Zone {

    private String name;
    private List<BasaDevice> devices;


    public Zone(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BasaDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<BasaDevice> devices) {
        this.devices = devices;
    }



    public static List<Zone> loadZones(){

        try {
            List<Zone> zones =  new ModelCache<List<Zone>>().loadModel(new TypeToken<List<Zone>>() {
            }.getType(), Global.DATA_ZONE);
            return zones != null? zones : new ArrayList<Zone>();
        }catch (Exception e){
            return new ArrayList<Zone>();
        }
    }
    public static void saveZones(List<Zone> zones){
        new ModelCache<List<Zone>>().saveModel(zones, Global.DATA_ZONE);
    }


}
