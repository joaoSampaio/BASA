package pt.ulisboa.tecnico.basa.util;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.util.Log;

public class BugRecognitionListener implements RecognitionListener {

    private final String CLS_NAME = BugRecognitionListener.class.getSimpleName();

    private boolean doError;
    private boolean doEndOfSpeech;
    private boolean doBeginningOfSpeech;

    public void resetBugVariables() {
        Log.i(CLS_NAME, "resetBugVariables");

        doError = false;
        doEndOfSpeech = false;
        doBeginningOfSpeech = false;
    }

    /**
     * Called when the endpointer is ready for the user to start speaking.
     *
     * @param params parameters set by the recognition service. Reserved for future use.
     */
    @Override
    public void onReadyForSpeech(final Bundle params) {
        doError = true;
        doEndOfSpeech = true;
        doBeginningOfSpeech = true;
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(CLS_NAME, "onBeginningOfSpeech: doEndOfSpeech: " + doEndOfSpeech);
        Log.i(CLS_NAME, "onBeginningOfSpeech: doError: " + doError);
        Log.i(CLS_NAME, "onBeginningOfSpeech: doBeginningOfSpeech: " + doBeginningOfSpeech);

        if (doBeginningOfSpeech) {
            doBeginningOfSpeech = false;
            onBeginningOfRecognition();
        }

    }

    public void onBeginningOfRecognition() {
    }

    @Override
    public void onRmsChanged(final float rmsdB) {
    }

    @Override
    public void onBufferReceived(final byte[] buffer) {
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(CLS_NAME, "onEndOfSpeech: doEndOfSpeech: " + doEndOfSpeech);
        Log.i(CLS_NAME, "onEndOfSpeech: doError: " + doError);
        Log.i(CLS_NAME, "onEndOfSpeech: doBeginningOfSpeech: " + doBeginningOfSpeech);

        if (doEndOfSpeech) {
            onEndOfRecognition();
        }
    }

    public void onEndOfRecognition() {
    }

    @Override
    public void onError(final int error) {
        Log.w(CLS_NAME, "onError: doEndOfSpeech: " + doEndOfSpeech);
        Log.w(CLS_NAME, "onError: doError: " + doError);
        Log.w(CLS_NAME, "onError: doBeginningOfSpeech: " + doBeginningOfSpeech);

        if (doError) {
            onRecognitionError(error);
        }
    }

    public void onRecognitionError(final int error) {
    }

    @Override
    public void onResults(final Bundle results) {
    }

    @Override
    public void onPartialResults(final Bundle partialResults) {
    }

    @Override
    public void onEvent(final int eventType, final Bundle params) {
    }

    // USAGE
    private final BugRecognitionListener recognitionListener = new BugRecognitionListener() {

        /**
         * MUST CALL SUPER!
         */
        @Override
        public void onReadyForSpeech(final Bundle params) {
            super.onReadyForSpeech(params);
        }

        /**
         * Instead of {@link RecognitionListener#onEndOfSpeech()}
         */
        @Override
        public void onEndOfRecognition() {
        }

        /**
         * Instead of {@link RecognitionListener#onError(int)}
         *
         * @param error the error code
         */
        @Override
        public void onRecognitionError(final int error) {
        }

        /**
         * Instead of {@link RecognitionListener#onBeginningOfSpeech()}
         */
        @Override
        public void onBeginningOfRecognition() {
        }

        @Override
        public void onBufferReceived(final byte[] buffer) {
        }

        @Override
        public void onEvent(final int eventType, final Bundle params) {
        }

        @Override
        public void onPartialResults(final Bundle partialResults) {
        }

        @Override
        public void onResults(final Bundle results) {
        }

        @Override
        public void onRmsChanged(final float rmsdB) {
        }
    };
}