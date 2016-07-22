package pt.ulisboa.tecnico.basa.util;

import android.media.AudioRecord;
import android.util.Log;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import pt.ulisboa.tecnico.basa.model.event.EventClap;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;

/**
 * Created by joaosampaio on 25-03-2016.
 */
public class ClapListener implements OnsetHandler {

//    static final int SAMPLE_RATE = 8000;
    static final int SAMPLE_RATE = 22050;
    private PercussionOnsetDetector mPercussionOnsetDetector;
    private TarsosDSPAudioFormat tarsosFormat;
    private int clap;
    private boolean mIsRecording;
    private byte[] buffer;
    private AudioRecord recorder;
    private long timeOld = 0, stopTime = 0;
    private Launch2Activity activity;





    AudioDispatcher dispatcher;

    public ClapListener(Launch2Activity activity){
        this.activity = activity;
        clap = 0;
//        setUpNew(0.2, 0.5);
        setUpNew(20, 8);
//        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE,1024,0);
//        PitchDetectionHandler pdh = new PitchDetectionHandler() {
//            @Override
//            public void handlePitch(PitchDetectionResult result,AudioEvent e) {
//                final float pitchInHz = result.getPitch();
//                if(pitchInHz != -1)
//                    Log.d("clap", "pitchInHz: " + pitchInHz);
//                else
//                    Log.d("else", "pitchInHz: " + pitchInHz);
//            }
//        };
//        AudioProcessor p1 = new PercussionOnsetDetector(SAMPLE_RATE, 1024, this, 24,5);
//
//
//        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.DYNAMIC_WAVELET, SAMPLE_RATE, 1024, pdh);
////        dispatcher.addAudioProcessor(p);
//        dispatcher.addAudioProcessor(p1);
//        new Thread(dispatcher,"Audio Dispatcher").start();

    }


    public void setUpNew(double sensitivity, double threshold){
        //sensitivity = 0.2, threshold =0.5;
        Log.d("Percussion", "sensitivity: " + sensitivity + " threshold: " + threshold);
        if(dispatcher != null){
            dispatcher.stop();
        }
//        int SAMPLE_RATE = 44100;
        int bufferSize = 1024;
        int overlap = 0;

        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(SAMPLE_RATE,bufferSize, overlap);
        AudioProcessor p1 = new PercussionOnsetDetector(SAMPLE_RATE, bufferSize, this, sensitivity, threshold);
        dispatcher.addAudioProcessor(p1);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }




    @Override
    public void handleOnset(double time, double salience) {
        Log.d("Percussion", "claplistenner called: time:"+time + " stopTime:"+stopTime);
        if((long)time < stopTime)
            return;

        if(timeOld == 0){
            timeOld = (long)time;
        }
        long elapsedTimeNs = (long)time - timeOld;
        if (elapsedTimeNs/1000 <= 5000) {
            timeOld = (long)time;;
            clap += 1;
        }else{
            clap = 1;
        }

        Log.d("Percussion", "salience: " + salience + "  cap num:"+clap);
        // have we detected a pitch?
        if (clap == 2) {
            clap = 0;
            stopTime = (long)time + 5;
            activity.getBasaManager().getEventManager().addEvent(new EventClap());

            // handlePitch will be run from a background thread
            // so we need to run it on the UI thread



        }
    }

    public void stop(){
        if(dispatcher != null){
            dispatcher.stop();
        }
    }






}
