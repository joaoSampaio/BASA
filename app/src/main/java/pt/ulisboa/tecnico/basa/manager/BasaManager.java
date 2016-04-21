package pt.ulisboa.tecnico.basa.manager;

import pt.ulisboa.tecnico.basa.ui.MainActivity;

/**
 * Created by joaosampaio on 20-04-2016.
 */
public class BasaManager {

    private EventManager eventManager;
    private LightingManager lightingManager;
    private SpeechRecognizerManager speechRecognizerManager;
    private TextToSpeechManager textToSpeechManager;
    private TemperatureManager temperatureManager;
    private MainActivity activity;

    public BasaManager(MainActivity activity) {
        this.activity = activity;
    }

    public void start(){
        this.eventManager = new EventManager(getActivity());
        this.lightingManager = new LightingManager(getActivity());
        this.speechRecognizerManager = new SpeechRecognizerManager(getActivity(), getActivity());
        this.textToSpeechManager = new TextToSpeechManager(getActivity());
        this.temperatureManager = new TemperatureManager(getActivity());
    }

    public void stop(){
        if(eventManager != null){
            eventManager.stop();
        }
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

    public MainActivity getActivity() {
        return activity;
    }
}
