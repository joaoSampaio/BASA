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
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ZoneSettingsInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ZoneSettingsInfoFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    private Zone zone;
    View changeName;
    TextView textViewName;

    public ZoneSettingsInfoFragment() {
        // Required empty public constructor
    }


    public static ZoneSettingsInfoFragment newInstance() {
        ZoneSettingsInfoFragment fragment = new ZoneSettingsInfoFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_settings_zone_info, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar!=null) {
            zone = Zone.getCurrentZone();
            toolbar.setTitle("Zone info");
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

        changeName = rootView.findViewById(R.id.changeName);
        textViewName = (TextView)rootView.findViewById(R.id.textViewName);
        textViewName.setText(zone.getName());
        changeName.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changeName:
                showInputDialog();

                break;
        }
    }


    protected void showInputDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        editText.setText(zone.getName());
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        List<Zone> zones = Zone.loadZones();
                        zone = Zone.getZoneByName(zone.getName(), zones);
                        zone.setName(editText.getText().toString());
                        Zone.saveZones(zones);
                        Zone.saveCurrentZone(zone);
                        textViewName.setText(zone.getName());
                        if(((MainActivity)getActivity()).getCommunicationHomeFragment()!= null){
                            ((MainActivity)getActivity()).getCommunicationHomeFragment().updateZone();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }



}
