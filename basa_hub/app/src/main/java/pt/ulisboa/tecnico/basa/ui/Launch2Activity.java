package pt.ulisboa.tecnico.basa.ui;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;
import com.gigamole.navigationtabbar.ntb.NavigationTabBar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.MainPagerAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.backgroundServices.ServerService;
import pt.ulisboa.tecnico.basa.camera.CameraBasa;
import pt.ulisboa.tecnico.basa.camera.CameraHelper;
import pt.ulisboa.tecnico.basa.manager.BasaManager;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.ui.setup.MainSetupActivity;
import pt.ulisboa.tecnico.basa.util.ClapListener;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class Launch2Activity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener {



    private CameraBasa mHelper;

    private ClapListener clapListener;
    private BasaManager basaManager;


    ServerService mService;
    boolean mBound = true;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private FirebaseAuth.AuthStateListener mAuthListener;
    String TAG = "main";
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseHelper helper = null;

    private Handler handler;
    private Runnable cameraRun = new Runnable() {
        @Override
        public void run() {
            if (mHelper == null) {
                mHelper = new CameraHelper(Launch2Activity.this);
                mHelper.start_camera();
            }
        }
    };

// ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);


        if(AppController.getInstance().getDeviceConfig() == null){
            openSetup();

        }else{
            initUI();
            initFirebase();
        }

    }


    public void openSetup(){
        Intent intent = new Intent(this, MainSetupActivity.class);
        startActivity(intent);
        finish();
    }


    private void initFirebase(){
        BasaDeviceConfig config = AppController.getInstance().getDeviceConfig();
        if(config != null && config.isFirebaseEnabled()) {


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mAuth = FirebaseAuth.getInstance();

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                        BasaDeviceConfig config = AppController.getInstance().getDeviceConfig();
                        config.setUuid(user.getUid());
                        AppController.getInstance().setDeviceConfig(config, true);
                        helper = new FirebaseHelper();


                        helper.checkIfDeviceExists(new CallbackMultiple<Boolean, Boolean>() {
                            @Override
                            public void success(Boolean exists) {
                                if(!exists)
                                    helper.registerDevice(AppController.getInstance().getDeviceConfig().getUuid());
                            }

                            @Override
                            public void failed(Boolean error) {

                            }
                        });
                    } else {
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            };
            if(mAuth.getCurrentUser() == null) {
                Log.d(TAG, "mAuth.getCurrentUser() == null");
                signIn();

            }
        }

    }

    private void initUI(){
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        showPage(Global.PAGE_LIGHTS);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                Fragment fragment = getFragmentManager().findFragmentByTag("PreferencesFragment");
                if(fragment != null)
                getFragmentManager().beginTransaction()
                        .remove(fragment).commit();

                findViewById(R.id.content).setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });





        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_vertical);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_light),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_light))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_temperature),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_light))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_option),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_light))
                        .build()
        );


        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(mPager, 0);





    }




    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Log.d(TAG, "Google Sign In was successful, authenticate with Firebase");
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(TAG, "Google Sign In failed, update UI appropriately:");

                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Launch2Activity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServerService.LocalBinder binder = (ServerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.registerClient(Launch2Activity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        BasaDeviceConfig config = AppController.getInstance().getDeviceConfig();
        if (config != null) {
            Intent intent = new Intent(this, ServerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            if (mAuth != null) {
                mAuth.addAuthStateListener(mAuthListener);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound && mService!= null) {
            mService.stopserver();
            mService.registerClient(null);
            unbindService(mConnection);
            mBound = false;
        }

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("myapp_new", "****onResume onResume onResume: ");
        this.handler = new Handler();
        BasaDeviceConfig config = AppController.getInstance().getDeviceConfig();
        if(config != null) {


            this.basaManager = AppController.getInstance().getBasaManager();
            this.basaManager.setActivity(this);
            this.basaManager.start();
            handler.postDelayed(cameraRun, 2000);

            initSavedValues();

            //clapListener = new ClapListener(this);

            SystemRequirementsChecker.checkWithDefaultDialogs(this);

            AppController.getInstance().beaconStart();
            Intent intent = new Intent(this, ServerService.class);
            startService(intent);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d("app", "onPause activity");
        if(handler != null)
            handler.removeCallbacks(cameraRun);

        if(mHelper != null) {
            mHelper.destroy();
            mHelper = null;
        }

        AppController.getInstance().beaconDisconect();
        if(this.basaManager != null) {
            this.basaManager.stop();
            this.basaManager.setActivity(null);
        }
    }

    public void restoreApp() {
        // Restart activity
        Intent i = new Intent(this, Launch2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {

        Log.d("app", "onBackPressed");
        Fragment f = getFragmentManager().findFragmentByTag("PreferencesFragment");
        if(f != null && f instanceof PreferencesFragment) {
            PreferencesFragment preferencesFragment = (PreferencesFragment) f;
            if(preferencesFragment.isVisible()){
                Log.d("app", "isVisible");
                getFragmentManager().beginTransaction().remove(preferencesFragment).commit();
                findViewById(R.id.content).setVisibility(View.GONE);

                return;
            }
        }


        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.

            //super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    private void showPage(int page){
        mPager.setCurrentItem(page);
    }





    public CameraBasa getmHelper() {
        return mHelper;
    }

    private void initSavedValues(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        AppController.getInstance().mThreshold = Float.parseFloat(preferences.getString("cam_accuracy", "0.5"));
        AppController.getInstance().mThreshold = AppController.getInstance().mThreshold /100;

        AppController.getInstance().timeScanPeriod = Integer.parseInt(preferences.getString("cam_time", "2"));
//        new ModelCache<List<Recipe>>().saveModel(new ArrayList<Recipe>(), Global.OFFLINE_RECIPES);

        if(getBasaManager().getEventManager() != null)
            getBasaManager().getEventManager().reloadSavedRecipes();

        if(!ModelCache.keyExists(Global.OFFLINE_USERS)){
            new ModelCache<>().saveModel(new ArrayList<>(), Global.OFFLINE_USERS);
        }

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("light_number", AppController.getInstance().getDeviceConfig().getEdupNumLight()+"");
        editor.commit();

    }



    public BasaManager getBasaManager(){
        return this.basaManager;
    }



    public void openFragment(int id){
        Fragment fragment = null;
        if(id == Global.PAGE_SETUP){

        }else{
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, PreferencesFragment.newInstance(), "PreferencesFragment").commit();
        }

        findViewById(R.id.content).setVisibility(View.VISIBLE);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }


    public interface InterfaceToActivity {
        void updateTemperature(double temperature);

        BasaManager getManager();
    }

    private boolean screenState = true;

    public boolean isScreenOn(){
        return screenState;
    }

    public void toggleScreen(boolean turnOn){
        WindowManager.LayoutParams params = getWindow().getAttributes();
        screenState = turnOn;
        if (!turnOn) {
            //TODO Store original brightness value
            params.screenBrightness = 0.001f;
            getWindow().setAttributes(params);
            //enableDisableViewGroup((ViewGroup)findViewById(R.id.main_container).getParent(),false);
            Log.e("onSensorChanged","NEAR");

        } else {
            //TODO Store original brightness value
            params.screenBrightness = -1.0f;
            this.getWindow().setAttributes(params);
            //enableDisableViewGroup((ViewGroup)findViewById(R.id.main_container).getParent(),true);
            Log.e("onSensorChanged","FAR");
        }
    }

    public static void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }
}