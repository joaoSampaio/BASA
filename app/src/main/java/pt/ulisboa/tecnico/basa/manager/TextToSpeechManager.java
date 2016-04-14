package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
import java.util.Set;

/**
 * Created by joaosampaio on 29-03-2016.
 */
public class TextToSpeechManager implements TextToSpeech.OnInitListener{

    private TextToSpeech tts;
    private boolean isInitialized;

    public TextToSpeechManager(Context ctx) {
        isInitialized = false;
        tts = new TextToSpeech(ctx, this);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //Setting speech Language
            isInitialized = true;
//            for (Locale loc : tts.getAvailableLanguages()){
//                Log.d("locale", "getCountry:"+loc.getCountry() + " toLanguageTag:" + loc.toLanguageTag());
//            }
////            tts.setLanguage(Locale.US);
//            Log.d("locale", "language available PT-pt:" + tts.isLanguageAvailable(new Locale("PT", "pt")));
//            Log.d("locale", "language available pt-PT:" + tts.isLanguageAvailable(new Locale("pt", "PT")));
//            Log.d("locale", "language available pt-pt:" + tts.isLanguageAvailable(new Locale("pt", "pt")));
//            Log.d("locale", "language available PT:" + tts.isLanguageAvailable(new Locale( "PT")));
//            Log.d("locale", "language available pt:" + tts.isLanguageAvailable(new Locale( "pt")));
//            Log.d("locale", "language available pt--PT:" + tts.isLanguageAvailable(new Locale( "pt-PT")));
//            Log.d("locale", "language available pt-POR:" + tts.isLanguageAvailable(new Locale("pt", "PT")));
            int res = tts.setLanguage(new Locale("pt","PT"));
            Log.d("locale", "res:"+res);
//            if (res >= TextToSpeech.LANG_AVAILABLE) {
//                tts.speak("Muito obrigado a todos!", TextToSpeech.QUEUE_FLUSH, null, null);
//            }
//            engine.setLanguage(new Locale("pt", "PT"));
                tts.setPitch(1);
        }
    }


    public boolean speak(String text){
        Log.d("locale", "speak:"+isInitialized);
        if(isInitialized){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        return isInitialized;
    }


}
