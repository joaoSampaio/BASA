package pt.ulisboa.tecnico.mybasaclient.model;

import java.util.List;

/**
 * Created by Sampaio on 10/07/2016.
 */
public class LightBulb {

    private boolean state;

    public LightBulb(boolean state) {
        this.state = state;
    }

    public boolean isOn() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public static boolean[] getArray(List<LightBulb> lights){
        boolean[] data = new boolean[lights.size()];
        int i=0;
        for (LightBulb l : lights) {
            data[i] = l.isOn();
            i++;
        }
        return data;
    }

}
