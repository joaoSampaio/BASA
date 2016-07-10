package pt.ulisboa.tecnico.mybasaclient.ui;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CheckServerService;
import pt.ulisboa.tecnico.mybasaclient.util.IPAddressValidator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DeviceSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceSettingsFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    private BasaDevice device;
    private TextView remove;
    private final static int[] CLICK = { R.id.changeDeviceIP, R.id.remove_device};

    public DeviceSettingsFragment() {
        // Required empty public constructor
    }


    public static DeviceSettingsFragment newInstance() {
        DeviceSettingsFragment fragment = new DeviceSettingsFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_device_settings, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        device = BasaDevice.getCurrentDevice();
        if (toolbar!=null) {
            toolbar.setTitle(device.getName());
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
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

    }

    @Override
    public void onPause(){
        super.onPause();

        ((MainActivity)getActivity()).setCommunicationSettings(null);


    }

    private void init(){
        device = BasaDevice.getCurrentDevice();
        remove = (TextView)rootView.findViewById(R.id.textViewRemove);
        remove.setText("Remove " + device.getName());
        String tempIp = device.getUrl();
        tempIp = tempIp.replace("http://", "");
        if(tempIp.length()> 10)
            tempIp = tempIp.substring(0 , 9) + "...";
        ((TextView)rootView.findViewById(R.id.textViewIP)).setText(tempIp);


        for(int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.changeDeviceIP:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setCancelable(false);
                final EditText editText = new EditText(getActivity());

                alertDialogBuilder.setView(editText);
                alertDialogBuilder.setTitle("Edit HUB IP");

                String tempIp = device.getUrl();
                tempIp = tempIp.replace("http://", "");

                editText.setText(tempIp);
                editText.setSelection(editText.getText().length());
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Change IP", null)
                        .setNegativeButton("Cancel", null);
                final AlertDialog alert = alertDialogBuilder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface arg0) {

                        Button okButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                        okButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {

                                String ip = editText.getText().toString().trim();


                                if(ip.endsWith("/")){
                                    ip = ip.substring(0, ip.length() - 1);
                                }
                                IPAddressValidator validator = new IPAddressValidator();
                                if(validator.validate(ip)){

                                    if(!ip.startsWith("http")) {
                                        ip = "http://" + ip;
                                    }

                                    device.setUrl(ip);


                                    showSnack(editText, "Contacting the server, please wait ...", true, Color.BLUE);
                                    new CheckServerService(ip, new CallbackFromService() {
                                        @Override
                                        public void success(Object response) {
                                            if(getDialog() != null)
                                            Zone.updateCurrentZone(device);
                                            if(alert != null)
                                            alert.dismiss();
                                        }

                                        @Override
                                        public void failed(Object error) {
                                            showSnack(editText, "Could not contact server", true, Color.RED);
                                        }
                                    }).execute();


//                                    alert.dismiss();
                                }else{
                                    showSnack(editText, "IP not valid ex:(192.168.10.10)", true, Color.RED);
                                }
                            }
                        });
                    }
                });
                alert.show();
                break;

            case R.id.remove_device:


                    new AlertDialog.Builder(getActivity())
                            .setTitle("Remove device: " + device.getName())
                            .setMessage("Do you want to remove " + device.getName() + " from your zone?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                    ((MainActivity)getActivity()).showMessage("Device removed.");

                                    Zone.removeDevice(device);


                                    if( ((MainActivity)getActivity()).getCommunicationHomeFragment() != null)
                                        ((MainActivity)getActivity()).getCommunicationHomeFragment().updateZone();

                                    ((MainActivity)getActivity()).dismissAllDialogs();
//                                    dismissFragment();


                                }})
                            .setNegativeButton(android.R.string.no, null).show();



                break;

        }
    }


    private void showSnack(View v, String message, boolean showColor, int color){
        Snackbar snack = Snackbar.make(v, message, Snackbar.LENGTH_SHORT);
        View view = snack.getView();
        if(showColor) {
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextColor(color);
        }
        snack.show();
    }


    private void dismissFragment(){
        if(getDialog() != null)
            getDialog().dismiss();
    }

    public interface CommunicationSettings{
        void onZoneChange();
    }

}
