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
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.backgroundServices.KioskService;
import pt.ulisboa.tecnico.basa.ui.secondary.CameraSettingsDialogFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.ScanHVAVDialogFragment;

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

        button = findPreference("FIREBASE_BTN");
        if(button != null) {
            button.setOnPreferenceClickListener(new PreferenceListener());
        }

        button = findPreference("btn_kiosk");
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

                case "FIREBASE_BTN":
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("message");

                    myRef.setValue("Hello, World!");

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    String password = preferences.getString(Global.FIREBASE_PASS, "");
                    String email = preferences.getString(Global.FIREBASE_EMAIL, "");

                    password = password.trim();
                    email = email.trim();

                    if (password.isEmpty() || email.isEmpty()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.signup_error_message)
                                .setTitle(R.string.signup_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {

                        // signup

                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnFailureListener(getActivity(), new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        if(e instanceof FirebaseAuthUserCollisionException){
                                            Log.d("login", "createUserWithEmail:getErrorCode:" + ((FirebaseAuthUserCollisionException )e).getErrorCode());
                                            if(((FirebaseAuthUserCollisionException )e).getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE")){
                                                Toast.makeText(AppController.getAppContext(), "Email already in use.",
                                                        Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                        e.printStackTrace();
                                    }
                                })
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d("login", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
//                                            task.getException().printStackTrace();
                                            Toast.makeText(AppController.getAppContext(), "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }

//                        ref.createUser(email, password, new Firebase.ResultHandler() {
//                            @Override
//                            public void onSuccess() {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setMessage(R.string.signup_success)
//                                        .setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialogInterface, int i) {
////                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
////                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                                                startActivity(intent);
//                                            }
//                                        });
//                                AlertDialog dialog = builder.create();
//                                dialog.show();
//                            }
//
//                            @Override
//                            public void onError(FirebaseError firebaseError) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                                builder.setMessage(firebaseError.getMessage())
//                                        .setTitle(R.string.signup_error_title)
//                                        .setPositiveButton(android.R.string.ok, null);
//                                AlertDialog dialog = builder.create();
//                                dialog.show();
//                            }
//                        });
//                    }

                    break;

            }

            return true;
        }
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
