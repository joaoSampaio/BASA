package pt.ulisboa.tecnico.mybasaclient.rest.pojo;

/**
 * Created by Sampaio on 05/09/2016.
 */
public class FcmNotificationData {

    String message;

    public FcmNotificationData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
