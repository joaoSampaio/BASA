package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.manager.TemperatureManager;
import pt.ulisboa.tecnico.basa.util.ModelCache;


public class SettingsTemperatureFragment extends DialogFragment {

    private View rootView;
    private RadioGroup radioGroupTemperature;


    public SettingsTemperatureFragment() {
        // Required empty public constructor
    }



    public static SettingsTemperatureFragment newInstance() {
        SettingsTemperatureFragment fragment = new SettingsTemperatureFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings_temp, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){



        radioGroupTemperature = (RadioGroup)rootView.findViewById(R.id.radioGroupTemperature);




        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);

        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        textViewDescription.setText("Temperature Settings");
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        radioGroupTemperature.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                int type;
                if (checkedId == R.id.radioCold) {
                    type = TemperatureManager.COLD;
                } else if (checkedId == R.id.radioHeat) {
                    type = TemperatureManager.HEAT;
                } else {
                    type = TemperatureManager.COLD_AND_HEAT;
                }

                new ModelCache<Integer>().saveModel(type, Global.OFFLINE_TEMPERATURE_OUTPUT);

            }

        });


    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
