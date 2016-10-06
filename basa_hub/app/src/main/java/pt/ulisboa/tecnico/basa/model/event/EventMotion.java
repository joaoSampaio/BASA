package pt.ulisboa.tecnico.basa.model.event;



public class EventMotion extends Event {

    private boolean detected;
    private int secondsNoMovement;

    public EventMotion(boolean detected) {
        super(MOTION);
        this.detected = detected;
    }

    public EventMotion(boolean detected, int secondsNoMovement) {
        super(MOTION);
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
