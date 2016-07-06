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
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.estimote.sdk.SystemRequirementsChecker;

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
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.util.ClapListener;
import pt.ulisboa.tecnico.basa.util.LevenshteinDistance;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class MainActivity extends FragmentActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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


//       t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                Log.d("activity", "activity onInit:" + status);
//                if(status != TextToSpeech.ERROR) {
//                    t1.setLanguage(Locale.UK);
//                    t1.speak("hello all", TextToSpeech.QUEUE_FLUSH, null);
//                }
//            }
//        });

    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServerService.LocalBinder binder = (ServerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.registerClient(MainActivity.this);
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
        Intent intent = new Intent(this, ServerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            mService.stopserver();
            mService.registerClient(null);
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("myapp_new", "****onResume onResume onResume: ");

        if(mHelper == null)
            mHelper = new CameraHelper(this);

        this.basaManager = new BasaManager(this);
        this.basaManager.start();
        AppController.getInstance().basaManager = this.basaManager;
        initSavedValues();




        start_camera();
        this.videoManager = new VideoManager(this);
        //clapListener = new ClapListener(this);


        getVideoManager().start();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        AppController.getInstance().setInterfaceToActivity(new InterfaceToActivity() {

            @Override
            public void updateTemperature(double temperature) {
                if(getBasaManager().getEventManager() != null)
                    getBasaManager().getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, temperature));
            }

            @Override
            public BasaManager getManager() {
                return getBasaManager();
            }
        });

        AppController.getInstance().beaconStart();


        Intent intent = new Intent(this, ServerService.class);
        startService(intent);

    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d("checkIsSignedIn", "**onPause**** ");

        if(mHelper != null) {
            mHelper = null;
        }
        if(mCamera != null) {
            stop_camera();

        }
        destroyView();

        AppController.getInstance().setInterfaceToActivity(null);
        AppController.getInstance().beaconDisconect();

        this.basaManager.stop();
        AppController.getInstance().basaManager = null;
        //clapListener.stop();
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
            super.onBackPressed();
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







    public void openFragment(){
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, PreferencesFragment.newInstance(), "PreferencesFragment").commit();
        findViewById(R.id.content).setVisibility(View.VISIBLE);

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
//        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
//        SpeechListener mRecognitionListener = new SpeechListener(mSpeechRecognizer, mSpeechIntent);
//        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
//
//
//        mSpeechRecognizer.startListening(mSpeechIntent);







        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainActivity.this);
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


//
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
////        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
////                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                "speach promt!!2!2!");
//        try {
//            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(this,
//                    "not supported",
//                    Toast.LENGTH_SHORT).show();
//        }
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

    public BasaManager getBasaManager() {
        return basaManager;
    }

    public VideoManager getVideoManager() {
        return videoManager;
    }
}