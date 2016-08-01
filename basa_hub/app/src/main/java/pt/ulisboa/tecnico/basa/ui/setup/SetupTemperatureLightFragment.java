package pt.ulisboa.tecnico.basa.ui.setup;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;


public class SetupTemperatureLightFragment extends Fragment{


    View rootView;
    EditText editTextLight, editTextArduino, editTextBeacon;
    private Spinner spinnerTemperature;
    private BasaDeviceConfig conf;
    private Handler handler;
    private String newNamespace;

    public SetupTemperatureLightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_light_temperature, container, false);
        spinnerTemperature = (Spinner)rootView.findViewById(R.id.spinnerTemperature);

        editTextLight = (EditText)rootView.findViewById(R.id.editTextLight);
        editTextArduino = (EditText)rootView.findViewById(R.id.editTextArduino);
        editTextBeacon = (EditText)rootView.findViewById(R.id.editTextBeacon);
        editTextArduino.setVisibility(View.GONE);
        editTextBeacon.setVisibility(View.GONE);
        conf = AppController.getInstance().getDeviceConfig();
        handler = new Handler();






        //conf.setTemperatureChoice(BasaDeviceConfig.TEMPERATURE_TYPE_NO_MONITOR_CONTROL);

//        editTextLight.setText("ZH037CC7097B7CA91");
//        AppController.getInstance().getDeviceConfig().setEdupLightId("ZH037CC7097B7CA91");
//
//        editTextBeacon.setText("2a11a5a1111111111111");
//        AppController.getInstance().getDeviceConfig().setBeaconUuidTemperature("2a11a5a1111111111111");


        spinnerTemperature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conf.setTemperatureChoice(position);

                editTextBeacon.setVisibility((position == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_BEACON)? View.VISIBLE : View.GONE);
                editTextArduino.setVisibility((position == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_CONTROL_ARDUINO)? View.VISIBLE : View.GONE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        editTextLight.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                AppController.getInstance().getDeviceConfig().setEdupLightId(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        editTextArduino.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                AppController.getInstance().getDeviceConfig().setArduinoIP(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        editTextBeacon.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                newNamespace = new String(c.toString().toLowerCase());
                final String namespace = new String(c.toString().toLowerCase());
                editTextBeacon.setError(null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (newNamespace.equals(namespace) && !namespace.isEmpty()) {

                            Log.d("main", " namespace.length():" + namespace.length());
                            boolean isValid = namespace.matches("[0-9a-f]+") && namespace.length() == 20;
                            if(!isValid) {
                                editTextBeacon.setError("Namespace should have 20 characters(A to F and 0 to 9");
                            }else
                                AppController.getInstance().getDeviceConfig().setBeaconUuidTemperature(namespace);
                        }
                    }
                }, 1500);
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
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
        spinnerTemperature.setSelection(conf.getTemperatureChoice());
        editTextArduino.setText(conf.getArduinoIP());
        editTextBeacon.setText(conf.getBeaconUuidTemperature());
        editTextLight.setText(conf.getEdupLightId());

    }

        @Override
    public void onPause(){
        super.onPause();
    }




}
