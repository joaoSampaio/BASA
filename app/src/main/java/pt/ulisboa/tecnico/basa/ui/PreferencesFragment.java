package pt.ulisboa.tecnico.basa.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.preference.Preference;
//import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.ui.secondary.CameraSettingsDialogFragment;


public class PreferencesFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {

    public PreferencesFragment(){}

    public static PreferencesFragment newInstance() {
        PreferencesFragment fragment = new PreferencesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

//    @Override
//    public void onCreatePreferences(Bundle bundle, String s) {
//
//    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("cam_accuracy")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.d("myapp", "cam_accuracy: " + preferences.getString("cam_accuracy", "0.5"));

            AppController.getInstance().mThreshold = Float.parseFloat(preferences.getString("cam_accuracy", "0.5"));
            AppController.getInstance().mThreshold = AppController.getInstance().mThreshold /100;

        }else if(key.equals("cam_time")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.d("myapp", "cam_time: " + preferences.getString("cam_time", "2"));

            AppController.getInstance().timeScanPeriod = Integer.parseInt(preferences.getString("cam_time", "2"));

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);


        Preference button = findPreference("btn_camera_advanced");
        if(button != null) {
            button.setOnPreferenceClickListener(new PreferenceListener());
        }


    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    private class PreferenceListener implements Preference.OnPreferenceClickListener{

        @Override
        public boolean onPreferenceClick(Preference preference) {

            String key = preference.getKey();
            switch (key){
                case "btn_camera_advanced":

                    String tag = "CameraSettingsDialogFragment";
                    DialogFragment newFragment = CameraSettingsDialogFragment.newInstance();

                    if(newFragment != null) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag(tag);
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);
                        newFragment.show(ft, tag);
                    }
                    break;
                case "start_service":


                    break;

            }

            return true;
        }
    }

}
