package pt.ulisboa.tecnico.basa.ui;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.estimote.sdk.SystemRequirementsChecker;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.MainPagerAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.backgroundServices.ServerService;
import pt.ulisboa.tecnico.basa.camera.CameraHelper;
import pt.ulisboa.tecnico.basa.manager.BasaManager;
import pt.ulisboa.tecnico.basa.manager.VideoManager;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.ui.setup.MainSetupActivity;
import pt.ulisboa.tecnico.basa.util.ClapListener;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;
import pt.ulisboa.tecnico.basa.util.LevenshteinDistance;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class Launch2Activity extends FragmentActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private final static int[] CLICKABLE = {R.id.action_gogo_lights, R.id.action_gogo_temperature, R.id.action_gogo_option};

    private Camera mCamera;
    private TextureView mTextureView;
    private Handler handler;
    private ImageView preview_img;

    private CameraHelper mHelper;
    private FrameLayout camera_preview;
//    private EventManager eventManager;
    public final static int REQ_CODE_SPEECH_INPUT = 100;

    private ClapListener clapListener;
//    private LightingManager lightingManager;
    private VideoManager videoManager;

    private SpeechRecognizer mSpeechRecognizer;

    private BasaManager basaManager;


    //handler to post changes to progress bar
    private Handler mHandler = new Handler();

    //intent for speech recogniztion
    Intent mSpeechIntent;

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

    TextToSpeech t1;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String TAG = "main";
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mDatabase;
    private FirebaseHelper helper = null;
// ...


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.activity_main);


        if(AppController.getInstance().getDeviceConfig() == null){

            Intent intent = new Intent(this, MainSetupActivity.class);
            startActivity(intent);
            finish();
        }else{
            initUI();
            initFirebase();
        }

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
                        BasaDeviceConfig.save(config);
                        mDatabase = FirebaseDatabase.getInstance().getReference();
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

