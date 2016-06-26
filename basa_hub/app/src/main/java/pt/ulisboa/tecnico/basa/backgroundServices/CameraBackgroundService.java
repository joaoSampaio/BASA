package pt.ulisboa.tecnico.basa.backgroundServices;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import pt.ulisboa.tecnico.basa.detection.IMotionDetection;
import pt.ulisboa.tecnico.basa.detection.ImageProcessing;
import pt.ulisboa.tecnico.basa.detection.RgbMotionDetection;

/**
 * Created by joaosampaio on 10-02-2016.
 */
public class CameraBackgroundService extends Service implements TextureView.SurfaceTextureListener {

    private final IBinder mBinder = new LocalBinder();
    private static String TAG = "teste";
    private Camera mCamera;
    private TextureView mTextureView;
    Activity me;

    private static volatile AtomicBoolean processing = new AtomicBoolean(false);
    private static IMotionDetection detector = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "--------------------------------stop");


       // this.clean();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("app", "onStartCommand");
        detector = new RgbMotionDetection();
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);

        SurfaceView view = new SurfaceView(getApplicationContext());


        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//        mCamera = Camera.open();

        if (mCamera == null) {
            // Seeing this on Nexus 7 2012 -- I guess it wants a rear-facing camera, but
            // there isn't one.  TODO: fix
            throw new RuntimeException("Default camera not available");
        }

        try {
            mCamera.setPreviewDisplay(view.getHolder());
            mCamera.startPreview();
            mCamera.setPreviewCallback(previewCallback);
        } catch (IOException ioe) {
            // Something bad happened
        }

        return Service.START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "--------------------------------onTaskRemoved");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }




    public class LocalBinder extends Binder {
        public CameraBackgroundService getServiceInstance(){
            return CameraBackgroundService.this;
        }
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

}
