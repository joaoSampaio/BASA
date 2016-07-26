package pt.ulisboa.tecnico.basa.model.registration;

/**
 * Created by sampaio on 14-06-2016.
 */
public class RegisterAndroidQRCode {
    private String ip;
    private String nounce;

    public RegisterAndroidQRCode(String ip, String nounce) {
        this.nounce = nounce;
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public String getNounce() {
        return nounce;
    }
}
