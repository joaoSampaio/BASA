package pt.ulisboa.tecnico.basa.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.event.EventLightSwitch;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;
import pt.ulisboa.tecnico.basa.util.LightingControl;
import pt.ulisboa.tecnico.basa.util.LightingControlEDUP;

public class LightingManager implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private List<Boolean> lights;
    private LightChanged lightChangedListener;
    private LightingControl lightingControl;
    private long timeOld = 0;
    private long timeCurrent = 0;

    public LightingManager(){
        SharedPreferences preferences = getSharedPreferences();
        int numLights = Integer.parseInt(preferences.getString("light_number", "1"));
        this.lights = new ArrayList<>();
        for (int i=0;i<numLights;i++)
            this.lights.add(false);

        lightingControl = new LightingControlEDUP(this);
        getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);



    }

    private SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
    }

    public void destroy(){
        getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);

        if(lightingControl != null)
            lightingControl.destroy();
    }

    public boolean[] getLights() {

        return convert(lights);
    }

    private boolean[] convert(List<Boolean> list){

        boolean[] array = new boolean[list.size()];
        for(int i = 0; i< list.size(); i++)
            array[i] = list.get(i);

        return array;
    }

    public int lightsOn(){
        int count = 0;
        for(Boolean on : lights)
            if(on)
                count++;

        return count;
    }

    public Boolean getLightState(int lightId){
        if(this.lights != null && lightId < this.lights.size()){
            return this.lights.get(lightId);
        }
        return false;
    }

    public void setLightState(boolean[] values, boolean sendServer, boolean sendFireDB){
        Log.d("webserver", "setLightState");
        Log.d("light", "setLightState:"+values.toString());


        timeCurrent = System.currentTimeMillis();
        long elapsedTimeNs = timeCurrent - timeOld;
        if (elapsedTimeNs >= 4) {
            //timeOld = timeCurrent;

            for (int i = 0; i < this.lights.size(); i++) {

                if(values != null && values.length > i) {
                    if (values[i]) {
                        turnONLight(i, sendServer, sendFireDB);
                    } else {
                        turnOFFLight(i, sendServer, sendFireDB);
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
        if(this.lights != null && lightId < this.lights.size()){

            if (!this.lights.get(lightId))
                turnONLight(lightId, true, true);
            else
                turnOFFLight(lightId, true, true);

        }
    }

    public void turnONLight(int lightId, boolean sendServer, boolean sendFireDB){
        Log.d("light", "turnONLight sendFireDB:"+sendFireDB);
        timeOld = System.currentTimeMillis();
        //already on

        if(lightId < this.lights.size()){
            if(this.lights.get(lightId)){
                return;
            }
            this.lights.set(lightId, true);
            if(this.getLightChangedListener() != null)
                this.getLightChangedListener().onLightON(lightId);

            if(sendFireDB && AppController.getInstance().getDeviceConfig().isFirebaseEnabled()) {
                FirebaseHelper mHelperFire = new FirebaseHelper();
                mHelperFire.changeLights(lights);
            }
            if(sendServer)
                lightingControl.sendLightCommand(convert(lights));

            AppController.getInstance().getBasaManager().getEventManager()
                    .addEvent(new EventLightSwitch(lightId,
                            true));

        }
    }

    public void turnOFFLight(int lightId, boolean sendServer, boolean sendFireDB){
        timeOld = System.currentTimeMillis();
        Log.d("light", "turnOFFLight sendFireDB:"+sendFireDB);
        if(lightId < this.lights.size()){

            //already off
            if(!this.lights.get(lightId)){
                return;
            }
            this.lights.set(lightId, false);
            if(this.getLightChangedListener() != null)
                this.getLightChangedListener().onLightOFF(lightId);
            if(sendFireDB && AppController.getInstance().getDeviceConfig().isFirebaseEnabled()) {
                FirebaseHelper mHelperFire = new FirebaseHelper();
                mHelperFire.changeLights(lights);
            }
            if(sendServer)
                lightingControl.sendLightCommand(convert(lights));

            if(AppController.getInstance().getBasaManager().getEventManager() != null)
                AppController.getInstance().getBasaManager().getEventManager()
                        .addEvent(new EventLightSwitch(lightId,
                                false));

        }
    }


    public LightChanged getLightChangedListener() {
        return lightChangedListener;
    }

    public void setLightChangedListener(LightChanged lightChangedListener) {
        this.lightChangedListener = lightChangedListener;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("light_number")){

            SharedPreferences preferences = getSharedPreferences();
            int numLights = Integer.parseInt(preferences.getString("light_number", "1"));
            if(lights.size() > numLights){

                for(int i= 0; i< (lights.size() - numLights); i++){
                    this.lights.remove(lights.size()-1);
                }

            } else if(lights.size() < numLights){
                for(int i= 0; i< ( numLights - lights.size()); i++){
                    this.lights.add(false);
                }
            }



        }
    }


    public long getTimeLastLightClick() {
        return timeOld;
    }

    public boolean hasLightChanged(List<Boolean> received){

        if(received.size() != lights.size())
            return true;
        for (int i= 0; i<lights.size(); i++){

            if(lights.get(i) != received.get(i))
                return true;
        }

        return false;
    }


    public interface LightChanged{
        void onLightON(int lightId);

        void onLightOFF(int lightId);
    }

}
