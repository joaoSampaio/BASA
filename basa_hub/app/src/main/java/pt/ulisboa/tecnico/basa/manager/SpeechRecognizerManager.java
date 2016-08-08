/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package pt.ulisboa.tecnico.basa.manager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.event.EventSpeech;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class SpeechRecognizerManager {

    /* Named searches allow to quickly reconfigure the decoder */
    private static final String KWS_SEARCH = "wakeup";
    /* Keyword we are looking for to activate menu */
//    private static final String KEYPHRASE = "ok office";
    private static final String KEYPHRASE = "my assistant";
//    private static final String KEYPHRASE = "ok big boss";
//    private static final String KEYPHRASE = "meu";

    private edu.cmu.pocketsphinx.SpeechRecognizer mPocketSphinxRecognizer;
    private static final String TAG = SpeechRecognizerManager.class.getSimpleName();
    protected Intent mSpeechRecognizerIntent;
    protected android.speech.SpeechRecognizer mGoogleSpeechRecognizer;
    private OnResultListener mOnResultListener;
    private BasaManager basaManager;
    protected long mSpeechRecognizerStartListeningTime = 0;
    private boolean mSuccess;

    public SpeechRecognizerManager( BasaManager basaManager) {
        this.basaManager = basaManager;
        Log.d(TAG, "SpeechRecognizerManager:");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initPockerSphinx();
                initGoogleSpeechRecognizer();
            }
        },2000);

