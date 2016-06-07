package pt.ulisboa.tecnico.basa.ui;

import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.ui.secondary.CameraSettingsDialogFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.IFTTTDialogFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.RegisterUserDialogFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.SettingsTemperatureFragment;


public class GeneralMenuFragment extends Fragment implements View.OnClickListener {


    View rootView;
    private static final int[] CLICK = {R.id.action_ifttt, R.id.action_settings,
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
                newFragment = IFTTTDialogFragment.newInstance();
                tag = "IFTTTDialogFragment";
                break;
            case R.id.action_settings:
                ((MainActivity)getActivity()).openFragment();
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
