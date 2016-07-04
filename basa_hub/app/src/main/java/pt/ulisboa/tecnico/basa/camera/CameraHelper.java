package pt.ulisboa.tecnico.basa.camera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;



import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.detection.IMotionDetection;
import pt.ulisboa.tecnico.basa.detection.ImageProcessing;
import pt.ulisboa.tecnico.basa.detection.RgbMotionDetection;
import pt.ulisboa.tecnico.basa.model.Event;
import pt.ulisboa.tecnico.basa.model.EventOccupantDetected;
import pt.ulisboa.tecnico.basa.ui.MainActivity;
import pt.ulisboa.tecnico.basa.ui.secondary.CameraSettingsDialogFragment;

/**
 * Created by joaosampaio on 21-02-2016.
 */
public class CameraHelper implements TextureView.SurfaceTextureListener {

    private MainActivity activity;
    private CameraSettingsDialogFragment.BitmapMotionTransfer bitmapMotionTransfer;
    private int previewCount = 0;
    private static volatile AtomicBoolean processing = new AtomicBoolean(false);
    private IMotionDetection detector = null;
    private SurfaceTexture surface;
    private int qrCodeSampleTime = 35;
    private long timeOld = 0;
    private long timeCurrent = 0;

    public CameraHelper(MainActivity act) {
        this.activity = act;
        setUpSize();
        detector = new RgbMotionDetection();
    }

    private void setUpSize(){
        int width,height;
        Display d = ((WindowManager)activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        width = d.getWidth();
        height = d.getHeight();
        AppController.getInstance().width = width;
        AppController.getInstance().height = height;

        SharedPreferences sp = getActivity().getSharedPreferences("BASA", Activity.MODE_PRIVATE);
        AppController.getInstance().skipTop = sp.getInt(Global.skipTop, 0);
        AppController.getInstance().skipBottom = sp.getInt(Global.skipBottom, 0);
        AppController.getInstance().skipLeft = sp.getInt(Global.skipLeft, 0);
        AppController.getInstance().skipRight = sp.getInt(Global.skipRight, 0);
    }

    public static void setCameraDisplayOrientation(
            int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        AppController app = AppController.getInstance();
        int degrees = 0;
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(0);

        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            //Choose another supported mode
        }

        //temos de trocar os valores caso a width seja mais baixa que o height
        int maxWidth = app.width > app.height? app.width : app.height;
        int maxHeight = app.width > app.height? app.height : app.width;


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            //nao funciona
            List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
            Camera.Size sizeScreen = sizes.get(0);
            for (int i = 0; i < sizes.size(); i++) {
//                Log.d("myapp", "size.width: " + sizes.get(i).width + " size.height: " + +sizes.get(i).height);
                if (sizes.get(i).width > sizeScreen.width)
                    sizeScreen = sizes.get(i);
                if(sizes.get(i).height == maxHeight) {
                    sizeScreen = sizes.get(i);
                   // break;
                }
            }
            sizeScreen = sizes.get(sizes.size()-1);

//            Log.d("MyCameraApp", "sizeScreen size.width: " + sizeScreen.width + " size.height: " + sizeScreen.height);
            parameters.setPreviewSize(sizeScreen.width, sizeScreen.height);

            sizes = parameters.getSupportedPictureSizes();
            Camera.Size sizeCamera = sizes.get(0);
            for (int i = 0; i < sizes.size(); i++) {

                if (sizes.get(i).width > sizeCamera.width)
                    sizeCamera = sizes.get(i);
                if(sizes.get(i).height == maxHeight) {
                    sizeCamera = sizes.get(i);
                    break;
                }
            }

//            Log.d("MyCameraApp", "best size.width: " + sizeCamera.width + " size.height: " + sizeCamera.height);
            parameters.setPictureSize(sizeCamera.width, sizeCamera.height);
        }

        parameters.setPictureFormat(PixelFormat.JPEG);
        parameters.set("jpeg-quality", 90);

        parameters.set("orientation", "landscape");
        degrees = 0;
        degrees = 90;
        if(cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
            degrees = 270;
        //parameters.set("rotation", degrees);
        parameters.setRotation(degrees);
        camera.setParameters(parameters);
    }





    public MainActivity getActivity() {
        return activity;
    }

    public void stop_camera(){
        getActivity().stop_camera();
    }



    private void start_camera() {
        getActivity().start_camera();
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {


            if(timeOld == 0){
                timeOld = System.currentTimeMillis();
                timeCurrent = timeOld;
                return;
            }
            timeCurrent = System.currentTimeMillis();
            long elapsedTimeNs = timeCurrent - timeOld;
            if (elapsedTimeNs/1000 >= AppController.getInstance().timeScanPeriod) {
                timeOld = timeCurrent;


                if (data == null) return;
                Camera.Size size = cam.getParameters().getPreviewSize();
                if (size == null) return;

                AppController.getInstance().widthPreview = size.width;
                AppController.getInstance().heightPreview = size.height;
//            if (!GlobalData.isPhoneInMotion()) {
                DetectionThread thread = new DetectionThread(data, size.width, size.height);
                thread.start();

                getActivity().getVideoManager().sendImagePacket(data);

//            }
            }
        }
    };

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d("Cam", "onSurfaceTextureAvailable");
        this.surface = surfaceTexture;
        start_camera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.d("Cam", "onSurfaceTextureDestroyed");
        stop_camera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }


    public SurfaceTexture getSurface() {
        return surface;
    }

    public void setSurface(SurfaceTexture surface) {
        this.surface = surface;
    }

    public Camera.PreviewCallback getPreviewCallback() {
        return previewCallback;
    }

    public CameraSettingsDialogFragment.BitmapMotionTransfer getBitmapMotionTransfer() {
        return bitmapMotionTransfer;
    }

    public void setBitmapMotionTransfer(CameraSettingsDialogFragment.BitmapMotionTransfer bitmapMotionTransfer) {
        this.bitmapMotionTransfer = bitmapMotionTransfer;
    }

    public final class DetectionThread extends Thread {

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
                    //img foi alterado


                }else{

                    detected(false);
                }
                if(getBitmapMotionTransfer() != null) {
                    final Bitmap b = ImageProcessing.rgbToBitmap(img, width, height);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (getBitmapMotionTransfer() != null) {
                                getBitmapMotionTransfer().onBitMapAvailable(b);
                            }
//                                Log.d("UI thread", "I am the UI thread");
//                                getActivity().getPreview_img().setImageBitmap(b);
//                                getActivity().getPreview_img().bringToFront();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                processing.set(false);
            }

            processing.set(false);
        }
    };

    private void detected(final boolean isDetected){

        new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    getActivity().getBasaManager().getEventManager().addEvent(new EventOccupantDetected(Event.OCCUPANT_DETECTED, isDetected));
                }
            });

        if(isDetected) {
//            Log.d("app", "has detected");
//            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
//            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);

        }

    }

    public IMotionDetection getDetector() {
        return detector;
    }
}