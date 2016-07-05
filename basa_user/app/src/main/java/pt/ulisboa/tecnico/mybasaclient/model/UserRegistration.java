package pt.ulisboa.tecnico.mybasaclient.model;

/**
 * Created by sampaio on 28-06-2016.
 */
public class UserRegistration {

    private String email;
    private String username;
    private String uuid; //optional
    private String token;

    public UserRegistration(String email, String username, String uuid, String token) {
        this.email = email;
        this.username = username;
        this.uuid = uuid;
        this.token = token;
    }

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
