package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.model.BasaLocation;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.WeatherForecast;
import pt.ulisboa.tecnico.basa.model.weather.HourlyForecast;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.GetTemperatureListService;
import pt.ulisboa.tecnico.basa.rest.GetTemperatureOfficeService;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class TemperatureManager {

    public final static int COLD = 0;
    public final static int HEAT = 1;
    public final static int COLD_AND_HEAT = 2;
    private List<ActionTemperatureManager> actionTemperatureManagerList;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private GlobalTemperatureForecast globalTemperatureForecast;
    SharedPreferences preferences;
    private Temperature latestTemperature;
    private Launch2Activity activity;
    Handler handler;
    private String urlTemperature;
    private InterestEventAssociation interest;

    public TemperatureManager(Launch2Activity ctx){
        this.activity = ctx;
        actionTemperatureManagerList = new ArrayList<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        handler = new Handler();

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
//        updateLocation();

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


        interest = new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {
                if(event instanceof EventTemperature){
                    double temperature = ((EventTemperature)event).getTemperature();
                    Log.d("servico", "latest temp:" + temperature);

                    setLatestTemperature(new Temperature(temperature, -1));

                }
            }
        }, 0);

        Log.d("servico", "TemperatureManager:" + (getActivity().getBasaManager().getEventManager() != null));
        if((getActivity()).getBasaManager().getEventManager() != null)
            getActivity().getBasaManager().getEventManager().registerInterest(interest);



        requestUpdateTemperature();



    }

    public interface ActionTemperatureManager{
        void onTemperatureOutputChange(int change);

        void onTargetTemperatureChange(int temperature);
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
        if(handler != null)
            handler.removeCallbacksAndMessages(null);

        if(((Launch2Activity)getActivity()).getBasaManager().getEventManager() != null)
            ((Launch2Activity)getActivity()).getBasaManager().getEventManager().removeInterest(interest);
        interest = null;


        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        this.activity = null;
    }

    public Launch2Activity getActivity() {
        return activity;
    }

    public void requestUpdateTemperature(){

        urlTemperature = preferences.getString(Global.OFFLINE_IP_TEMPERATURE, "");
        handler.removeCallbacksAndMessages(null);

        handler.post(new Runnable() {
            @Override
            public void run() {
                new GetTemperatureOfficeService(urlTemperature, new CallbackMultiple<Temperature, String>() {
                    @Override
                    public void success(Temperature response) {
                        if(response != null && activity != null && response.isValid()){
                            activity.getBasaManager().getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, response.getTemperature(), response.getHumidity()));
                            //latestTemperature = response;
                        }
                    }

                    @Override
                    public void failed(String error) {

                    }

                }).execute();
                handler.postDelayed(this, 30 * 1000);
            }
        });
    }

    public void changeTargetTemperature(int temperature){

        //TODO logic


        for (ActionTemperatureManager listenner: actionTemperatureManagerList) {
            listenner.onTargetTemperatureChange(temperature);
        }
    }


    private void updateLocation(){


        new GetTemperatureListService(new CallbackMultiple<WeatherForecast, String>() {
            @Override
            public void success(WeatherForecast forecast) {
                Log.d("web", "Deu:");

                HourlyForecast hourlyOld = null;
                WeatherForecast old = WeatherForecast.load();
                if(old != null)
                    hourlyOld = old.getCurrent();
                if(hourlyOld != null){

                    forecast.getHourly_forecast().add(0, hourlyOld);
                }

                forecast.save();
                if(forecast != null && forecast.getCurrent() != null){
                    HourlyForecast hourlyForecast = forecast.getCurrent();

                    if(hourlyForecast != null && getGlobalTemperatureForecast() != null){
                        getGlobalTemperatureForecast().onChangeForecast(hourlyForecast.getTemp().getTemperature(), hourlyForecast.getIcon(), hourlyForecast.getCondition());
                    }
                }
            }

            @Override
            public void failed(String error) {
                Log.d("web", "Fail:");
            }
        }).execute();


    }

    private void getLongitude(){
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                Log.d("updateLocation", "getLatitude:" + location.getLatitude());
                Log.d("updateLocation", "getLongitude:" + location.getLongitude());
                BasaLocation basaLocation = new BasaLocation(location.getLatitude(), location.getLongitude());
                basaLocation.save();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public void setLatestTemperature(Temperature latestTemperature) {
        Log.d("servico", "setLatestTemperature:" + (latestTemperature != null));
        this.latestTemperature = latestTemperature;
    }

    public Temperature getLatestTemperature() {
        return latestTemperature;
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
