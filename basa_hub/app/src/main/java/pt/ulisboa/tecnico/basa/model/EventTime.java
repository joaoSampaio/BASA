package pt.ulisboa.tecnico.basa.model;


import java.util.Date;

public class EventTime extends Event {

    public Date date;
    public EventTime(Date date) {
        super(Event.TIME);
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
