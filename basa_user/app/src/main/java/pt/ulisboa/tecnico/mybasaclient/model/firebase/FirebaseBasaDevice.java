package pt.ulisboa.tecnico.mybasaclient.model.firebase;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sampaio on 20/07/2016.
 */
public class FirebaseBasaDevice {

    private String uuid;
    private String ip;
    private int currentTemperature;
    private int changeTemperature;
    private List<Boolean> lights;
    private HashMap<String, String> liveVideo;
    private HashMap<String, String> videos;
    private boolean record;

    public FirebaseBasaDevice() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(int currentTemperature) {
        this.currentTemperature = currentTemperature;
    }

    public int getChangeTemperature() {
        return changeTemperature;
    }

    public void setChangeTemperature(int changeTemperature) {
        this.changeTemperature = changeTemperature;
    }

    public List<Boolean> getLights() {
        return lights;
    }

    public void setLights(List<Boolean> lights) {
        this.lights = lights;
    }

    public HashMap<String, String> getLiveVideo() {
        return liveVideo;
    }

    public void setLiveVideo(HashMap<String, String> liveVideo) {
        this.liveVideo = liveVideo;
    }

    public HashMap<String, String> getVideos() {
        return videos;
    }

    public void setVideos(HashMap<String, String> videos) {
        this.videos = videos;
    }

    public boolean isRecord() {
        return record;
    }

    public void setRecord(boolean record) {
        this.record = record;
    }
}
