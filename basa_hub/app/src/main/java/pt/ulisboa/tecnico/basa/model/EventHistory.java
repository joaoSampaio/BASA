package pt.ulisboa.tecnico.basa.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sampaio on 12/08/2016.
 */
public class EventHistory {

    private String event;
    private String date;
    private String time;

    public EventHistory(String event) {
        this.event = event;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTime = new Date();
        this.date = dateFormat.format(dateTime);
        dateFormat = new SimpleDateFormat("HH:mm:ss");
        this.time = dateFormat.format(dateTime);

    }


    public String getEvent() {
        return event;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
