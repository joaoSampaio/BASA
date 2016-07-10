package pt.ulisboa.tecnico.mybasaclient.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.ChangeTemperatureLights;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.ChangeTemperatureLightsService;
import pt.ulisboa.tecnico.mybasaclient.util.SeekArc;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceTemperatureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceTemperatureFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    SeekArc mSeekArc;
    private BasaDevice device;

    public DeviceTemperatureFragment() {
        // Required empty public constructor
    }


    public static DeviceTemperatureFragment newInstance() {
        DeviceTemperatureFragment fragment = new DeviceTemperatureFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_device_temperature, container, false);
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

        init();
        return rootView;
    }

    private void init(){

        mSeekArc = (SeekArc) rootView.findViewById(R.id.seekArc);
        mSeekArc.setCurrentTemperature(device.getLatestTemperature() + "");

        mSeekArc.setBackgroundColor((device.getLatestTemperature() >= 18)? Global.COLOR_HEAT : Global.COLOR_COLD);

        View settings = rootView.findViewById(R.id.action_settings);
        settings.setVisibility(View.VISIBLE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).openPage(Global.DIALOG_DEVICE_SETTINGS);
                Toast.makeText(getActivity(), "settings", Toast.LENGTH_SHORT).show();
            }
        });


        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

//                if(fromUser){
//                    Log.d("arc", "progress:"+progress);
//                }

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

                new ChangeTemperatureLightsService(device.getUrl(), new ChangeTemperatureLights(new boolean[0], seekArc.getProgress()), new CallbackFromService() {
                    @Override
                    public void success(Object response) {
                        Toast.makeText(AppController.getAppContext(), "Temperature changed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failed(Object error) {
                        Toast.makeText(AppController.getAppContext(), "Cound not change temperature", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
                Log.d("arc", "onStopTrackingTouch:"+seekArc.getProgress());
            }
        });
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
