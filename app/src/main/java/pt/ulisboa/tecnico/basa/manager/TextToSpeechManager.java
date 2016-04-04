package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by joaosampaio on 29-03-2016.
 */
public class TextToSpeechManager implements TextToSpeech.OnInitListener{

    private TextToSpeech engine;
    private boolean isInitialized;

    public TextToSpeechManager(Context ctx) {
        isInitialized = false;
        engine = new TextToSpeech(ctx, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Setting speech Language
            isInitialized = true;
            engine.setLanguage(Locale.US);
            engine.setPitch(1);
        }
    }


    public boolean speak(String text){
        if(isInitialized){
            engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        return isInitialized;
    }


}
