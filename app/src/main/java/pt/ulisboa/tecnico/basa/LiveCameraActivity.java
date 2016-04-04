package pt.ulisboa.tecnico.basa;

/**
 * Created by joaosampaio on 10-02-2016.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.TextureView;



import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;

import pt.ulisboa.tecnico.basa.detection.AggregateLumaMotionDetection;
import pt.ulisboa.tecnico.basa.detection.IMotionDetection;
import pt.ulisboa.tecnico.basa.detection.ImageProcessing;
import pt.ulisboa.tecnico.basa.detection.LumaMotionDetection;
import pt.ulisboa.tecnico.basa.detection.RgbMotionDetection;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class LiveCameraActivity extends Activity implements TextureView.SurfaceTextureListener {
    private static final String TAG = "joao";

    private Camera mCamera;
    private TextureView mTextureView;
    Activity me;

    private static volatile AtomicBoolean processing = new AtomicBoolean(false);
    private static IMotionDetection detector = null;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("app", "onCreate");

        detector = new RgbMotionDetection();
//        if (Preferences.USE_RGB) {
//            detector = new RgbMotionDetection();
//        } else if (Preferences.USE_LUMA) {
//            detector = new LumaMotionDetection();
//        } else {
//            // Using State based (aggregate map)
//            detector = new AggregateLumaMotionDetection();
//        }

        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        me = this;
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);

        setContentView(mTextureView);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d("app", "onSurfaceTextureAvailable");
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//        mCamera = Camera.open();

        if (mCamera == null) {
            // Seeing this on Nexus 7 2012 -- I guess it wants a rear-facing camera, but
            // there isn't one.  TODO: fix
            throw new RuntimeException("Default camera not available");
        }

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            mCamera.setPreviewCallback(previewCallback);
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("app", "onSurfaceTextureDestroyed");
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
        //Log.d(TAG, "updated, ts=" + surface.getTimestamp());
        //surface.
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
//            Log.d("teste", "onPreviewFrame");
            if (data == null) return;
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) return;

//            if (!GlobalData.isPhoneInMotion()) {
                DetectionThread thread = new DetectionThread(data, size.width, size.height);
                thread.start();
//            }
        }
    };







    public static final class DetectionThread extends Thread {

        private byte[] data;
        private int width;
        private int height;

        public DetectionThread(byte[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {


            if (!processing.compareAndSet(false, true)) return;

            // Log.d(TAG, "BEGIN PROCESSING...");
            try {

                int[] img = null;
                    img = ImageProcessing.decodeYUV420SPtoRGB(data, width, height);


//                Log.d("teste", "width->" + width + " height->" + height);
//                Log.i(TAG, "img != null ->" + (img != null));
//                Log.i(TAG, "detector.detect(img, width, height)->" + (detector.detect(img, width, height)) );

                if (img != null && detector.detect(img, width, height)) {

                    detected(true);
                }else{

                    detected(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                processing.set(false);
            }

            processing.set(false);
        }
    };

    private static void detected(boolean hasDetected){
        if(hasDetected) {
            Log.d("app", "has detected");
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);

        }
        else
            Log.d("app", "...");
    }

    public  void turnOnScreen(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // turn on screen
                Log.v("ProximityActivity", "ON!");
                mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
                mWakeLock.acquire();


            }
        });

    }

    @TargetApi(21) //Suppress lint error for PROXIMITY_SCREEN_OFF_WAKE_LOCK
    public void turnOffScreen(){
        // turn off screen
        Log.v("ProximityActivity", "OFF!");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "tag");
        mWakeLock.acquire();
    }




}