//                        helper.registerDevice(user.getUid());

                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                    // ...
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
        camera_preview = (FrameLayout)findViewById(R.id.camera_preview);
        preview_img = (ImageView)findViewById(R.id.preview_img);
        handler = new Handler();

        View.OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.action_gogo_lights:

                        showPage(Global.PAGE_LIGHTS);

                        break;
                    case R.id.action_gogo_temperature:
                        showPage(Global.PAGE_TEMPERATURE);
                        break;
                    case R.id.action_gogo_option:
                        showPage(Global.PAGE_OPTIONS);
                        break;
                }
            }
        };

        for(int id : CLICKABLE)
            findViewById(id).setOnClickListener(click);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                resetBackgroundBtns();
                ImageView view = (ImageView) findViewById(CLICKABLE[position]);
                view.setBackground(getResources().getDrawable(R.drawable.circle));
                view.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

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
        BasaDeviceConfig config = AppController.getInstance().getDeviceConfig();
        if(config != null) {
            if (mHelper == null)
                mHelper = new CameraHelper(this);

            this.basaManager = AppController.getInstance().getBasaManager();
            this.basaManager.setActivity(this);
//            this.basaManager.start();

            initSavedValues();


            start_camera();
            this.videoManager = new VideoManager(this);
            //clapListener = new ClapListener(this);

            getVideoManager().start();

            SystemRequirementsChecker.checkWithDefaultDialogs(this);

//            AppController.getInstance().setInterfaceToActivity(new InterfaceToActivity() {
//
//                @Override
//                public void updateTemperature(double temperature) {
//                    if (getBasaManager().getEventManager() != null)
//                        getBasaManager().getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, temperature));
//                }
//
//                @Override
//                public BasaManager getManager() {
//                    return getBasaManager();
//                }
//            });

            AppController.getInstance().beaconStart();
            Intent intent = new Intent(this, ServerService.class);
            startService(intent);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d("checkIsSignedIn", "**onPause**** ");
        Log.d("KioskService2", "onPause:");
        if(mHelper != null) {
            mHelper = null;
        }
        if(mCamera != null) {
            stop_camera();

        }
        destroyView();

        AppController.getInstance().beaconDisconect();

        if(this.basaManager != null) {
            this.basaManager.stop();
            this.basaManager.setActivity(null);
//            AppController.getInstance().basaManager = null;
        }



        //restoreApp();


        //clapListener.stop();
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

    private void resetBackgroundBtns(){
        ImageView view;
        for(int id : CLICKABLE) {
            view = (ImageView)findViewById(id);
            view.setBackground(null);
            view.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
        }
    }

    private void showPage(int page){
        resetBackgroundBtns();
        ImageView view = (ImageView)findViewById(CLICKABLE[page]);
        view.setBackground(getResources().getDrawable(R.drawable.circle));
        view.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);



        mPager.setCurrentItem(page);
    }



    public void start_camera(){
        Log.d("cam", "start_camera " + (mCamera == null));
        if(mCamera != null)
            return;


        if( mTextureView == null){
            mTextureView = new TextureView(this);
            mTextureView.setSurfaceTextureListener(mHelper);
            camera_preview.addView(mTextureView);
            return;
        }
        mCamera = Camera.open(1);
        try {
            CameraHelper.setCameraDisplayOrientation(1, mCamera);
            mCamera.setPreviewTexture(mHelper.getSurface());
            mCamera.startPreview();
            mCamera.setPreviewCallback(mHelper.getPreviewCallback());
            AppController.getInstance().mCameraReady = true;

        } catch (IOException ioe) {
            Log.d("cam", "ioe" + ioe.getMessage());
            // Something bad happened
        }
    }

    public void stop_camera(){
        if(mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            AppController.getInstance().mCameraReady = false;

        }
    }

    public void destroyView(){
        if( mTextureView != null){
            camera_preview.removeAllViews();
            mTextureView = null;
        }
    }

    public ImageView getPreview_img() {
        return preview_img;
    }

    public CameraHelper getmHelper() {
        return mHelper;
    }

    public Camera getmCamera() {
        return mCamera;
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

    }



    public BasaManager getBasaManager(){
//        if(this.basaManager  == null) {
//            Log.d("basaManager", "basaManager");
//            this.basaManager = new BasaManager(this);
//            this.basaManager.start();
//            AppController.getInstance().basaManager = this.basaManager;
//        }
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

    /**
     * Showing google speech input dialog
     * */
    public void promptSpeechInput() {

//        mSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
//        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
//
//        // Given an hint to the recognizer about what the user is going to say
//        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        // Specify how many results you want to receive. The results will be sorted
//        // where the first result is the one with higher confidence.
//        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);
//        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
//
//        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Launch2Activity.this);
//        SpeechListener mRecognitionListener = new SpeechListener(mSpeechRecognizer, mSpeechIntent);
//        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
//
//
//        mSpeechRecognizer.startListening(mSpeechIntent);







        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(Launch2Activity.this);
        SpeechListener2 mRecognitionListener = new SpeechListener2();
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        mSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"com.androiddev101.ep8");

        // Given an hint to the recognizer about what the user is going to say
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Specify how many results you want to receive. The results will be sorted
        // where the first result is the one with higher confidence.
        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);


        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mSpeechRecognizer.startListening(mSpeechIntent);

    }



    public class SpeechListener2 implements RecognitionListener {
        String TAG = "SpeechListener";

        //legal commands
        private final String[] VALID_COMMANDS = {
                "What time is it",
                "que horas são",
                "Ligar luzes",
                "exit"
        };
        private final int VALID_COMMANDS_SIZE = VALID_COMMANDS.length;


        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "buffer recieved ");
        }

        public void onError(int error) {
            //if critical error then exit
            if (error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                Log.d(TAG, "client error");
            }
            //else ask to repeats
            else if(error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT){
                Log.d(TAG, "other error:" + error);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSpeechRecognizer.startListening(mSpeechIntent);
                    }
                },500);
            }
            else {
                Log.d(TAG, "other error:" + error);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSpeechRecognizer.startListening(mSpeechIntent);
                    }
                },5000);

            }
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent");
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "partial results");
        }

        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "on ready for speech");
        }

        public void onResults(Bundle results) {
            Log.d(TAG, "on results");
            ArrayList<String> matches = null;
            if (results != null) {
                matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    Log.d(TAG, "results are " + matches.toString());
                    final ArrayList<String> matchesStrings = matches;
                    processCommand(matchesStrings);

                    mSpeechRecognizer.startListening(mSpeechIntent);


                }
            }

        }

        public void onRmsChanged(float rmsdB) {
            //			Log.d(TAG, "rms changed");
        }

        public void onBeginningOfSpeech() {
            Log.d(TAG, "speach begining");
        }

        public void onEndOfSpeech() {
            Log.d(TAG, "speach done");
        }

        private void processCommand(ArrayList<String> matchStrings) {
            String response = "I'm sorry, Dave. I'm afraid I can't do that.";
            int maxStrings = matchStrings.size();
            boolean resultFound = false;
            for (int i = 0; i < VALID_COMMANDS_SIZE && !resultFound; i++) {
                for (int j = 0; j < maxStrings && !resultFound; j++) {
                    if (LevenshteinDistance.getLevenshteinDistance(matchStrings.get(j), VALID_COMMANDS[i]) < (VALID_COMMANDS[i].length() / 3)) {
                        Log.d("response", "found LevenshteinDistance, original:" + matchStrings.get(j) + "****: ");
                        Log.d("response", "found LevenshteinDistance, VALID_COMMANDS:" + VALID_COMMANDS[i] + "****: ");
                        resultFound = true;
                        //response = getResponse(i);
                    }
                }
            }
            if (!resultFound)
                Log.d("response", "****" + response + "****: ");


        }
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


    public VideoManager getVideoManager() {
        return videoManager;
    }
}