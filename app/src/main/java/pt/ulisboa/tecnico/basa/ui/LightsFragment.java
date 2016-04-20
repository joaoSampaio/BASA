package pt.ulisboa.tecnico.basa.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.manager.LightingManager;
import pt.ulisboa.tecnico.basa.model.EventCustomSwitchPressed;


public class LightsFragment extends Fragment implements View.OnClickListener {


    View rootView;
    private int numLights, numCustomSwitches;
    private LinearLayout containerLightSwitch, containerCustomSwitch;
    private Map<Integer, Integer> lightsIds;
    private Map<Integer, Integer> customSwitchesIds;

    public LightsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lights, container, false);
        containerLightSwitch = (LinearLayout)rootView.findViewById(R.id.containerLightSwitch);
        containerCustomSwitch = (LinearLayout)rootView.findViewById(R.id.containerCustomSwitch);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onResume(){
        super.onResume();
        ((MainActivity)getActivity()).getLightingManager().setLightChangedListener(new LightingManager.LightChanged() {
            @Override
            public void onLightON(final int lightId) {
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run() {
                        if (lightsIds.containsKey(lightId)) {
                            int viewId = lightsIds.get(lightId);
                            View state = rootView.findViewById(viewId);
                            if (state != null) {
                                ((ImageView) state).setImageResource(R.drawable.power_button);
                            }
                        }
                    }
                });
            }

            @Override
            public void onLightOFF(final int lightId) {
                Handler refresh = new Handler(Looper.getMainLooper());
                refresh.post(new Runnable() {
                    public void run() {
                        if (lightsIds.containsKey(lightId)) {
                            int viewId = lightsIds.get(lightId);
                            View state = rootView.findViewById(viewId);
                            if (state != null) {
                                ((ImageView) state).setImageResource(R.drawable.power_button_off);
                            }
                        }
                    }
                });
            }
        });
        refreshLightLayout();

        }

        @Override
    public void onPause(){
        super.onPause();
        ((MainActivity)getActivity()).getLightingManager().setLightChangedListener(null);
    }


    private void refreshLightLayout(){
        lightsIds = new TreeMap<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        numLights = Integer.parseInt(preferences.getString("light_number", "1"));
        containerLightSwitch.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        int viewId;
        for (int id=0; id< numLights; id++) {
            View view  = inflater.inflate(R.layout.layout_single_light_switch, containerLightSwitch, true);

            View btn_light = view.findViewById(R.id.btn_light);
            View img_light_state = view.findViewById(R.id.img_light_state);

            viewId = View.generateViewId();
            btn_light.setId(viewId);
            final int lightId = id;
            Log.d("app", "light id:" + id);
            Log.d("app", "light viewId:"+viewId);

            viewId = View.generateViewId();
            img_light_state.setId(viewId);
            lightsIds.put(id, img_light_state.getId());
            btn_light.setTag(img_light_state);

            if(((MainActivity)getActivity()).getLightingManager() != null &&  ((MainActivity)getActivity()).getLightingManager().getLightState(id)){
                ((ImageView) img_light_state).setImageResource(R.drawable.power_button);
            }
            //containerSwitch.addView(view);
            btn_light.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("EVENT", "onclick2:");
                    ((MainActivity) getActivity()).getLightingManager().toggleLight(lightId);
                }
            });
        }


        ////////*****///////
        customSwitchesIds = new TreeMap<>();
        numCustomSwitches = Integer.parseInt(preferences.getString("custom_button_number", "0"));
        containerCustomSwitch.removeAllViews();
        for (int id=0; id< numCustomSwitches; id++) {
            View view  = inflater.inflate(R.layout.layout_single_custom_switch, containerCustomSwitch, true);

            View btn_light = view.findViewById(R.id.btn_light);
            // set item content in view
            viewId = View.generateViewId();
            btn_light.setId(viewId);
            final int lightId = id;

            //containerSwitch.addView(view);
            btn_light.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("EVENT", "onclick2:");
                    ((MainActivity) getActivity()).getEventManager().addEvent(new EventCustomSwitchPressed(lightId));
                }
            });
        }




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
        Log.d("EVENT","onclick");
        if(lightsIds != null){
            if(lightsIds.containsKey(v.getId())){
                int lightId = lightsIds.get(v.getId());

                ((MainActivity)getActivity()).getEventManager().addEvent(new EventCustomSwitchPressed(lightId));

            }
        }
    }

//    public interface SwitchLight{
//        void turnOnLight(int id);
//        void turnOffLight(int id);
//    }

}
