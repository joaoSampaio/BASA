package pt.ulisboa.tecnico.basa.rest.Pojo;

/**
 * Created by Sampaio on 05/09/2016.
 */
public class FcmNotification {

    String to;
    FcmNotificationData data;

    public FcmNotification(String to, FcmNotificationData data) {
        this.to = to;
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public FcmNotificationData getData() {
        return data;
    }

    public void setData(FcmNotificationData data) {
        this.data = data;
    }

    /*
    * "to": "/topics/foo-bar",
  "data": {
    "message": "This is a Firebase Cloud Messaging Topic Message!",
   }

    * */
}
