package pt.ulisboa.tecnico.basa.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by joaosampaio on 22-03-2016.
 */
public class SpeechListener implements RecognitionListener {
    String TAG = "SpeechListener";

    //legal commands
    private static final String[] VALID_COMMANDS = {
            "What time is it",
            "que horas são",
            "Ligar luzes",
            "exit"
    };
    private static final int VALID_COMMANDS_SIZE = VALID_COMMANDS.length;

    private SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechIntent;

    public SpeechListener(SpeechRecognizer mSpeechRecognizer, Intent mSpeechIntent) {
        this.mSpeechRecognizer = mSpeechRecognizer;
        this.mSpeechIntent = mSpeechIntent;
    }

    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "buffer recieved ");
    }
    public void onError(int error) {
        //if critical error then exit
        if(error == SpeechRecognizer.ERROR_CLIENT || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS){
            Log.d(TAG, "client error");
        }
        //else ask to repeats
        else{
            Log.d(TAG, "other error:"+error);
            mSpeechRecognizer.startListening(mSpeechIntent);
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
        if(results != null){
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if(matches != null){
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

    private void processCommand(ArrayList<String> matchStrings){
        String response = "I'm sorry, Dave. I'm afraid I can't do that.";
        int maxStrings = matchStrings.size();
        boolean resultFound = false;
        for(int i =0; i < VALID_COMMANDS_SIZE && !resultFound;i++){
            for(int j=0; j < maxStrings && !resultFound; j++){
                if(LevenshteinDistance.getLevenshteinDistance(matchStrings.get(j), VALID_COMMANDS[i]) <(VALID_COMMANDS[i].length() / 3) ){
                    Log.d("response", "found LevenshteinDistance, original:" + matchStrings.get(j)+"****: ");
                    Log.d("response", "found LevenshteinDistance, VALID_COMMANDS:" + VALID_COMMANDS[i]+"****: ");
                    resultFound = true;
                    //response = getResponse(i);
                }
            }
        }
        if(!resultFound)
        Log.d("response", "****" + response+"****: ");


    }


}