package pt.ulisboa.tecnico.basa.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.util.Encryptor;
import pt.ulisboa.tecnico.basa.util.ModelCache;

/**
 * Created by Sampaio on 23/07/2016.
 */
public class BasaDeviceConfig {

    public final static int TEMPERATURE_TYPE_NO_MONITOR_CONTROL = 0;
    public final static int TEMPERATURE_TYPE_MONITOR_BEACON = 1;
    public final static int TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO = 2;

    private String uuid;
    private String name;
    private String description;

    private List<String> macList;
    private List<String> macBeacon;
    private boolean firebaseEnabled;
    private String edupLightId;

    private int temperatureChoice;
    private String beaconUuidTemperature;
    private String arduinoIP;

    private String pinSha;

    public BasaDeviceConfig() {
        this.macList = new ArrayList<>();
        this.macBeacon = new ArrayList<>();
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public boolean isFirebaseEnabled() {
        return firebaseEnabled;
    }

    public void setFirebaseEnabled(boolean firebaseEnabled) {
        this.firebaseEnabled = firebaseEnabled;
    }

    public boolean isPinCorrect(String pin) {

        String attempt = Encryptor.getSHA(pin);

        return attempt != null && attempt.equals(pinSha);
    }

    public boolean isPinDefined(){
        return pinSha != null && !pinSha.isEmpty();
    }

    public void setPinSha(String pinSha) {
        String attempt = Encryptor.getSHA(pinSha);
        this.pinSha = attempt;
    }

    public String getEdupLightId() {
        return edupLightId;
    }

    public int getTemperatureChoice() {
        return temperatureChoice;
    }

    public void setTemperatureChoice(int temperatureChoice) {
        this.temperatureChoice = temperatureChoice;
    }

    public String getBeaconUuidTemperature() {
        return beaconUuidTemperature;
    }

    public void setBeaconUuidTemperature(String beaconUuidTemperature) {
        this.beaconUuidTemperature = beaconUuidTemperature;
    }

    public void setEdupLightId(String edupLightId) {
        this.edupLightId = edupLightId;
    }

    public String getArduinoIP() {
        return arduinoIP;
    }

    public void setArduinoIP(String arduinoIP) {
        this.arduinoIP = arduinoIP;
    }

    public List<String> getMacList() {
        return macList;
    }

    public void setMacList(List<String> macList) {
        this.macList = macList;
    }

    public List<String> getMacBeacon() {
        return macBeacon;
    }

    public void setMacBeacon(List<String> macBeacon) {
        this.macBeacon = macBeacon;
    }

    public static BasaDeviceConfig getConfig(){
        return new ModelCache<BasaDeviceConfig>()
                .loadModel(new TypeToken<BasaDeviceConfig>(){}.getType(), Global.OFFLINE_DEVICE_CONFIG);
    }

    public static void clear(){
        new ModelCache().saveModel(null, Global.OFFLINE_DEVICE_CONFIG);
    }


    public static void save(BasaDeviceConfig conf){
        new ModelCache<BasaDeviceConfig>().saveModel(conf, Global.OFFLINE_DEVICE_CONFIG);
    }

}