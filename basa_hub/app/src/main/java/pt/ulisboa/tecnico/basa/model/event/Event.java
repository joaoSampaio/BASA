package pt.ulisboa.tecnico.basa.model.event;



public class Event {

    public final static int TEMPERATURE = 0;
    public final static int OCCUPANT_DETECTED = 1;
    public final static int VOICE = 2;
    public final static int CUSTOM_SWITCH = 3;
    public final static int CLAP = 4;
    public final static int TIME = 5;

    private int type;

    public Event(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
