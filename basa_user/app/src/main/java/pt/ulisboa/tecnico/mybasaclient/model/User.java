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


}
