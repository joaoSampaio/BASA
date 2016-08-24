package pt.ulisboa.tecnico.basa.model;

/**
 * Created by Sampaio on 22/08/2016.
 */
public class StatisticalEvent {

    private long x;
    private float y;

    /**
     * @param x
     * @param y
     */
    public StatisticalEvent(long x, float y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
