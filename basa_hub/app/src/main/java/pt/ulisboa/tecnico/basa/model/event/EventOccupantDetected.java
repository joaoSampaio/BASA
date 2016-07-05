package pt.ulisboa.tecnico.basa.model.event;



public class EventOccupantDetected extends Event {

    private boolean detected;


    public EventOccupantDetected(int type, boolean detected) {
        super(type);
        this.detected = detected;
    }

    public boolean isDetected() {
        return detected;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }
}
