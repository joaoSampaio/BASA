package pt.ulisboa.tecnico.basa.model.registration;

/**
 * Created by sampaio on 28-06-2016.
 */
public class UserRegistration {

    private String email;
    private String username;
    private String uuid; //optional


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
