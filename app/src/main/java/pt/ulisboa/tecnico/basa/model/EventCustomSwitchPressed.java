package pt.ulisboa.tecnico.basa.model;



public class EventCustomSwitchPressed extends Event {

    private int id;


    public EventCustomSwitchPressed(int id) {
        super(Event.CUSTOM_SWITCH);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
