package pt.ulisboa.tecnico.basa.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.ui.secondary.IFTTTMainFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.RegisterUserDialogFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.SettingsTemperatureFragment;

//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;


public class GeneralMenuFragment extends Fragment implements View.OnClickListener {


    View rootView;
    boolean isOn = true;
    private static final int[] CLICK = {R.id.action_ifttt, R.id.action_settings, R.id.action_intruder,
            R.id.action_settings_temperature, R.id.action_user};

    public GeneralMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_general_menu, container, false);

        for (int id: CLICK)
            rootView.findViewById(id).setOnClickListener(this);


        return rootView;
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
        DialogFragment newFragment = null;
        String tag = " ";
        switch (v.getId()){
            case R.id.action_ifttt:
                newFragment = IFTTTMainFragment.newInstance();
                tag = "IFTTTMainFragment";
                break;
            case R.id.action_settings:
                ((Launch2Activity)getActivity()).openFragment(Global.PAGE_SETTINGS);
                break;
            case R.id.action_settings_temperature:
                newFragment = SettingsTemperatureFragment.newInstance();
                tag = "SettingsTemperatureFragment";
                break;
            case R.id.action_user:
                Log.d("register", "register user");
                newFragment = RegisterUserDialogFragment.newInstance();
                tag = "RegisterUserDialogFragment";
                break;
            case R.id.action_intruder:

                Launch2Activity activity = (Launch2Activity)getActivity();
                activity.toggleScreen(!activity.isScreenOn());




//                PowerManager powerManager = (PowerManager) getActivity().getSystemService(getActivity().POWER_SERVICE);
//                PowerManager.WakeLock wakeLock = powerManager.newWakeLock(field, getLocalClassName());


                Log.d("menu", "action_intruder user");
//                Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1000);
                break;


        }


        if(newFragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            newFragment.show(ft, tag);
        }

    }



}
