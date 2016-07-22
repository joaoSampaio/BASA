package pt.ulisboa.tecnico.basa.manager;

import pt.ulisboa.tecnico.basa.ui.Launch2Activity;

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

    public BasaManager(Launch2Activity activity) {
        this.activity = activity;
    }

    public void start(){
        this.eventManager = new EventManager(getActivity());
        this.lightingManager = new LightingManager(getActivity());
        this.speechRecognizerManager = new SpeechRecognizerManager(getActivity(), getActivity());
        this.textToSpeechManager = new TextToSpeechManager(getActivity());
        this.temperatureManager = new TemperatureManager(getActivity());
        this.deviceDiscoveryManager = new DeviceDiscoveryManager(getActivity());
//        this.webServerManager = new WebServerManager(getActivity());
        this.userManager = new UserManager(getActivity());
    }

    public void stop(){

        if(lightingManager != null){

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

        if(webServerManager != null){
            webServerManager.destroy();
        }

        if(userManager != null){
            userManager.destroy();
        }

        if(eventManager != null){
            eventManager.stop();
        }
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
}
