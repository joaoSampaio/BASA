package pt.ulisboa.tecnico.mybasaclient.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.adapter.LightsAdapter;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.DeviceStatus;
import pt.ulisboa.tecnico.mybasaclient.model.LightBulb;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.ChangeTemperatureLights;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.ChangeTemperatureLightsService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.GetDeviceStatusService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceLightsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceLightsFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LightsAdapter mAdapter;
    private List<LightBulb> lights;
    private BasaDevice device;
    private Button toggle_all;

    public DeviceLightsFragment() {
        // Required empty public constructor
    }


    public static DeviceLightsFragment newInstance() {
        DeviceLightsFragment fragment = new DeviceLightsFragment();
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
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_device_lights, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        device = BasaDevice.getCurrentDevice();
        if (toolbar!=null) {

            toolbar.setTitle(device.getName());
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(getDialog() != null)
                        getDialog().dismiss();
                }
            });
        }
        lights = new ArrayList<>();
        init();
        return rootView;
    }

    private void init(){

        toggle_all = (Button) rootView.findViewById(R.id.toggle_all);
        toggle_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allTurnedOn = true;
                for(LightBulb l : lights) {
                    if(!l.isOn())
                        allTurnedOn = false;

                }



                for(LightBulb lightBulb : lights){
                    lightBulb.setState(!allTurnedOn);
                }

                ChangeTemperatureLights changeTemperatureLights = new ChangeTemperatureLights(LightBulb.getArray(lights), -80);
                new ChangeTemperatureLightsService(device.getUrl(), changeTemperatureLights, new CallbackFromService() {
                    @Override
                    public void success(Object response) {

                    }

                    @Override
                    public void failed(Object error) {

                    }
                }).execute();
                mAdapter.notifyDataSetChanged();
                toggle_all.setText(!allTurnedOn? "All off" : "All on");



            }
        });


        View settings = rootView.findViewById(R.id.action_settings);
        settings.setVisibility(View.VISIBLE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).openPage(Global.DIALOG_DEVICE_SETTINGS);
            }
        });

        if(mRecyclerView == null) {
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new LightsAdapter((MainActivity) getActivity(), lights, device);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnLightChange(new LightsAdapter.OnLightChange() {
                @Override
                public void onChange(List<LightBulb> bulbList) {

                    boolean allTurnedOn = true;
                    for(LightBulb l : bulbList) {
                        if(!l.isOn())
                            allTurnedOn = false;

                    }

                    toggle_all.setText(allTurnedOn? "All off" : "All on");

                }
            });
        }
//        mAdapter.notifyDataSetChanged();


        new GetDeviceStatusService(device.getUrl(), new CallbackFromService<DeviceStatus, String>() {
            @Override
            public void success(DeviceStatus response) {


                if(device != null){
                    int numLights = response.getLights().length;
                    device.setNumLights(numLights);
                    Zone.updateCurrentZone(device);
                    BasaDevice.saveCurrentDevice(device);

                }

                if(getActivity() != null){

                    boolean allTurnedOn = true;
                    lights.clear();
                    for(boolean l : response.getLights()) {
                        lights.add(new LightBulb(l));
                        if(!l)
                            allTurnedOn = false;

                    }

                    toggle_all.setText(allTurnedOn? "All off" : "All on");

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failed(String error) {

            }
        }).execute();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zone_info:
                ((MainActivity)getActivity()).openPage(Global.DIALOG_SETTINGS_ZONE_INFO);
                break;
            case R.id.add_device:

                break;
            case R.id.remove_zone:

                break;

        }
    }
}
