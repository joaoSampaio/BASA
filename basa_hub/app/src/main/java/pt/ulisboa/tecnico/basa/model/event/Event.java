package pt.ulisboa.tecnico.basa.model.event;



public class Event {

    public final static int TEMPERATURE = 0;
    public final static int MOTION = 1;
    public final static int SPEECH = 2;
    public final static int CUSTOM_SWITCH = 3;
    public final static int CLAP = 4;
    public final static int TIME = 5;

    public final static int USER_LOCATION = 6;
    public final static int BRIGHTNESS = 7;
    public final static int LIGHT = 8;
    public final static int CHANGE_TEMPERATURE = 9;

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
