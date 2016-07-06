package pt.ulisboa.tecnico.basa.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.manager.EventManager;
import pt.ulisboa.tecnico.basa.manager.TemperatureManager;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.util.ModelCache;
import pt.ulisboa.tecnico.basa.util.SeekArc;


public class TemperatureFragment extends Fragment {


    View rootView;
    private TextView textTemperature, forecastTemp, forecastSummary;
    private InterestEventAssociation interest;
    private SeekArc mSeekArc;
    private ImageView image_temperature_mode, imageForecast;
    private View action_increase, action_decrease;
      public TemperatureFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_temperature, container, false);

        textTemperature = (TextView)rootView.findViewById(R.id.textTemperature);
        textTemperature.setText("Waiting...: ");
        mSeekArc = (SeekArc) rootView.findViewById(R.id.seekArc);
        image_temperature_mode = (ImageView)rootView.findViewById(R.id.image_temperature_mode);
        forecastTemp = (TextView) rootView.findViewById(R.id.forecastTemp);
        forecastSummary = (TextView) rootView.findViewById(R.id.forecastSummary);
        imageForecast = (ImageView) rootView.findViewById(R.id.imageForecast);
        action_increase = rootView.findViewById(R.id.action_increase);
        action_decrease = rootView.findViewById(R.id.action_decrease);
        action_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekArc.setProgress(mSeekArc.getProgress() + 1);
            }
        });
        action_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekArc.setProgress(mSeekArc.getProgress() - 1);
            }
        });


        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onProgressChanged(SeekArc seekArc, int progress,
                                          boolean fromUser) {
                //mSeekArcProgress.setText(String.valueOf(progress));
            }
        });

        return rootView;
    }







    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onResume(){
        super.onResume();

        setUp(-1);
        interest = new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {
                if(event instanceof EventTemperature){
                    double temperature = ((EventTemperature)event).getTemperature();
                    textTemperature.setText("" + temperature);

                    int color = (temperature < 18)? Global.COLOR_COLD : Global.COLOR_HEAT;
                    mSeekArc.setBackgroundColor(color);


                }
            }
        }, 0);
        if(((MainActivity)getActivity()).getBasaManager().getEventManager() != null)
            ((MainActivity)getActivity()).getBasaManager().getEventManager().registerInterest(interest);

        if(((MainActivity)getActivity()).getBasaManager().getTemperatureManager() != null)
            ((MainActivity)getActivity()).getBasaManager().getTemperatureManager().addListenner(new TemperatureManager.ActionTemperatureManager() {
                @Override
                public void onTemperatureOutputChange(int change) {
                    setUp(change);
                }
            });
        if(((MainActivity)getActivity()).getBasaManager().getTemperatureManager() != null)
            ((MainActivity)getActivity()).getBasaManager().getTemperatureManager().setGlobalTemperatureForecast(new TemperatureManager.GlobalTemperatureForecast() {
                @Override
                public void onChangeForecast(int temperature, String icon, String summary) {
                    forecastTemp.setText(""+temperature+"º");
                    forecastSummary.setText(summary);
                    imageForecast.setImageResource(getIcon(icon));


            }
        });
    }



    private void setUp(int change){

        if(change < 0)
            change = new ModelCache<Integer>().loadModel(new TypeToken<Integer>(){}.getType(), Global.OFFLINE_TEMPERATURE_OUTPUT, "0");

        int color, resId;
        if (change == TemperatureManager.COLD) {
            color = Global.COLOR_COLD;
            resId = R.drawable.ic_snowflake;
        }
        else if (change == TemperatureManager.HEAT) {
            color = Global.COLOR_HEAT;
            resId = R.drawable.ic_fire;
        }
        else {
            color = Global.COLOR_HEAT;
            resId = R.drawable.ic_fire;
        }
        //mSeekArc.setBackgroundColor(color);

        image_temperature_mode.setImageResource(resId);

    }



    @Override
    public void onPause(){
        super.onPause();
        if(((MainActivity)getActivity()).getBasaManager().getEventManager() != null)
            ((MainActivity)getActivity()).getBasaManager().getEventManager().removeInterest(interest);
        interest = null;
    }

    private int getIcon(String iconString){
        int resId;
        switch (iconString){
            case "chanceflurries":
                resId = R.drawable.ic_weather_snow;
                break;
            case "chancesnow":
                resId = R.drawable.ic_weather_snow;
                break;
            case "flurries":
                resId = R.drawable.ic_weather_snow;
                break;
            case "snow":
                resId = R.drawable.ic_weather_snow;
                break;
            case "nt_chanceflurries":
                resId = R.drawable.ic_weather_snow;
                break;
            case "nt_chancesnow":
                resId = R.drawable.ic_weather_snow;
                break;
            case "nt_flurries":
                resId = R.drawable.ic_weather_snow;
                break;
            case "nt_snow":
                resId = R.drawable.ic_weather_snow;
                break;

            case "chancesleet":
                resId = R.drawable.ic_weather_sleet;
                break;
            case "sleet":
                resId = R.drawable.ic_weather_sleet;
                break;
            case "nt_chancesleet":
                resId = R.drawable.ic_weather_sleet;
                break;
            case "nt_sleet":
                resId = R.drawable.ic_weather_sleet;
                break;

            case "chancerain":
                resId = R.drawable.ic_weather_rain;
                break;
            case "rain":
                resId = R.drawable.ic_weather_rain;
                break;
            case "nt_chancerain":
                resId = R.drawable.ic_weather_rain;
                break;
            case "nt_rain":
                resId = R.drawable.ic_weather_rain;
                break;

            case "clear":
                resId = R.drawable.ic_weather_sun;
                break;
            case "sunny":
                resId = R.drawable.ic_weather_sun;
                break;

            case "chancetstorms":
                resId = R.drawable.ic_weather_storm;
                break;
            case "tstorms":
                resId = R.drawable.ic_weather_storm;
                break;
            case "nt_chancetstorms":
                resId = R.drawable.ic_weather_storm;
                break;
            case "nt_tstorms":
                resId = R.drawable.ic_weather_storm;
                break;

            case "cloudy":
                resId = R.drawable.ic_weather_cloud;
                break;
            case "nt_cloudy":
                resId = R.drawable.ic_weather_cloud;
                break;

            case "mostlycloudy":
                resId = R.drawable.ic_weather_cloud_sun;
                break;
            case "mostlysunny":
                resId = R.drawable.ic_weather_cloud_sun;
                break;
            case "partlycloudy":
                resId = R.drawable.ic_weather_cloud_sun;
                break;
            case "partlysunny":
                resId = R.drawable.ic_weather_cloud_sun;
                break;

            case "nt_clear":
                resId = R.drawable.ic_weather_night_clear;
                break;
            case "nt_sunny":
                resId = R.drawable.ic_weather_night_clear;
                break;

            case "nt_mostlycloudy":
                resId = R.drawable.ic_weather_cloud_moon;
                break;
            case "nt_mostlysunny":
                resId = R.drawable.ic_weather_cloud_moon;
                break;
            case "nt_partlycloudy":
                resId = R.drawable.ic_weather_cloud_moon;
                break;
            case "nt_partlysunny":
                resId = R.drawable.ic_weather_cloud_moon;
                break;

            case "fog":
                resId = R.drawable.ic_weather_fog;
                break;
            case "hazy":
                resId = R.drawable.ic_weather_fog;
                break;
            case "nt_fog":
                resId = R.drawable.ic_weather_fog;
                break;
            case "nt_hazy":
                resId = R.drawable.ic_weather_fog;
                break;

            default:
                resId = R.drawable.ic_weather_default;
                break;

        }
        return resId;
    }



}
