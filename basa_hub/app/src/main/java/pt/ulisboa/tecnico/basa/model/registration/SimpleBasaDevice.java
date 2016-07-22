package pt.ulisboa.tecnico.basa.model.registration;

/**
 * Created by sampaio on 28-06-2016.
 */
public class SimpleBasaDevice {

    private String url;
    private String token;

    public SimpleBasaDevice(String url, String token) {
        this.url = url;
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
