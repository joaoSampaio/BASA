package pt.ulisboa.tecnico.mybasaclient.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;

/**
 * Created by sampaio on 29-06-2016.
 */
public class Zone {

    private String name;
    private List<BasaDevice> devices;
    private boolean isUserInZone;

    public Zone(){
        devices = new ArrayList<>();
    }


    public Zone(String name) {
        this.name = name;
        devices = new ArrayList<>();
        this.isUserInZone = false;
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

    public void addDevice(BasaDevice device){
        if(!containsDevice(device.getId()))
        devices.add(device);
    }

    public boolean isUserInZone() {
        return isUserInZone;
    }

    public void setUserInZone(boolean userInZone) {
        isUserInZone = userInZone;
    }

    public boolean containsDevice(String id){

        for (BasaDevice device : devices){
            if(device.getId().equals(id))
                return true;
        }
        return false;
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

    public static Zone getCurrentZone(){
        try {
            String current =  new ModelCache<String>().loadModel(new TypeToken<String>() {
            }.getType(), Global.CURRENT_ZONE);

            Zone zone = null;
            List<Zone> zones = AppController.getInstance().loadZones();
            for (Zone z : zones){
                if(current != null && z.getName().equals(current))
                    zone = z;
            }

            if(zone == null && !zones.isEmpty())
                zone = zones.get(0);
            if(zone != null){
                new ModelCache<String>().saveModel(zone.getName(), Global.CURRENT_ZONE);
            }
            return zone;


        } catch (Exception e) {
            return null;
        }
    }

    public static void removeDevice(BasaDevice device){
        Zone currentZone = AppController.getInstance().getCurrentZone();
        List<Zone> zones =  AppController.getInstance().loadZones();

        currentZone.getDevices().remove(device);

        AppController.getInstance().saveZones(zones);
        AppController.getInstance().saveCurrentZone(currentZone);
    }

    public static List<Zone> getOtherZones(List<Zone> zones, Zone current){
        List<Zone> tmp = new ArrayList<Zone>(zones);
        Zone zone = null;
        for (Zone z : tmp) {
            if (z.getName().equals(current.getName()))
                zone = z;
        }
        if(zone != null)
            tmp.remove(zone);

        return tmp;
    }

    public static Zone getZoneByName(String name){
        List<Zone> zones = AppController.getInstance().loadZones();
        for (Zone z : zones){
            if(z.getName().equals(name))
                return z;
        }
        return null;
    }
    public static Zone getZoneByName(String name, List<Zone> zones){
        for (Zone z : zones){
            if(z.getName().equals(name))
                return z;
        }
        return null;
    }

    public static void saveCurrentZone(Zone zone){
        new ModelCache<String>().saveModel(zone.getName(), Global.CURRENT_ZONE);
    }

    public static void removeZone(String name){
        List<Zone> zones = AppController.getInstance().loadZones();
        Zone zone = null;
        for (Zone z : zones){
            if(z.getName().equals(name))
                zone = z;
        }
        zones.remove(zone);
        Zone.saveZones(zones);
    }

    public static boolean isZoneCreated(){
        try {
            List<Zone> zones = AppController.getInstance().loadZones();
            return zones != null && !zones.isEmpty();
        }catch (Exception e){
            //if no user is saved an exception my the thrown
            return false;
        }
    }

}