//        mGoogleSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }


    private void initPockerSphinx() {
        Log.d(TAG, "initPockerSphinx:");

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Log.d(TAG, "initPockerSphinx doInBackground:");
                    Assets assets = new Assets(AppController.getAppContext());

                    //Performs the synchronization of assets in the application and external storage
                    File assetDir = assets.syncAssets();

                    //Creates a new SpeechRecognizer builder with a default configuration
                    SpeechRecognizerSetup speechRecognizerSetup = defaultSetup();

                    //Set Dictionary and Acoustic Model files
                    speechRecognizerSetup.setAcousticModel(new File(assetDir, "en-us-ptm"));
                    speechRecognizerSetup.setDictionary(new File(assetDir, "cmudict-en-us.dict"));


//                    speechRecognizerSetup.setAcousticModel(new File(assetDir, "pt-br-semi"))
//                            .setDictionary(new File(assetDir, "pt-br-dict/constituicao.dic"));



//                    speechRecognizerSetup.setRawLogDir(assetDir);
                    // Threshold to tune for keyphrase to balance between false positives and false negatives
//                    speechRecognizerSetup.setKeywordThreshold(1e-45f);
                    speechRecognizerSetup.setKeywordThreshold(1e-25f);


                    //Creates a new SpeechRecognizer object based on previous set up.
                    mPocketSphinxRecognizer = speechRecognizerSetup.getRecognizer();

                    mPocketSphinxRecognizer.addListener(new PocketSphinxRecognitionListener());


                    File digitsGrammar = new File(assetDir, "keys.gram");
                    mPocketSphinxRecognizer.addKeywordSearch(KWS_SEARCH, digitsGrammar);


                    // Create keyword-activation search.
//                    mPocketSphinxRecognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
                    Log.d(TAG, "initPockerSphinx end:");
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    Toast.makeText(AppController.getAppContext(), "Failed to init mPocketSphinxRecognizer ", Toast.LENGTH_SHORT).show();
                } else {
                    restartSearch(KWS_SEARCH);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public BasaManager getBasaManager() {
        return basaManager;
    }

    private void initGoogleSpeechRecognizer() {

        mGoogleSpeechRecognizer = android.speech.SpeechRecognizer
                .createSpeechRecognizer(AppController.getAppContext());

        mGoogleSpeechRecognizer.setRecognitionListener(new GoogleRecognitionListener());

        mSpeechRecognizerIntent = new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
//        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-PT");
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"pt.ulisboa.tecnico.basa");

        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechRecognizerIntent.putExtra( RecognizerIntent. EXTRA_CONFIDENCE_SCORES, true);
    }


    public void destroy() {
        if (mPocketSphinxRecognizer != null) {
            mPocketSphinxRecognizer.cancel();
            mPocketSphinxRecognizer.shutdown();
            mPocketSphinxRecognizer = null;
        }


        if (mGoogleSpeechRecognizer != null) {
            mGoogleSpeechRecognizer.cancel();
            ;
            mGoogleSpeechRecognizer.destroy();
            mPocketSphinxRecognizer = null;
        }

    }

    private void restartSearch(String searchName) {
        Log.d(TAG, "restartSearch:");
        mPocketSphinxRecognizer.stop();

        mPocketSphinxRecognizer.startListening(searchName);

    }



    protected class PocketSphinxRecognitionListener implements edu.cmu.pocketsphinx.RecognitionListener {

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
        }


        /**
         * In partial result we get quick updates about current hypothesis. In
         * keyword spotting mode we can react here, in other modes we need to wait
         * for final result in onResult.
         */
        @Override
        public void onPartialResult(Hypothesis hypothesis) {
            if (hypothesis == null)
            {
                Log.d(TAG, "hypothesis == null:");
                return;
            }


            String text = hypothesis.getHypstr();
            String[] results= text.split("  ");
            Log.d(TAG, "onPartialResult:" + text);
            Log.d(TAG, "last result:" + results[0]);
            if (results[0].trim().equals(KEYPHRASE)) {

                Log.d(TAG, "mGoogleSpeechRecognizer.startListening:");
                speechRecognizerStartListening(mSpeechRecognizerIntent);

//                mPocketSphinxRecognizer.stop();
                mPocketSphinxRecognizer.cancel();

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d(TAG, "mGoogleSpeechRecognizer.startListening:");
//                        mGoogleSpeechRecognizer.startListening(mSpeechRecognizerIntent);
//                    }
//                },1000);



                Toast.makeText(AppController.getAppContext(), "You said: " + text, Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onResult(Hypothesis hypothesis) {
            String text = hypothesis.getHypstr();
            Log.d(TAG, "onResult->" + text);
        }


        /**
         * We stop mPocketSphinxRecognizer here to get a final result
         */
        @Override
        public void onEndOfSpeech() {

        }

        public void onError(Exception error) {
        }

        @Override
        public void onTimeout() {
        }

    }


    protected class GoogleRecognitionListener implements
            android.speech.RecognitionListener {

        private final String TAG = GoogleRecognitionListener.class
                .getSimpleName();

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "mGoogleSpeechRecognizer.onBeginningOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "mGoogleSpeechRecognizer.onEndOfSpeech:");
        }

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "mGoogleSpeechRecognizer.onReadyForSpeech:");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
//            Log.d(TAG, "mGoogleSpeechRecognizer.onRmsChanged:");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "mGoogleSpeechRecognizer.onBufferReceived:");
        }

        @Override
        public synchronized void onError(int error) {
            Log.e(TAG, "onError:" + error);


            if (mSuccess) {
                Log.e(TAG, "Already success, ignoring error");
                return;
            }
            long duration = System.currentTimeMillis() - mSpeechRecognizerStartListeningTime;
            if (duration < 500 && error == SpeechRecognizer.ERROR_NO_MATCH) {
                Log.e(TAG, "Doesn't seem like the system tried to listen at all. duration = " + duration + "ms. This might be a bug with onError and startListening methods of SpeechRecognizer");
                Log.e(TAG, "Going to ignore the error");
                return;
            }
            if (duration < 500 && error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {

                Log.e(TAG, "Going to ignore the error ERROR_RECOGNIZER_BUSY");

                return;
            }

            if (duration > 5000 && error == SpeechRecognizer.ERROR_NO_MATCH) {
                mGoogleSpeechRecognizer.cancel();
                mPocketSphinxRecognizer.startListening(KWS_SEARCH);
            }

            speechRecognizerStartListening(mSpeechRecognizerIntent);
//            mGoogleSpeechRecognizer.cancel();

//
            Log.d(TAG, "getBasaManager().getTextToSpeechManager():");
//            if(getBasaManager().getTextToSpeechManager() != null)
//                getBasaManager().getTextToSpeechManager().speak("I'm sorry please repeat");

            Log.d(TAG, "mPocketSphinxRecognizer.startListening do erro google:");



//            mPocketSphinxRecognizer.startListening(KWS_SEARCH);
            //mGoogleSpeechRecognizer.startListening(mSpeechRecognizerIntent);

        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResultsheard google:");

        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "mGoogleSpeechRecognizer.onResults:");
            if ((results != null)
                    && results
                    .containsKey(android.speech.SpeechRecognizer.RESULTS_RECOGNITION)) {

                mSuccess = true;

                ArrayList<String> heard = results
                        .getStringArrayList(android.speech.SpeechRecognizer.RESULTS_RECOGNITION);
                float[] scores = results
                        .getFloatArray(android.speech.SpeechRecognizer.CONFIDENCE_SCORES);

                for (int i = 0; i < heard.size(); i++) {
                    Log.d(TAG, "onResultsheard:" + heard.get(i)
                            + " confidence:" + scores[i]);

                }

                getBasaManager().getEventManager().addEvent(new EventSpeech(heard));

                //send list of words to activity
                if (mOnResultListener!=null){
                    mOnResultListener.OnResult(heard);
                }

            }
            Log.d(TAG, "mPocketSphinxRecognizer.startListening:");
            mPocketSphinxRecognizer.startListening(KWS_SEARCH);
            mGoogleSpeechRecognizer.cancel();
//            speechRecognizerStartListening(mSpeechRecognizerIntent);

        }


        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "mGoogleSpeechRecognizer.onEvent:");
        }

    }


    protected synchronized void speechRecognizerStartListening(Intent intent) {
        if (mGoogleSpeechRecognizer != null) {
            mSuccess = false;
            this.mSpeechRecognizerStartListeningTime = System.currentTimeMillis();
            this.mGoogleSpeechRecognizer.startListening(intent);
        }
    }


    public void setOnResultListner(OnResultListener onResultListener){
        mOnResultListener=onResultListener;
    }



    public interface OnResultListener
    {
        public void OnResult(ArrayList<String> commands);
    }
}
