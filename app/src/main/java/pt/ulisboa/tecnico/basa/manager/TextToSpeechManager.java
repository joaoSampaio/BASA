package pt.ulisboa.tecnico.basa.manager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.Set;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.ui.MainActivity;

/**
 * Created by joaosampaio on 29-03-2016.
 */
public class TextToSpeechManager implements TextToSpeech.OnInitListener{

    private TextToSpeech tts;
    private boolean isInitialized;
    public static final int RESULT_SPEECH = 1;

    public TextToSpeechManager(MainActivity activity) {
        isInitialized = false;
        tts = new TextToSpeech(AppController.getAppContext(), this);
        Log.d("TextToSpeechManager", "constructor:");
    }


    public void setTts(TextToSpeech tts) {
        this.tts = tts;
        isInitialized = true;
    }

    @Override
    public void onInit(int status) {
        Log.d("TextToSpeechManager", "onInit:"+status);
        if (status == TextToSpeech.SUCCESS) {
            //Setting speech Language
            isInitialized = true;
//            for (Locale loc : tts.getAvailableLanguages()){
//                Log.d("locale", "getCountry:"+loc.getCountry() + " toLanguageTag:" + loc.toLanguageTag());
//            }
            tts.setLanguage(Locale.US);

//            int res = tts.setLanguage(new Locale("pt","PT"));
//            Log.d("locale", "res:"+res);
//            if (res >= TextToSpeech.LANG_AVAILABLE) {
//                tts.speak("Muito obrigado a todos!", TextToSpeech.QUEUE_FLUSH, null, null);
//            }
//            engine.setLanguage(new Locale("pt", "PT"));
                tts.setPitch(1);
        }
    }


    public boolean speak(String text){
        Log.d("TextToSpeechManager", "speak22:"+isInitialized);
        if(isInitialized){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        return isInitialized;
    }

    public void destroy(){
        tts.stop();
        tts.shutdown();
        tts = null;
        isInitialized = false;
    }


}
