package pt.ulisboa.tecnico.basa.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;
import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.manager.EventManager;
import pt.ulisboa.tecnico.basa.manager.TemperatureManager;
import pt.ulisboa.tecnico.basa.model.Event;
import pt.ulisboa.tecnico.basa.model.EventTemperature;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.util.ClapListener;
import pt.ulisboa.tecnico.basa.util.ModelCache;
import pt.ulisboa.tecnico.basa.util.SeekArc;
import pt.ulisboa.tecnico.basa.util.SeekCircle;


public class TemperatureFragment extends Fragment {


    View rootView;
    private TextView textTemperature;
    private InterestEventAssociation interest;
    private TextView mSeekArcProgress;
    private SeekArc mSeekArc;
    private ImageView image_temperature_mode;

      public TemperatureFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_temperature, container, false);

        textTemperature = (TextView)rootView.findViewById(R.id.textTemperature);
        textTemperature.setText("Waiting...: ");
        mSeekArcProgress = (TextView) rootView.findViewById(R.id.seekArcProgress);
        mSeekArc = (SeekArc) rootView.findViewById(R.id.seekArc);
        image_temperature_mode = (ImageView)rootView.findViewById(R.id.image_temperature_mode);

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
                mSeekArcProgress.setText(String.valueOf(progress));
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
                    textTemperature.setText("Temperature: " + ((EventTemperature)event).getTemperature());
                }
            }
        }, 0);
        ((MainActivity)getActivity()).getBasaManager().getEventManager().registerInterest(interest);
        ((MainActivity)getActivity()).getBasaManager().getTemperatureManager().addListenner(new TemperatureManager.ActionTemperatureManager() {
            @Override
            public void onTemperatureOutputChange(int change) {
                setUp(change);
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
        mSeekArc.setBackgroundColor(color);

        image_temperature_mode.setImageResource(resId);

    }



    @Override
    public void onPause(){
        super.onPause();
        ((MainActivity)getActivity()).getBasaManager().getEventManager().removeInterest(interest);
        interest = null;
    }



}
