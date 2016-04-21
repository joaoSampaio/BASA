package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class TemperatureManager {

    public final static int COLD = 0;
    public final static int HEAT = 1;
    public final static int COLD_AND_HEAT = 2;
    private List<ActionTemperatureManager> actionTemperatureManagerList;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    SharedPreferences preferences;
    private Context ctx;

    public TemperatureManager(Context ctx){
        this.ctx = ctx;
        actionTemperatureManagerList = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        int numLights = Integer.parseInt(preferences.getString("light_number", "1"));

        preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                if(key.equals(Global.OFFLINE_TEMPERATURE_OUTPUT)){
                    int type = new ModelCache<Integer>().loadModel(new TypeToken<Integer>(){}.getType(), Global.OFFLINE_TEMPERATURE_OUTPUT);
                    for (ActionTemperatureManager listenner: actionTemperatureManagerList) {
                        listenner.onTemperatureOutputChange(type);
                    }
                }

            }
        };

        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    public interface ActionTemperatureManager{
        void onTemperatureOutputChange(int change);
//        void onTemperatureChanged(double temperature);
    }

    public void addListenner(ActionTemperatureManager listenner){
        actionTemperatureManagerList.add(listenner);
    }

    public void destroy() {
        if (actionTemperatureManagerList != null) {
            actionTemperatureManagerList.clear();
            actionTemperatureManagerList = null;
        }
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);

    }
}
