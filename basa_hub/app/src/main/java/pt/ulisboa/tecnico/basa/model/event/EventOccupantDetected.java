package pt.ulisboa.tecnico.basa.model.event;



public class EventOccupantDetected extends Event {

    private boolean detected;
    private int secondsNoMovement;


    public EventOccupantDetected(boolean detected) {
        super(OCCUPANT_DETECTED);
        this.detected = detected;
    }

    public EventOccupantDetected(boolean detected, int secondsNoMovement) {
        super(OCCUPANT_DETECTED);
        this.detected = detected;
        this.secondsNoMovement = secondsNoMovement;
    }

    public boolean isDetected() {
        return detected;
    }

    public int getSecondsNoMovement() {
        return secondsNoMovement;
    }
}
