package pt.ulisboa.tecnico.mybasaclient.ui;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZoneSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoneSettingsFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    private Zone zone;
    private TextView remove;
    private final static int[] CLICK = {R.id.zone_info, R.id.add_device, R.id.remove_zone};

    public ZoneSettingsFragment() {
        // Required empty public constructor
    }


    public static ZoneSettingsFragment newInstance() {
        ZoneSettingsFragment fragment = new ZoneSettingsFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_settings_zone, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar!=null) {
            zone = AppController.getInstance().getCurrentZone();
            toolbar.setTitle(zone.getName());
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

//            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismissFragment();
                }
            });
        }

        init();
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();


        ((MainActivity)getActivity()).setCommunicationSettings(new CommunicationSettings() {
            @Override
            public void onZoneChange() {
                zone = AppController.getInstance().getCurrentZone();
                toolbar.setTitle(zone.getName());
                remove.setText("Remove " + zone.getName());
            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();

        ((MainActivity)getActivity()).setCommunicationSettings(null);


    }

    private void init(){

        remove = (TextView)rootView.findViewById(R.id.textViewRemove);
        remove.setText("Remove " + zone.getName());

        for(int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zone_info:
                ((MainActivity)getActivity()).openPage(Global.DIALOG_SETTINGS_ZONE_INFO);
                break;
            case R.id.add_device:
                ((MainActivity)getActivity()).openPage(Global.DIALOG_ADD_DEVICE);
                break;
            case R.id.remove_zone:
                List<Zone> zones = AppController.getInstance().loadZones();
                if(zones.size() >= 2){

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Remove zone: " + zone.getName())
                            .setMessage("Do you want to remove " + zone.getName() + " from your account?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    ((MainActivity)getActivity()).showMessage("Zone removed.");
                                    Zone.removeZone(zone.getName());
                                    List<Zone> zones = AppController.getInstance().loadZones();
                                    Zone newCurrent = zones.get(0);
                                    if( ((MainActivity)getActivity()).getCommunicationHomeFragment() != null)
                                        ((MainActivity)getActivity()).getCommunicationHomeFragment().changeZone(newCurrent.getName());
                                    if( ((MainActivity)getActivity()).getCommunicationUserFragment() != null)
                                        ((MainActivity)getActivity()).getCommunicationUserFragment().refreshZones();
                                    dismissFragment();


                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                }else{
                    //
                    Toast.makeText(getActivity(), "You need at least two zones to remove this one.", Toast.LENGTH_LONG).show();
                    return;
                }


                break;

        }
    }


    private void dismissFragment(){
        if(getDialog() != null)
            getDialog().dismiss();
    }

    public interface CommunicationSettings{
        void onZoneChange();
    }

}
