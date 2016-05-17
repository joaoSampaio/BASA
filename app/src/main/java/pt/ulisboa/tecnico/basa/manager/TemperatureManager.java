package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.model.BasaLocation;
import pt.ulisboa.tecnico.basa.model.Event;
import pt.ulisboa.tecnico.basa.model.EventTime;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.model.WeatherForecast;
import pt.ulisboa.tecnico.basa.model.weather.HourlyForecast;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.GetTemperatureListService;
import pt.ulisboa.tecnico.basa.rest.RestClient;
import pt.ulisboa.tecnico.basa.ui.MainActivity;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class TemperatureManager {

    public final static int COLD = 0;
    public final static int HEAT = 1;
    public final static int COLD_AND_HEAT = 2;
    private List<ActionTemperatureManager> actionTemperatureManagerList;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private GlobalTemperatureForecast globalTemperatureForecast;
    SharedPreferences preferences;
    private MainActivity activity;

    public TemperatureManager(MainActivity ctx){
        this.activity = ctx;
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
        updateLocation();



        getActivity().getBasaManager().getEventManager().registerInterest(new InterestEventAssociation(Event.TIME, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {

                if(event instanceof EventTime){
                    EventTime time = (EventTime)event;

                    if(getGlobalTemperatureForecast() != null) {

                        WeatherForecast forecast = WeatherForecast.load();
                        if(forecast != null && forecast.getCurrent() != null){
                            HourlyForecast hourlyForecast = forecast.getCurrent();
                            if(hourlyForecast != null){
                                getGlobalTemperatureForecast().onChangeForecast(hourlyForecast.getTemp().getTemperature(), hourlyForecast.getIcon(), hourlyForecast.getCondition());
                            }

                        }
                    }
                }
            }
        },0));
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
        this.activity = null;
    }

    public MainActivity getActivity() {
        return activity;
    }

    private void updateLocation(){


        new GetTemperatureListService(new CallbackMultiple<WeatherForecast, String>() {
            @Override
            public void success(WeatherForecast forecast) {
                Log.d("web", "Deu:");

                WeatherForecast old = WeatherForecast.load();
                HourlyForecast hourlyOld = old.getCurrent();
                Log.d("web", "hourlyOld:"+(hourlyOld != null));
                if(hourlyOld != null){

                    forecast.getHourly_forecast().add(0, hourlyOld);
                }

                forecast.save();
                if(forecast != null && forecast.getCurrent() != null){
                    HourlyForecast hourlyForecast = forecast.getCurrent();

                    for(HourlyForecast hour: forecast.getHourly_forecast()){
                        Log.d("web", "day:"+hour.getFCTTIME().getMday());
                        Log.d("web", "Hour:"+hour.getFCTTIME().getHour());
                        Log.d("web", "Temperature:"+hour.getTemp().getTemperature());
                        Log.d("web", "Summay:"+hour.getCondition());
                    }



                    if(hourlyForecast != null){
                        Log.d("web", "Current day:"+hourlyForecast.getFCTTIME().getMday());
                        Log.d("web", "Current Hour:"+hourlyForecast.getFCTTIME().getHour());
                        Log.d("web", "Current Temperature:"+hourlyForecast.getTemp().getTemperature());
                        Log.d("web", "Current Summay:"+hourlyForecast.getCondition());
                        getGlobalTemperatureForecast().onChangeForecast(hourlyForecast.getTemp().getTemperature(), hourlyForecast.getIcon(), hourlyForecast.getCondition());
                    }

                }



            }

            @Override
            public void failed(String error) {
                Log.d("web", "Fail:");
            }
        }).execute();

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("updateLocation", "getLatitude:" + location.getLatitude());
                Log.d("updateLocation", "getLongitude:" + location.getLongitude());
                BasaLocation basaLocation = new BasaLocation(location.getLatitude(), location.getLongitude());
                basaLocation.save();
//                makeUseOfNewLocation(location);
                //http://api.wunderground.com/api/d46d181594664567/hourly/lang:BR/q/autoip.json
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }


    public GlobalTemperatureForecast getGlobalTemperatureForecast() {
        return globalTemperatureForecast;
    }

    public void setGlobalTemperatureForecast(GlobalTemperatureForecast globalTemperatureForecast) {
        this.globalTemperatureForecast = globalTemperatureForecast;
    }

    public interface GlobalTemperatureForecast{
        void onChangeForecast(int temperature, String icon, String summary);
    }

}
