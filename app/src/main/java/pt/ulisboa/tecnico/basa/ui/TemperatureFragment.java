package pt.ulisboa.tecnico.basa.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.estimote.sdk.SystemRequirementsChecker;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.manager.EventManager;
import pt.ulisboa.tecnico.basa.model.Event;
import pt.ulisboa.tecnico.basa.model.EventTemperature;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.util.ClapListener;


public class TemperatureFragment extends Fragment {


    View rootView;
    private TextView textTemperature;
    private InterestEventAssociation interest;

    private TextView txtSpeechInput;
    private Button btnSpeak;
    private EditText sensitivity, threshold;
    ClapListener clap;

    public TemperatureFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_temperature, container, false);

        textTemperature = (TextView)rootView.findViewById(R.id.textTemperature);
        textTemperature.setText("Waiting...: ");

        threshold = (EditText)rootView.findViewById(R.id.threshold);
        sensitivity = (EditText)rootView.findViewById(R.id.sensitivity);

        txtSpeechInput = (TextView) rootView.findViewById(R.id.txtSpeechInput);
        btnSpeak = (Button) rootView.findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                double threshold_app = Double.parseDouble(threshold.getText().toString());
                double sensitivity_app = Double.parseDouble(sensitivity.getText().toString());
                clap.setUpNew(sensitivity_app, threshold_app);
                //((MainActivity) getActivity()).promptSpeechInput();
            }
        });

        //clap = new ClapListener();
        return rootView;
    }







    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onResume(){
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(getActivity());


        interest = new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {
                if(event instanceof EventTemperature){
                    textTemperature.setText("Temperature: " + ((EventTemperature)event).getTemperature());
                }
            }
        }, 0);
        ((MainActivity)getActivity()).getEventManager().registerInterest(interest);


    }



    @Override
    public void onPause(){
        super.onPause();
        ((MainActivity)getActivity()).getEventManager().removeInterest(interest);
        interest = null;
    }



}
