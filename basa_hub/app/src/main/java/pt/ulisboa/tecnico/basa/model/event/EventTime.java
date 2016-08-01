package pt.ulisboa.tecnico.basa.model.event;


public class EventTime extends Event {

    public Long date;
    public EventTime(Long date) {
        super(TIME);
        this.date = date;
    }

    public Long getDate() {
        return date;
    }
}
