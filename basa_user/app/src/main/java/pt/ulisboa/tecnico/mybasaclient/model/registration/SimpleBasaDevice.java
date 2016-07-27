package pt.ulisboa.tecnico.mybasaclient.model.registration;

public class SimpleBasaDevice {

    private String ip;
    private String nounce;

    public SimpleBasaDevice(String url, String nounce) {
        this.ip = url;
        this.nounce = nounce;
    }

    public String getUrl() {
        return ip;
    }

    public void setUrl(String url) {
        this.ip = url;
    }

    public String getToken() {
        return nounce;
    }

    public void setToken(String token) {
        this.nounce = token;
    }
}
