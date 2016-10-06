package pt.ulisboa.tecnico.mybasaclient.model;

import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;

/**
 * Created by sampaio on 28-06-2016.
 */
public class User {

    private String userName;
    private String email;
    private String pin;
    private String uuid;
    private boolean enableFirebase;
    private boolean enableTracking;

    private boolean enableTestRoomLocation;
    private boolean enableTestBuildingLocation;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isEnableFirebase() {
        return enableFirebase;
    }

    public void setEnableFirebase(boolean enableFirebase) {
        this.enableFirebase = enableFirebase;
    }

    public boolean isEnableTracking() {
        return enableTracking;
    }

    public void setEnableTracking(boolean enableTracking) {
        this.enableTracking = enableTracking;
    }

    public static User getLoggedUser(){

        try {
            User user =  new ModelCache<User>().loadModel(new TypeToken<User>() {
            }.getType(), Global.DATA_USER);
            return (user != null && !user.getEmail().isEmpty())? user : null;
        }catch (Exception e){
            return null;
        }
    }

    public static void saveUser(User user){
        new ModelCache<>().saveModel(user, Global.DATA_USER);
    }

    public static void signOut(){
        try {
            new ModelCache<String>().saveModel("", Global.DATA_USER);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isEnableTestRoomLocation() {
        return enableTestRoomLocation;
    }

    public void setEnableTestRoomLocation(boolean enableTestRoomLocation) {
        this.enableTestRoomLocation = enableTestRoomLocation;
    }

    public boolean isEnableTestBuildingLocation() {
        return enableTestBuildingLocation;
    }

    public void setEnableTestBuildingLocation(boolean enableTestBuildingLocation) {
        this.enableTestBuildingLocation = enableTestBuildingLocation;
    }
}
