package pt.ulisboa.tecnico.basa.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.manager.EventManager;
import pt.ulisboa.tecnico.basa.manager.TemperatureManager;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.util.SeekArc;


public class TemperatureFragment extends Fragment {


    View rootView;
    private TextView forecastTemp, forecastSummary;
    private InterestEventAssociation interest;
    private SeekArc mSeekArc;
    private ImageView imageForecast;
    private View action_increase, action_decrease;
      public TemperatureFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_temperature, container, false);

        mSeekArc = (SeekArc) rootView.findViewById(R.id.seekArc);
        mSeekArc.setCurrentTemperature("Waiting...: ");

        forecastTemp = (TextView) rootView.findViewById(R.id.forecastTemp);
        forecastSummary = (TextView) rootView.findViewById(R.id.forecastSummary);
        imageForecast = (ImageView) rootView.findViewById(R.id.imageForecast);
//        action_increase = rootView.findViewById(R.id.action_increase);
//        action_decrease = rootView.findViewById(R.id.action_decrease);
//        action_increase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSeekArc.setProgress(mSeekArc.getProgress() + 1);
//            }
//        });
//        action_decrease.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSeekArc.setProgress(mSeekArc.getProgress() - 1);
//            }
//        });


        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                AppController.getInstance().getBasaManager().getTemperatureManager().changeTargetTemperatureFromUI(seekArc.getProgress());
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

        setEcoTemperatureRange();

        return rootView;
    }



    private void setEcoTemperatureRange(){
        mSeekArc.setLeafLimit(20, 26);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onResume(){
        super.onResume();

        interest = new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {
                if(event instanceof EventTemperature){

                    double temperature = ((EventTemperature)event).getTemperature();
                    Log.d("servico", "latest temp frag:" + temperature);
//                    textTemperature.setText("" + temperature);
                    mSeekArc.setCurrentTemperature("" + (int)temperature);
                    int color = (temperature < 18)? Global.COLOR_COLD : Global.COLOR_HEAT;
                    mSeekArc.setBackgroundColor(color);


                }
            }
        }, 0);
        if(((Launch2Activity)getActivity()).getBasaManager().getEventManager() != null)
            ((Launch2Activity)getActivity()).getBasaManager().getEventManager().registerInterest(interest);

        if(((Launch2Activity)getActivity()).getBasaManager().getTemperatureManager() != null)
            ((Launch2Activity)getActivity()).getBasaManager().getTemperatureManager().addListenner(new TemperatureManager.ActionTemperatureManager() {
                @Override
                public void onTemperatureOutputChange(int change) {
                    //setUp(change);
                }

                @Override
                public void onTargetTemperatureChange(int temperature) {
                    mSeekArc.setProgress(temperature);
                }
            });
        if(((Launch2Activity)getActivity()).getBasaManager().getTemperatureManager() != null)
            ((Launch2Activity)getActivity()).getBasaManager().getTemperatureManager().setGlobalTemperatureForecast(new TemperatureManager.GlobalTemperatureForecast() {
                @Override
                public void onChangeForecast(int temperature, String icon, String summary) {
                    forecastTemp.setText(""+temperature+"º");
                    forecastSummary.setText(summary);
                    imageForecast.setImageResource(getIcon(icon));


            }
        });
    }


    @Override
    public void onPause(){
        super.onPause();
        if(((Launch2Activity)getActivity()).getBasaManager().getEventManager() != null)
            ((Launch2Activity)getActivity()).getBasaManager().getEventManager().removeInterest(interest);
        interest = null;
    }

    private int getIcon(String iconString){
        int resId;
        Log.d("icon temp", "icon:" + iconString);
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
