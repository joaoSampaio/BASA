package pt.ulisboa.tecnico.basa.model.event;


public class EventBrightness extends Event {

    public int mBrightness;
    public EventBrightness(int light) {
        super(BRIGHTNESS);
        this.mBrightness = light;
    }

    public int getmBrightness() {
        return mBrightness;
    }
}
