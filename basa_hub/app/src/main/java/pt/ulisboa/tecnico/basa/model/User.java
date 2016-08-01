package pt.ulisboa.tecnico.basa.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.util.ModelCache;

/**
 * Created by joaosampaio on 08-03-2016.
 */
public class User {
    private String userName;
    private String email;
    private String pin;
    private String uuid;
    private List<Recipe> recipes;


    public User(String userName) {
        this.userName = userName;
    }

    public User(String userName, String uuid) {
        this.userName = userName;
        this.uuid = uuid;
    }

    public User(String userName, String email, String uuid) {
        this.userName = userName;
        this.email = email;
        this.uuid = uuid;
    }

    public String getName() {
        return userName;
    }

    public String getUuid() {
        return uuid;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPin() {
        return pin;
    }

    public static User getUserFromList(List<User> users, String uuid){
        for (User user: users){
            if(user.getUuid() != null && user.getUuid().equals(uuid))
                return user;
        }
        return null;
    }

    public static User getUserNameFromList(List<User> users, String name){
        for (User user: users){
            if(user.getName() != null && user.getName().equals(name))
                return user;
        }
        return null;
    }

    public static User getUserEmailFromList(List<User> users, String email){
        for (User user: users){
            if(user.getName() != null && user.getEmail().equals(email))
                return user;
        }
        return null;
    }


    public static User getUuidFromList(List<User> users, String uuid){
        for (User user: users){
            if(user.getName() != null && user.getUuid().equals(uuid))
                return user;
        }
        return null;
    }

    public static boolean userNameExists(List<User> users, String uuid){
        return getUserNameFromList(users, uuid) != null;
    }

    public static boolean userEmailExists(List<User> users, String email){
        return getUserEmailFromList(users, email) != null;
    }

    public static boolean userUuidExists(List<User> users, String uuid){
        return getUuidFromList(users, uuid) != null;
    }

    public static boolean userExists(List<User> users, String uuid){
        return getUserFromList(users, uuid) != null;
    }



    public static List<User> getUsers(){
        List<User> users = new ModelCache<List<User>>().loadModel(new TypeToken<List<User>>(){}.getType(), Global.OFFLINE_USERS);
        if(users == null)
            users = new ArrayList<>();
        return users;
    }





}
