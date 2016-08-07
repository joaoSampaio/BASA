package pt.ulisboa.tecnico.mybasaclient.rest.pojo;


public class UserLocation {

    public static final int TYPE_BUILDING = 0;
    public static final int TYPE_OFFICE = 1;

    private boolean isInBuilding;
    private int type;
    private long duration;

    public UserLocation(boolean isInBuilding, int type) {
        this.isInBuilding = isInBuilding;
        this.type = type;
        this.duration = -1;
    }

    public UserLocation(boolean isInBuilding, int type, long duration) {
        this.isInBuilding = isInBuilding;
        this.type = type;
        this.duration = duration;
    }

    public boolean isInBuilding() {
        return isInBuilding;
    }

    public int getType() {
        return type;
    }
}
