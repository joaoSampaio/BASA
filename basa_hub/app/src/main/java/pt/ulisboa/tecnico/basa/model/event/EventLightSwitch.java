package pt.ulisboa.tecnico.basa.model.event;


public class EventLightSwitch extends Event {

    public int lightNum;
    private boolean status;
    public EventLightSwitch(int lightNum, boolean status) {
        super(LIGHT);
        this.lightNum = lightNum;
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public int getLightNum() {
        return lightNum+1;
    }

    public int getRealLightNum() {
        return lightNum;
    }
}
