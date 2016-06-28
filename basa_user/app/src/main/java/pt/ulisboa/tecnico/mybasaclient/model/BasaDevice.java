package pt.ulisboa.tecnico.mybasaclient.model;

/**
 * Created by sampaio on 28-06-2016.
 */
public class BasaDevice {

    private String id;
    private String url;
    private String name;
    private String description;
    private String token;

    public BasaDevice(String url, String name, String description, String token) {
        this.url = url;
        this.name = name;
        this.description = description;
        this.token = token;
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
}
