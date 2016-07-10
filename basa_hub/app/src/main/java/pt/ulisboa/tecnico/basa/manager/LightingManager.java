package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import pt.ulisboa.tecnico.basa.ui.MainActivity;
import pt.ulisboa.tecnico.basa.util.LightingControl;
import pt.ulisboa.tecnico.basa.util.LightingControlEDUP;

public class LightingManager {

    private boolean[] lights;
    private Context ctx;
    private LightChanged lightChangedListener;
    private LightingControl lightingControl;
    private long timeOld = 0;
    private long timeCurrent = 0;

    public LightingManager(MainActivity ctx){
        this.ctx = ctx;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        int numLights = Integer.parseInt(preferences.getString("light_number", "1"));
        this.lights = new boolean[numLights];
        for (int i=0;i<numLights;i++)
            this.lights[i]=false;

        lightingControl = new LightingControlEDUP(ctx);
    }

    public boolean[] getLights() {
        return lights;
    }

    public boolean getLightState(int lightId){
        if(this.lights != null && lightId < this.lights.length){
            return this.lights[lightId];
        }
        return false;
    }

    public void setLightState(boolean[] values){
        Log.d("webserver", "setLightState");


        timeCurrent = System.currentTimeMillis();
        long elapsedTimeNs = timeCurrent - timeOld;
        if (elapsedTimeNs >= 4) {
            //timeOld = timeCurrent;

            for (int i = 0; i < this.lights.length; i++) {

                if(values != null && values.length > i) {
                    if (values[i]) {
                        turnONLight(i, false);
                    } else {
                        turnOFFLight(i, false);
                    }
                }
            }
        }else {
            Log.d("webserver", "setlight too close in time");
        }
    }

    public void toggleLight(int lightId){
        timeOld = System.currentTimeMillis();
        Log.d("light", "toggleLight");
        if(this.lights != null && lightId < this.lights.length){

            this.lights[lightId] = ! this.lights[lightId];
            if (this.lights[lightId])
                turnONLight(lightId, true);
            else
                turnOFFLight(lightId, true);

        }
    }

    public void turnONLight(int lightId, boolean sendServer){
        Log.d("light", "turnONLight");
        if(lightId < this.lights.length){
            this.lights[lightId] = true;
            if(this.getLightChangedListener() != null)
                this.getLightChangedListener().onLightON(lightId);

            if(sendServer)
                lightingControl.sendLightCommand(lights);
        }
    }

    public void turnOFFLight(int lightId, boolean sendServer){
        Log.d("light", "turnOFFLight");
        if(lightId < this.lights.length){
            this.lights[lightId] = false;
            if(this.getLightChangedListener() != null)
                this.getLightChangedListener().onLightOFF(lightId);
            if(sendServer)
                lightingControl.sendLightCommand(lights);
        }
    }


    public LightChanged getLightChangedListener() {
        return lightChangedListener;
    }

    public void setLightChangedListener(LightChanged lightChangedListener) {
        this.lightChangedListener = lightChangedListener;
    }






    public interface LightChanged{
        void onLightON(int lightId);

        void onLightOFF(int lightId);
    }

}
