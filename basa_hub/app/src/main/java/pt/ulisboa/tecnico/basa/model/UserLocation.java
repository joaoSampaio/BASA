package pt.ulisboa.tecnico.basa.model;


public class UserLocation {

    public static final int TYPE_BUILDING = 0;
    public static final int TYPE_OFFICE = 1;

    private boolean isInBuilding;
    private int type;

    public UserLocation(boolean isInBuilding, int type) {
        this.isInBuilding = isInBuilding;
        this.type = type;
    }

    public boolean isInBuilding() {
        return isInBuilding;
    }

    public int getType() {
        return type;
    }
}
