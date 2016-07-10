package pt.ulisboa.tecnico.mybasaclient.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceCameraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceCameraFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    private BasaDevice device;

    public DeviceCameraFragment() {
        // Required empty public constructor
    }


    public static DeviceCameraFragment newInstance() {
        DeviceCameraFragment fragment = new DeviceCameraFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_device_camera, container, false);
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

        View settings = rootView.findViewById(R.id.action_settings);
        settings.setVisibility(View.VISIBLE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).openPage(Global.DIALOG_DEVICE_SETTINGS);
            }
        });
//imageCamera

        Glide.with(this).load(R.drawable.ic_device_camera_large)
                .crossFade()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView)rootView.findViewById(R.id.imageCamera));


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
