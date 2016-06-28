package pt.ulisboa.tecnico.mybasaclient.model;

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
}
