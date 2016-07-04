package pt.ulisboa.tecnico.mybasaclient.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;

/**
 * Created by sampaio on 28-06-2016.
 */
public class BasaDevice {

    private String id;
    private String url;
    private String name;
    private String description;
    private String token;
    private List<String> beaconUuids;

    public BasaDevice(String url, String name, String description, String token) {
        this.url = url;
        this.name = name;
        this.description = description;
        this.token = token;
        this.beaconUuids = new ArrayList<>();
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public static void saveCurrentDevice(BasaDevice device){
        new ModelCache<BasaDevice>().saveModel(device, Global.DATA_CURRENT_DEVICE);
    }

    public static BasaDevice getCurrentDevice(){
        try {
            BasaDevice current =  new ModelCache<BasaDevice>().loadModel(new TypeToken<BasaDevice>() {
            }.getType(), Global.DATA_CURRENT_DEVICE);


            return current;


        } catch (Exception e) {
            return null;
        }
    }


}
