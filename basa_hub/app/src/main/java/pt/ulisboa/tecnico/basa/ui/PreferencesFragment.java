package pt.ulisboa.tecnico.basa.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.backgroundServices.KioskService;
import pt.ulisboa.tecnico.basa.ui.secondary.CameraSettingsDialogFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.ScanHVAVDialogFragment;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;

//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v7.preference.Preference;
//import android.support.v7.preference.PreferenceFragmentCompat;


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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        if(v != null) {

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)v.getLayoutParams();
            params.setMargins(150, 0, 10, 0); //substitute parameters for left, top, right, bottom
            v.setLayoutParams(params);
        }
        return v;
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

        }else if(key.equals("LOCATION_WIFI")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AppController.getInstance().getDeviceConfig().setMacList(getList(preferences.getString("LOCATION_WIFI", "")));
            AppController.getInstance().saveDeviceConfig();
            new FirebaseHelper().updateDeviceLocationList();

        }else if(key.equals("LOCATION_BLE")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AppController.getInstance().getDeviceConfig().setBeaconList(getList(preferences.getString("LOCATION_BLE", "")));
            AppController.getInstance().saveDeviceConfig();
            new FirebaseHelper().updateDeviceLocationList();

        }else if(key.equals("light_number")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AppController.getInstance().getDeviceConfig().setEdupNumLight(Integer.parseInt(preferences.getString("light_number", "1")));
            AppController.getInstance().saveDeviceConfig();

        }else if(key.equals("BEACON_UUID")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            AppController.getInstance().getDeviceConfig().setBeaconUuidTemperature(preferences.getString("BEACON_UUID", ""));
            AppController.getInstance().saveDeviceConfig();

        }
        else if(key.equals("cam_time")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Log.d("myapp", "cam_time: " + preferences.getString("cam_time", "2"));

            AppController.getInstance().timeScanPeriod = Integer.parseInt(preferences.getString("cam_time", "2"));

        }else if(key.equals("cam_recording")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean record =  preferences.getBoolean("cam_recording", true);
            Log.d("myapp", "cam_recording: " + record);

            AppController.getInstance().getDeviceConfig().setEnableRecording(record);

        }else if(key.equals("cam_live")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean live =  preferences.getBoolean("cam_live", true);
            Log.d("myapp", "cam_live: " + live);

            AppController.getInstance().getDeviceConfig().setEnableLiveView(live);

        }else if(key.equals("enable_kiosk")){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean enableKiosk = preferences.getBoolean("enable_kiosk", false);
            KioskService.setKioskModeActive(enableKiosk, AppController.getAppContext());
            Toast.makeText(AppController.getAppContext(),enableKiosk?"Kiosk Mode enabled": "Kiosk Mode disabled", Toast.LENGTH_SHORT).show();
            PackageManager p = getActivity().getPackageManager();
            ComponentName cN = new ComponentName(getActivity(), Launch2Activity.class);
            if(enableKiosk){

                if(!isMyLauncherDefault()){
                    Log.d("myapp", "isMyLauncherDefault false");


                    cN = new ComponentName(getActivity(), FakeHome.class);
                    p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

                    Intent selector = new Intent(Intent.ACTION_MAIN);
                    selector.addCategory(Intent.CATEGORY_HOME);
                    startActivity(selector);

                    p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                }
            }else{
                if(isMyLauncherDefault()){
                    Log.d("myapp", "isMyLauncherDefault true");
//                    p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    getActivity().getPackageManager().clearPackagePreferredActivities(getActivity().getPackageName());
//                    Intent selector = new Intent(Intent.ACTION_MAIN);
//                    selector.addCategory(Intent.CATEGORY_HOME);
//                    startActivity(selector);
                }else{


                }

//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory(Intent.CATEGORY_HOME);
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startMain);
            }
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

        button = findPreference("btn_scan_hvac");
        if(button != null) {
            button.setOnPreferenceClickListener(new PreferenceListener());
        }



        button = findPreference("btn_kiosk");
        if(button != null) {
            button.setOnPreferenceClickListener(new PreferenceListener());
        }


        button = findPreference("OPEN_REG");
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
            String tag;
            DialogFragment newFragment;
            String key = preference.getKey();
            switch (key){
                case "btn_camera_advanced":

                    tag = "CameraSettingsDialogFragment";
                    newFragment = CameraSettingsDialogFragment.newInstance();

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

                case "btn_scan_hvac":
                    tag = "ScanHVAVDialogFragment";
                    newFragment = ScanHVAVDialogFragment.newInstance();

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
                case "btn_kiosk":

                    String message = "password";
                    if (message.equals("password")) {
                        Intent intent = new Intent("android.intent.action.MAIN");
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        startActivity(Intent.createChooser(intent, "Scegli:"));
                    }


                    KioskService.setKioskModeActive(false, AppController.getAppContext());

                    Toast.makeText(AppController.getAppContext(),"You can leave the app now!", Toast.LENGTH_SHORT).show();
                    getActivity().finish();

                    break;
                case "OPEN_REG":
                    ((Launch2Activity)getActivity()).openSetup();


                    break;
                case "FIREBASE_BTN":

                    break;

            }

            return true;
        }
    }

    private List<String> getList(String data){
        List<String> list = new ArrayList<>();

        String[] words = data.split(",");
        for(String word : words){
            list.add(word.trim());
        }
        return list;
    }

    private boolean isMyLauncherDefault() {
        PackageManager localPackageManager = getActivity().getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str.equals(getActivity().getPackageName());
    }

}
