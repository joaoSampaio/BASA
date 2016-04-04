package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class LightingManager {

    private boolean[] lights;
    private Context ctx;
    private LightChanged lightChangedListener;

    public LightingManager(Context ctx){
        this.ctx = ctx;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        int numLights = Integer.parseInt(preferences.getString("light_number", "1"));
        this.lights = new boolean[numLights];
        for (int i=0;i<numLights;i++)
            this.lights[i]=false;
    }

    public boolean getLightState(int lightId){
        if(this.lights != null && lightId < this.lights.length){
            return this.lights[lightId];
        }
        return false;
    }

    public void toggleLight(int lightId){
        if(this.lights != null && lightId < this.lights.length){
            this.lights[lightId] = !this.lights[lightId];
            if(this.getLightChangedListener() != null)
                if(this.lights[lightId])
                    this.getLightChangedListener().onLightON(lightId);
                else
                    this.getLightChangedListener().onLightOFF(lightId);
        }
    }

    public void turnONLight(int lightId){
        if(lightId < this.lights.length){
            this.lights[lightId] = true;
            if(this.getLightChangedListener() != null)
                this.getLightChangedListener().onLightON(lightId);
        }
    }

    public void turnOFFLight(int lightId){
        if(lightId < this.lights.length){
            this.lights[lightId] = false;
            if(this.getLightChangedListener() != null)
                this.getLightChangedListener().onLightOFF(lightId);
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
