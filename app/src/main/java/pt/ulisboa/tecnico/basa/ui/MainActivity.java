package pt.ulisboa.tecnico.basa.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
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

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.MainPagerAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.camera.CameraHelper;
import pt.ulisboa.tecnico.basa.manager.EventManager;
import pt.ulisboa.tecnico.basa.manager.LightingManager;
import pt.ulisboa.tecnico.basa.manager.SpeechRecognizerManager;
import pt.ulisboa.tecnico.basa.manager.TextToSpeechManager;
import pt.ulisboa.tecnico.basa.manager.VideoManager;
import pt.ulisboa.tecnico.basa.model.Event;
import pt.ulisboa.tecnico.basa.model.EventClap;
import pt.ulisboa.tecnico.basa.model.EventCustomSwitchPressed;
import pt.ulisboa.tecnico.basa.model.EventTemperature;
import pt.ulisboa.tecnico.basa.model.EventVoice;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.model.Trigger;
import pt.ulisboa.tecnico.basa.model.TriggerAction;
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
    private EventManager eventManager;
    public final static int REQ_CODE_SPEECH_INPUT = 100;

    private ClapListener clapListener;
    private LightingManager lightingManager;
    private VideoManager videoManager;

    private SpeechRecognizer mSpeechRecognizer;
    private SpeechRecognizerManager mSpeechRecognizerManager;
    private TextToSpeechManager mTextToSpeechManager;

    //handler to post changes to progress bar
    private Handler mHandler = new Handler();

    //intent for speech recogniztion
    Intent mSpeechIntent;



    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;


    boolean isInitialized = false;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        eventManager = new EventManager(this);

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
                Log.d("myapp2", "**--onPageScrollStateChanged inBoxFragment:" + state);
            }

        });

//        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                Log.d("TextToSpeechManager", "onInit:"+status);
//                if (status == TextToSpeech.SUCCESS) {
//                    isInitialized = true;
//
//                    tts.setLanguage(Locale.US);
//
//                    tts.setPitch(1);
//                }
//            }
//        });

        this.mTextToSpeechManager = new TextToSpeechManager(this);
        mSpeechRecognizerManager =new SpeechRecognizerManager(this, this);
//        mTextToSpeechManager.setTts(tts);
//        mSpeechRecognizerManager.setOnResultListner(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d("myapp_new", "****onResume onResume onResume: ");
        initSavedValues();
        if(mHelper == null)
            mHelper = new CameraHelper(this);
        this.lightingManager = new LightingManager(this);


        start_camera();
        this.videoManager = new VideoManager(this);
        //clapListener = new ClapListener(this);


        getVideoManager().start();

//        SystemRequirementsChecker.checkWithDefaultDialogs(this);
//
//        AppController.getInstance().setInterfaceToActivity(new InterfaceToActivity() {
//
//            @Override
//            public void updateTemperature(double temperature) {
//                getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, temperature));
//            }
//        });
//
//        AppController.getInstance().beaconStart();
    }

    @Override
    protected void onStart() {

        super.onStart();
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


        getEventManager().reloadSavedRecipes();

//        List<Recipe> recipes = new ModelCache<List<Recipe>>().loadModel(new TypeToken<List<Recipe>>(){}.getType(), Global.OFFLINE_RECIPES);
//        if(recipes == null)
//            recipes = new ArrayList<>();
//
//        for(Recipe re: recipes){
//            final Recipe recipeFinal = re;
//            switch (re.getTriggerId()){
//                case Trigger.CLAP:
//                    getEventManager().registerInterest(new InterestEventAssociation(Event.CLAP, new EventManager.RegisterInterestEvent() {
//                        @Override
//                        public void onRegisteredEventTriggered(Event event) {
//                            Log.d("initSavedValues", "SWITCH onRegisteredEventTriggered");
//                            if(event instanceof EventClap){
//
//                                doAction(recipeFinal);
//
//
//
//                            }
//                        }
//                    }, 0));
//                    break;
//                case Trigger.TEMPERATURE:
//
//                    getEventManager().registerInterest(new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
//                        @Override
//                        public void onRegisteredEventTriggered(Event event) {
//                            if(event instanceof EventTemperature){
//                                double temp = ((EventTemperature)event).getTemperature();
//
//                                if(recipeFinal.isTriggerConditionBigger()){
//                                    //Temp ≥ 25
//                                    if(temp >= Integer.parseInt(recipeFinal.getConditionTrigger())){
//                                        doAction(recipeFinal);
//                                    }
//
//                                }
//                                if(recipeFinal.isTriggerConditionLess()){
//                                    //Temp ≤ 25
//                                    if(temp <= Integer.parseInt(recipeFinal.getConditionTrigger())){
//                                        doAction(recipeFinal);
//                                    }
//
//                                }
//                            }
//                        }
//                    }, 0));
//
//                    break;
//                case Trigger.SWITCH:
//                    Log.d("initSavedValues", "SWITCH ");
//
//                    getEventManager().registerInterest(new InterestEventAssociation(Event.CUSTOM_SWITCH, new EventManager.RegisterInterestEvent() {
//                        @Override
//                        public void onRegisteredEventTriggered(Event event) {
//                            Log.d("initSavedValues", "SWITCH onRegisteredEventTriggered");
//                            if(event instanceof EventCustomSwitchPressed){
//                                int id = ((EventCustomSwitchPressed)event).getId();
//
//                                //se for o switch pretendido
//                                if(recipeFinal.getSelectedTrigger().get(0) == id){
//                                    doAction(recipeFinal);
//                                }
//                            }
//                        }
//                    }, 0));
//                    break;
//                case Trigger.VOICE:
//
//                    break;
//                case Trigger.EMAIL:
//
//                    break;
//
//            }
//        }
    }

//    public void doAction(Recipe re){
//        int lightId;
//        switch (re.getActionId()){
//
//            case TriggerAction.TEMPERATURE:
//
//                break;
//            case TriggerAction.LIGHT_ON:
//
//
//                for(int id: re.getSelectedAction()){
//                    if(getLightingManager() != null)
//                        getLightingManager().turnONLight(id);
//                }
//
//                break;
//            case TriggerAction.LIGHT_OFF:
//                for(int id: re.getSelectedAction()){
//                    if(getLightingManager() != null)
//                        getLightingManager().turnOFFLight(id);
//                }
//                break;
//
//            case TriggerAction.VOICE:
//
//                String say = re.getConditionEventValue();
//                getmTextToSpeechManager().speak(say);
//
//
//                break;
//            case TriggerAction.EMAIL:
//
//                break;
//        }
//    }





    public void openFragment(){
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, PreferencesFragment.newInstance(), "PreferencesFragment").commit();
        findViewById(R.id.content).setVisibility(View.VISIBLE);

    }



    public EventManager getEventManager() {
        if (eventManager == null)
            eventManager = new EventManager(this);
        return eventManager;
    }


    public interface InterfaceToActivity {
        void updateTemperature(double temperature);
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

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult", "**requestCode:"+requestCode);
        Log.d("onActivityResult", "**chegou--**:"+ (null != data) + "   :"+ (resultCode == Activity.RESULT_OK));
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    getEventManager().addEvent(new EventVoice( result.get(0)));
                    for(String s : result)
                        Log.d("onActivityResult", "****" + s + "****: ");
//                    txtSpeechInput.setText(result.get(0));
                }
                break;
            }

        }
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

    public LightingManager getLightingManager() {
        return lightingManager;
    }

    public TextToSpeechManager getmTextToSpeechManager() {
        return mTextToSpeechManager;
    }


    public VideoManager getVideoManager() {
        return videoManager;
    }
}