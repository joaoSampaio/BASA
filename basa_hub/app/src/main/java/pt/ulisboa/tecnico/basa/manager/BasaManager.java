package pt.ulisboa.tecnico.basa.manager;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;

/**
 * Created by joaosampaio on 20-04-2016.
 */
public class BasaManager {

    private EventManager eventManager;
    private LightingManager lightingManager;
    private SpeechRecognizerManager speechRecognizerManager;
    private TextToSpeechManager textToSpeechManager;
    private TemperatureManager temperatureManager;
    private DeviceDiscoveryManager deviceDiscoveryManager;
    private WebServerManager webServerManager;
    private UserManager userManager;
    private Launch2Activity activity;
    private ValueEventListener fireListenner;
    private VideoManager videoManager;
    private BasaSensorManager basaSensorManager;

    public BasaManager() {
        Log.d("manager", "BasaManager new ");
//        start();
    }

    public void start(){
        if(eventManager == null && lightingManager == null) {
            Log.d("manager", "BasaManager start ");
            this.eventManager = new EventManager(this);
            this.lightingManager = new LightingManager();
            this.textToSpeechManager = new TextToSpeechManager();
            this.speechRecognizerManager = new SpeechRecognizerManager(this);

            this.temperatureManager = new TemperatureManager(this);
            this.deviceDiscoveryManager = new DeviceDiscoveryManager();
            this.userManager = new UserManager();
            this.basaSensorManager = new BasaSensorManager();
//        if(getActivity() != null){
//            videoManager = new VideoManager(getActivity());
//        }

            if (AppController.getInstance().getDeviceConfig().isFirebaseEnabled()) {
                FirebaseHelper mHelperFire = new FirebaseHelper();
                fireListenner = mHelperFire.getZoneDevicesListener();
            }

        }

    }

    public void stop(){

        Log.d("manager", "basamanager stop:");

        if(basaSensorManager != null){
            basaSensorManager.destroy();
        }

        if(fireListenner != null){
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.removeEventListener(fireListenner);
            fireListenner = null;
        }

//        if(videoManager != null){
//            videoManager.destroy();
//        }

        if(lightingManager != null){
            lightingManager.destroy();
        }
        if(speechRecognizerManager != null){
            speechRecognizerManager.destroy();
        }
        if(textToSpeechManager != null){
            textToSpeechManager.destroy();
        }

        if(temperatureManager != null){
            temperatureManager.destroy();
        }

        if(deviceDiscoveryManager != null){
            deviceDiscoveryManager.destroy();
        }

//        if(webServerManager != null){
//            webServerManager.destroy();
//        }

        if(userManager != null){
            userManager.destroy();
        }

        if(eventManager != null){
            eventManager.stop();
        }

        this.eventManager = null;
        this.lightingManager = null;
        this.speechRecognizerManager = null;
        this.textToSpeechManager = null;
        this.temperatureManager = null;
        this.deviceDiscoveryManager = null;
        this.userManager = null;

    }


    public EventManager getEventManager() {
        return eventManager;
    }

    public LightingManager getLightingManager() {
        return lightingManager;
    }

    public SpeechRecognizerManager getSpeechRecognizerManager() {
        return speechRecognizerManager;
    }

    public TextToSpeechManager getTextToSpeechManager() {
        return textToSpeechManager;
    }

    public TemperatureManager getTemperatureManager() {
        return temperatureManager;
    }

    public DeviceDiscoveryManager getDeviceDiscoveryManager() {
        return deviceDiscoveryManager;
    }

    public WebServerManager getWebServerManager() {
        return webServerManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Launch2Activity getActivity() {
        return activity;
    }

    public void setActivity(Launch2Activity activity) {
        this.activity = activity;
    }
}
