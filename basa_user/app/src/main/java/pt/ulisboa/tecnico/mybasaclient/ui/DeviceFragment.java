package pt.ulisboa.tecnico.mybasaclient.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    private BasaDevice device;
    private final static int[] CLICK = {R.id.zone_info, R.id.add_device, R.id.remove_zone};

    public DeviceFragment() {
        // Required empty public constructor
    }


    public static DeviceFragment newInstance() {
        DeviceFragment fragment = new DeviceFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_device, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar!=null) {
            device = BasaDevice.getCurrentDevice();
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



//        for(int id : CLICK)
//            rootView.findViewById(id).setOnClickListener(this);
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
