package pt.ulisboa.tecnico.mybasaclient.ui;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.adapter.LightsAdapter;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.DeviceStatus;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.ChangeTemperatureLights;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.ChangeTemperatureLightsService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.GetDeviceStatusService;
import pt.ulisboa.tecnico.mybasaclient.util.DividerItemDecoration;
import pt.ulisboa.tecnico.mybasaclient.util.GenericCommunicationToFragment;


public class DeviceLightsFragment extends DialogFragment {
    View rootView;
    Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private LightsAdapter mAdapter;
    private BasaDevice device;
    private Button toggle_all;
    private Handler handler;
    private Runnable runnable;
    private GenericCommunicationToFragment listener;

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
        device = AppController.getInstance().getCurrentDevice();
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
        init();
        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();
        if (!AppController.getInstance().getLoggedUser().isEnableFirebase() || ((MainActivity)getActivity()).getmManager() == null) {
            handler.post(runnable);
        }
        ((MainActivity)getActivity()).addGenericCommunication(listener);
    }

    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);
        ((MainActivity)getActivity()).removeGenericCommunication(listener);
    }

    private void init(){

        listener = new GenericCommunicationToFragment() {
            @Override
            public void onDataChanged() {

                mAdapter.notifyDataSetChanged();

            }
        };
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                refreshLights();
                handler.postDelayed(this, 3000);
            }
        };
        toggle_all = (Button) rootView.findViewById(R.id.toggle_all);
        toggle_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLights();
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
            mAdapter = new LightsAdapter((MainActivity) getActivity(), device);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setOnLightChange(new LightsAdapter.OnLightChange() {
                @Override
                public void onChange(List<Boolean> bulbList) {

                    boolean allTurnedOn = true;
                    for(Boolean isOn : bulbList) {
                        if(!isOn)
                            allTurnedOn = false;

                    }

                    toggle_all.setText(allTurnedOn? "All off" : "All on");

                }
            });
        }
    }


    private void refreshLights(){
        if(getActivity() == null)
            return;

        new GetDeviceStatusService(device.getUrl(), new CallbackFromService<DeviceStatus, String>() {
            @Override
            public void success(DeviceStatus response) {


                if (device != null) {
                    int numLights = response.getLights().length;
                    device.setNumLights(numLights);
//                    AppController.getInstance().saveCurrentDevice(device);
                }

                if (getActivity() != null) {

                    boolean allTurnedOn = true;
                    device.getLights().clear();
                    for (boolean l : response.getLights()) {
                        device.getLights().add(l);
                        if (!l)
                            allTurnedOn = false;

                    }

                    toggle_all.setText(allTurnedOn ? "All off" : "All on");

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failed(String error) {

            }
        }).execute();

    }

    private void toggleLights() {
        boolean allTurnedOn = true;
        for (Boolean isOn : device.getLights()) {
            if (!isOn)
                allTurnedOn = false;
        }

        for (int i = 0; i < device.getLights().size(); i++)
            device.getLights().set(i, !allTurnedOn);

        if (AppController.getInstance().getLoggedUser().isEnableFirebase() && ((MainActivity)getActivity()).getmManager() != null) {
            Log.d("light", "firebase command:");
            ((MainActivity)getActivity()).getmManager().changeLights(device.getLights());

        } else {
            boolean[] tmp = new boolean[device.getLights().size()];
            for(int i = 0; i < device.getLights().size(); i++) tmp[i] = device.getLights().get(i);
            ChangeTemperatureLights changeTemperatureLights = new ChangeTemperatureLights(tmp, -80);
            new ChangeTemperatureLightsService(device.getUrl(), changeTemperatureLights, new CallbackFromService() {
                @Override
                public void success(Object response) {

                }

                @Override
                public void failed(Object error) {

                }
            }).execute();
            mAdapter.notifyDataSetChanged();

        }
        toggle_all.setText(!allTurnedOn ? "All off" : "All on");
    }


}
