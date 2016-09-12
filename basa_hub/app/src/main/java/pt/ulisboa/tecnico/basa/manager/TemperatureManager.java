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
import pt.ulisboa.tecnico.basa.model.WeatherForecast;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventChangeTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.weather.HourlyForecast;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.Pojo.ArduinoChangeTemperature;
import pt.ulisboa.tecnico.basa.rest.services.ChangeTemperatureService;
import pt.ulisboa.tecnico.basa.rest.services.GetTemperatureListService;
import pt.ulisboa.tecnico.basa.rest.services.GetTemperatureOfficeService;
import pt.ulisboa.tecnico.basa.rest.Pojo.Temperature;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class TemperatureManager {

    public final static int COLD = 0;
    public final static int HEAT = 1;
    public final static int COLD_AND_HEAT = 2;
    WeatherForecast forecast;
    private List<ActionTemperatureManager> actionTemperatureManagerList;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private GlobalTemperatureForecast globalTemperatureForecast;
    SharedPreferences preferences;

    private int currentTemperature;
    private int currentHumidity;
    private long timeStartHvac = 0;
    private BasaManager basaManager;
    Handler handler;
    private long lastCheck = 0, lastHVACCmd = 0;
    private boolean calledUpdate = false;
    private String urlTemperature;
    private InterestEventAssociation interest;

    private int targetTemperature;

    public TemperatureManager(BasaManager basaManager){
        this.basaManager = basaManager;
        actionTemperatureManagerList = new ArrayList<>();
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


        forecast = WeatherForecast.load();
        if(forecast != null)
            Log.d("TemperatureManager", "forecast.isUpToDate():" + forecast.isUpToDate());

        if(forecast == null || !forecast.isUpToDate()){
            updateWeather();
        }


        getBasaManager().getEventManager().registerInterest(new InterestEventAssociation(Event.TIME, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {

                if(event instanceof EventTime){
                    EventTime time = (EventTime)event;
                    if((time.getDate() - lastCheck) > 45*60*1000)
                        lastCheck = time.getDate();
                    if(getGlobalTemperatureForecast() != null) {

                        if(forecast == null || !forecast.isUpToDate()){
                            if(forecast != null)
                                Log.d("TemperatureManager", "forecast.isUpToDate()222:" + forecast.isUpToDate());
                                updateWeather();
                            return;
                        }

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

        requestUpdateTemperature();

    }

    private void updateWeather(){
        calledUpdate = true;
        new GetTemperatureListService(new CallbackMultiple<WeatherForecast, Object>() {
            @Override
            public void success(WeatherForecast forecastWeb) {
                forecast = forecastWeb;
                forecast.save();
                if(forecast != null && forecast.getCurrent() != null){
                    HourlyForecast hourlyForecast = forecast.getCurrent();
                    if(hourlyForecast != null){
                        getGlobalTemperatureForecast().onChangeForecast(hourlyForecast.getTemp().getTemperature(), hourlyForecast.getIcon(), hourlyForecast.getCondition());
                    }
                }
                calledUpdate = false;
            }

            @Override
            public void failed(Object error) {
                calledUpdate = false;
            }
        }).execute();
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
        BasaDeviceConfig conf = AppController.getInstance().getDeviceConfig();
        if(conf.getTemperatureChoice() == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO ||
                conf.getTemperatureChoice() == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_BEACON) {
            urlTemperature = AppController.getInstance().getDeviceConfig().getArduinoIP();
            handler.post(new Runnable() {
                @Override
                public void run() {


                    BasaDeviceConfig conf = AppController.getInstance().getDeviceConfig();
                    if(conf.getTemperatureChoice() == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO) {

                        new GetTemperatureOfficeService(urlTemperature, new CallbackMultiple<Temperature, String>() {
                            @Override
                            public void success(Temperature response) {
                                if (response != null && getBasaManager() != null && response.isValid()) {
                                    setLatestTemperature((int)response.getTemperature());
                                    currentHumidity = (int)response.getHumidity();
                                    getBasaManager().getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, (int)response.getTemperature(), (int)response.getHumidity()));
                                    //latestTemperature = response;
                                }
                            }

                            @Override
                            public void failed(String error) {
                            }

                        }).execute();

                    } else if(conf.getTemperatureChoice() == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_BEACON){

                        if(AppController.getInstance().getTemperature() > -99) {
                            setLatestTemperature(AppController.getInstance().getTemperature());
                            getBasaManager().getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, AppController.getInstance().getTemperature(), -1));

                        }

                    }

                    handler.postDelayed(this, 30 * 1000);
                }
            });
        }
    }




    public void changeTargetTemperatureFromUI(int temperature){

        targetTemperature = temperature;
        AppController.getInstance().getBasaManager().getEventManager()
                .addEvent(new EventChangeTemperature(temperature));

        if(AppController.getInstance().getDeviceConfig().isFirebaseEnabled()) {
            FirebaseHelper mHelperFire = new FirebaseHelper();
            mHelperFire.changeTemperature(temperature);
        }
        handleTemperatureTarget();
    }

    public void onChangeTargetTemperatureFromClient(int temperature) {

        if(temperature != targetTemperature) {
            AppController.getInstance().getBasaManager().getEventManager()
                    .addEvent(new EventChangeTemperature(temperature));

            targetTemperature = temperature;
        }

        //TODO logic

        Log.d("tempera", "actionTemperatureManagerList:" + (actionTemperatureManagerList != null));
        if (actionTemperatureManagerList != null) {
            for (ActionTemperatureManager listenner : actionTemperatureManagerList) {
                listenner.onTargetTemperatureChange(temperature);
            }
        }
        handleTemperatureTarget();
    }

    private void handleTemperatureTarget(){

        //timeStartHvac


        ArduinoChangeTemperature arduinoChangeTemperature = new ArduinoChangeTemperature();
        if(currentTemperature > targetTemperature){

            arduinoChangeTemperature.turnOnCold(((currentTemperature - targetTemperature ) > 4) ? ArduinoChangeTemperature.SPEED3 : ArduinoChangeTemperature.SPEED1);


        }else if(currentTemperature < targetTemperature){

            arduinoChangeTemperature.turnOnHot(((targetTemperature - currentTemperature ) > 4) ? ArduinoChangeTemperature.SPEED3 : ArduinoChangeTemperature.SPEED1);

        }else {
            //stop hvac reached target temperature
            arduinoChangeTemperature.turnOff();

        }
        lastHVACCmd = System.currentTimeMillis();
        new ChangeTemperatureService(AppController.getInstance().getDeviceConfig().getArduinoIP(), arduinoChangeTemperature, new CallbackMultiple() {
            @Override
            public void success(Object response) {

            }

            @Override
            public void failed(Object error) {

            }
        }).execute();

    }

    private void handleCurrentTemperature(){

        if((System.currentTimeMillis() - lastHVACCmd) > 1*60*1000){

            if(currentTemperature > targetTemperature){
                targetTemperature++;
            } else if(currentTemperature < targetTemperature){
                targetTemperature--;
            }
            onChangeTargetTemperatureFromClient(targetTemperature);
        }


        //time: every 10m


    }


    public void setLatestTemperature(int latestTemperature) {
        Log.d("servico", "setLatestTemperature:" + latestTemperature);
        this.currentTemperature = latestTemperature;

        if(AppController.getInstance().getDeviceConfig().isFirebaseEnabled()) {
            FirebaseHelper mHelperFire = new FirebaseHelper();
            mHelperFire.setLatestTemperature(latestTemperature);
        }
        handleCurrentTemperature();
    }

    public int getCurrentHumidity() {
        return currentHumidity;
    }

    public int getCurrentTemperature() {
        return currentTemperature;
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
