package pt.ulisboa.tecnico.basa.manager;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.WeatherForecast;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.weather.HourlyForecast;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.GetTemperatureListService;
import pt.ulisboa.tecnico.basa.rest.GetTemperatureOfficeService;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;
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
    private BasaManager basaManager;
    Handler handler;
    private String urlTemperature;
    private InterestEventAssociation interest;

    private int targetTemperature;

    public TemperatureManager(BasaManager basaManager){
        this.basaManager = basaManager;
        actionTemperatureManagerList = new ArrayList<>();
        Log.d("tempera", "TemperatureManager:"+(actionTemperatureManagerList != null));
        preferences = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
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

        getBasaManager().getEventManager().registerInterest(new InterestEventAssociation(Event.TIME, new EventManager.RegisterInterestEvent() {
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

        Log.d("servico", "TemperatureManager:" + (getBasaManager().getEventManager() != null));
        if(getBasaManager().getEventManager() != null) {
            Log.d("servico", "TemperatureManager register:" );
            getBasaManager().getEventManager().registerInterest(interest);

        }



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
        Log.d("tempera", "TemperatureManager destroy:"+(actionTemperatureManagerList != null));
        if (actionTemperatureManagerList != null) {
            actionTemperatureManagerList.clear();
            actionTemperatureManagerList = null;
        }
        if(handler != null)
            handler.removeCallbacksAndMessages(null);

        if(getBasaManager().getEventManager() != null)
            getBasaManager().getEventManager().removeInterest(interest);
        interest = null;


        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        this.basaManager = null;
    }


    public BasaManager getBasaManager() {
        return basaManager;
    }

    public void requestUpdateTemperature(){

//        urlTemperature = preferences.getString(Global.OFFLINE_IP_TEMPERATURE, "");

        handler.removeCallbacksAndMessages(null);

        if(AppController.getInstance().getDeviceConfig().getTemperatureChoice() == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO) {
            urlTemperature = AppController.getInstance().getDeviceConfig().getArduinoIP();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    new GetTemperatureOfficeService(urlTemperature, new CallbackMultiple<Temperature, String>() {
                        @Override
                        public void success(Temperature response) {
                            if (response != null && getBasaManager() != null && response.isValid()) {
                                getBasaManager().getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, response.getTemperature(), response.getHumidity()));
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
    }




    public void changeTargetTemperature(int temperature){
        if(BasaDeviceConfig.getConfig().isFirebaseEnabled()) {
            FirebaseHelper mHelperFire = new FirebaseHelper();
            mHelperFire.changeTemperature(temperature);
        }
    }

    public void onChangeTargetTemperature(int temperature) {

        //TODO logic
        Log.d("tempera", "actionTemperatureManagerList:" + (actionTemperatureManagerList != null));
        if (actionTemperatureManagerList != null) {
            for (ActionTemperatureManager listenner : actionTemperatureManagerList) {
                listenner.onTargetTemperatureChange(temperature);
            }
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



    public void setLatestTemperature(Temperature latestTemperature) {
        Log.d("servico", "setLatestTemperature:" + (latestTemperature != null));
        this.latestTemperature = latestTemperature;

        if(BasaDeviceConfig.getConfig().isFirebaseEnabled()) {
            FirebaseHelper mHelperFire = new FirebaseHelper();
            mHelperFire.setLatestTemperature((int)latestTemperature.getTemperature());
        }

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
