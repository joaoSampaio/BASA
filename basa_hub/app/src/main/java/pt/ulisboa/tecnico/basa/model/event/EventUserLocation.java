package pt.ulisboa.tecnico.basa.model.event;


public class EventUserLocation extends Event {

    private String userId;
    private long time;
    public static final int TYPE_BUILDING = 0;
    public static final int TYPE_OFFICE = 1;

    private boolean isInBuilding;
    private int location;
    private boolean isFirstArrive;



    public EventUserLocation(String userId, int type) {
        super(USER_LOCATION);
        this.userId = userId;
        this.time = System.currentTimeMillis();
        this.isInBuilding = true;
        this.location = type;
    }

    public EventUserLocation(String userId, boolean isInBuilding, int type, boolean isFirstArrive) {
        super(USER_LOCATION);
        this.userId = userId;
        this.time = System.currentTimeMillis();
        this.isInBuilding = isInBuilding;
        this.location = type;
        this.isFirstArrive = isFirstArrive;
    }

    public boolean isFirstArrive() {
        return isFirstArrive;
    }

    public String getUserId() {
        return userId;
    }

    public long getTime() {
        return time;
    }

    public boolean isInBuilding() {
        return isInBuilding;
    }

    public int getLocation() {
        return location;
    }
}
