package pt.ulisboa.tecnico.basa.model.event;



public class EventCustomSwitchPressed extends Event {

    private int id;


    public EventCustomSwitchPressed(int id) {
        super(CUSTOM_SWITCH);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
