package pt.ulisboa.tecnico.basa.model;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.model.weather.HourlyForecast;
import pt.ulisboa.tecnico.basa.util.ModelCache;

/**
 * Created by Sampaio on 26/04/2016.
 */
public class WeatherForecast {

    private List<HourlyForecast> hourly_forecast;

    public WeatherForecast() {
        hourly_forecast = new ArrayList<>();
    }

    public List<HourlyForecast> getHourly_forecast() {
        return hourly_forecast;
    }

    public void setHourly_forecast(List<HourlyForecast> hourly_forecast) {
        this.hourly_forecast = hourly_forecast;
    }

    public static WeatherForecast load(){
        WeatherForecast location = new ModelCache<WeatherForecast>().loadModel(new TypeToken<WeatherForecast>(){}.getType(), Global.OFFLINE_WEATHER);
        return location;
    }

    public void save(){
        new  ModelCache<WeatherForecast>().saveModel(this, Global.OFFLINE_WEATHER);
    }

    public boolean isUpToDate(){
        if(!hourly_forecast.isEmpty()){

            HourlyForecast lastOne = hourly_forecast.get(hourly_forecast.size()-1);

            Log.d("TemperatureManager", "lastOne getHour_padded:"+lastOne.getFCTTIME().getHour_padded());
            Log.d("TemperatureManager", "lastOne getMday_padded:"+lastOne.getFCTTIME().getMday_padded());
            Log.d("TemperatureManager", "lastOne getEpoch:"+lastOne.getFCTTIME().getEpoch());
            Log.d("TemperatureManager", "currentTimeMil():"+System.currentTimeMillis());
            Log.d("TemperatureManager", "diff():"+(System.currentTimeMillis()/1000 - lastOne.getFCTTIME().getEpoch()));
            return (System.currentTimeMillis()/1000 - lastOne.getFCTTIME().getEpoch() < 60*60);


        }
        return false;
    }

//    1461773681
//    1461776400
    public HourlyForecast getCurrent(){
        long currentTime = System.currentTimeMillis()/1000;
//        Log.d("web", "epoch:"+currentTime);
        for (HourlyForecast forecast: hourly_forecast){
            long oneHour = 60*60;
            long difference = currentTime - forecast.getFCTTIME().getEpoch() ;
//            Log.d("web", "getEpoch:"+forecast.getFCTTIME().getEpoch());
//            Log.d("web", "difference:"+difference + "      " + forecast.getFCTTIME().getHour_padded());
            if(difference> 0 && difference < oneHour){
                return forecast;
            }
        }
        if (hourly_forecast.size() > 0)
            return hourly_forecast.get(0);

        return null;
    }
}
