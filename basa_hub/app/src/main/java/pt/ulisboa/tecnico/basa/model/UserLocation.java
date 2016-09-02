package pt.ulisboa.tecnico.basa.model;


public class UserLocation {

    public static final int TYPE_BUILDING = 0;
    public static final int TYPE_OFFICE = 1;

    private boolean isInBuilding;
    private int type;
    private long duration;
    private long date;

    public UserLocation() {
    }

    public UserLocation(boolean isInBuilding, int type) {
        this.isInBuilding = isInBuilding;
        this.type = type;
        this.duration = -1;
        this.date = System.currentTimeMillis();
    }

    public UserLocation(boolean isInBuilding, int type, long duration) {
        this.isInBuilding = isInBuilding;
        this.type = type;
        this.duration = duration;
        this.date = System.currentTimeMillis();
    }

    public long getDuration() {
        return duration;
    }

    public boolean isInBuilding() {
        return isInBuilding;
    }

    public int getType() {
        return type;
    }

    public void setInBuilding(boolean inBuilding) {
        isInBuilding = inBuilding;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
