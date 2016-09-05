package pt.ulisboa.tecnico.basa.rest.Pojo;

/**
 * Created by Sampaio on 05/09/2016.
 */
public class FcmNotificationData {

    public static final int MOVEMENT_DETECTED = 0;


    String message;
    String senderId;
    int code;

    public FcmNotificationData(String message, String senderId, int code) {
        this.message = message;
        this.senderId = senderId;
        this.code = code;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
