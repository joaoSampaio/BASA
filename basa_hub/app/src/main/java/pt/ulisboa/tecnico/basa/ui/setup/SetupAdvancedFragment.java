package pt.ulisboa.tecnico.basa.ui.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.model.registration.BasaDeviceLoad;


public class SetupAdvancedFragment extends Fragment implements View.OnClickListener{


    View rootView;
    EditText editTextBeacon, editTextMac;
    private static final int[] CLICK = {R.id.action_open_beacon, R.id.action_open_mac};

    public SetupAdvancedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_advanced, container, false);


        editTextBeacon = (EditText)rootView.findViewById(R.id.editTextBeacon);
        editTextMac = (EditText)rootView.findViewById(R.id.editTextMac);

        BasaDeviceLoad load = ((MainSetupActivity)getActivity()).getBasaDeviceLoad();
        BasaDeviceConfig conf = AppController.getInstance().getDeviceConfig();
        if(load != null) {
            editTextBeacon.setText(load.getBeaconList());
            editTextMac.setText(load.getMacList());
            conf.setBeaconList(getList(load.getBeaconList()));
            conf.setMacList(getList(load.getMacList()));
        }



        editTextBeacon.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count) {

                AppController.getInstance().getDeviceConfig().setBeaconList(getList(c.toString()));
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            public void afterTextChanged(Editable c) {}
        });

        editTextMac.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count) {

                AppController.getInstance().getDeviceConfig().setMacList(getList(c.toString()));
            }
            public void beforeTextChanged(CharSequence c, int start, int count, int after) {}
            public void afterTextChanged(Editable c) {}
        });

        for (int id: CLICK)
            rootView.findViewById(id).setOnClickListener(this);


        return rootView;
    }


    private List<String> getList(String data){
        List<String> list = new ArrayList<>();

        String[] words = data.split(",");
        for(String word : words){
            list.add(word.trim());
        }
        return list;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onResume(){
        super.onResume();


        }

        @Override
    public void onPause(){
        super.onPause();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_open_beacon:

                break;

            case R.id.action_open_mac:

                break;



        }
    }
}
