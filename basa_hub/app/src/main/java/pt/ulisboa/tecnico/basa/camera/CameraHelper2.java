package pt.ulisboa.tecnico.basa.camera;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.detection.IMotionDetection;
import pt.ulisboa.tecnico.basa.detection.ImageProcessing;
import pt.ulisboa.tecnico.basa.detection.RgbMotionDetection;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.BitmapMotionTransfer;

/**
 * Created by joaosampaio on 21-02-2016.
 */
public class CameraHelper2 implements TextureView.SurfaceTextureListener, CameraBasa {



    final int RECORD_LENGTH = 5;
    private final static String CLASS_LABEL = "RecordActivity";
    private final static String LOG_TAG = CLASS_LABEL;
    long startTime = 0;
    boolean recording = false;
    Frame[] images;
    long[] timestamps;
    ShortBuffer[] samples;
    int imagesIndex, samplesIndex;
//    private File ffmpeg_link = new File(Environment.getExternalStorageDirectory(), "stream.mp4");
    private volatile FFmpegFrameRecorder recorder;
    private boolean isPreviewOn = false;
    private int sampleAudioRateInHz = 44100;
    private int imageWidth = 320;
    private int imageHeight = 240;
    private int frameRate = 5;
    private Frame yuvImage = null;

    private MediaRecorder mediaRecorder;
    private Camera mCamera;
    private TextureView mTextureView;
    private FrameLayout camera_preview;
    private Launch2Activity activity;
    private List<BitmapMotionTransfer> bitmapMotionTransfer;
    private static volatile AtomicBoolean processing = new AtomicBoolean(false);
    private IMotionDetection detector = null;
    private SurfaceTexture surface;
    private long timeOld = 0;
    private long timeCurrent = 0;
    private String latestFilePath = "";
    private Handler handler;


    private Runnable cameraTimer = new Runnable() {
        @Override
        public void run() {

            stopRecording();
            startRecording();

            handler.postDelayed(this, 20000);
        }
    };

    public CameraHelper2(Launch2Activity act) {
        this.activity = act;
        handler = new Handler();
        camera_preview = (FrameLayout)act.findViewById(R.id.camera_preview);
        bitmapMotionTransfer = new ArrayList<>();
        setUpSize();
        detector = new RgbMotionDetection();
        mediaRecorder = new MediaRecorder();
        setMuteAll(true);
//        initRecorder();
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

    public void setCameraDisplayOrientation(
            int cameraId, Camera camera) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
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
            Log.d("camera", "setPreviewSize sizeScreen.width:" + sizeScreen.width + " sizeScreen.height:"+sizeScreen.height);
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
            Log.d("camera", "setPictureSize sizeScreen.width:" + sizeScreen.width + " sizeScreen.height:"+sizeScreen.height);
            parameters.setPictureSize(sizeCamera.width, sizeCamera.height);
            imageWidth = sizeCamera.width;
            imageHeight = sizeCamera.height;
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





    public Launch2Activity getActivity() {
        return activity;
    }


    private void releaseMediaRecorder(){
        try {
            if (handler != null){
                handler.removeCallbacks(cameraTimer);
                handler = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //---------------------------------------
    // initialize ffmpeg_recorder
    //---------------------------------------
    private void initRecorder() {

        Log.w(LOG_TAG, "init recorder");

        if(RECORD_LENGTH > 0) {
            imagesIndex = 0;
            images = new Frame[RECORD_LENGTH * frameRate];
            timestamps = new long[images.length];
            for(int i = 0; i < images.length; i++) {
                images[i] = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
                timestamps[i] = -1;
            }
        } else if(yuvImage == null) {
            yuvImage = new Frame(imageWidth, imageHeight, Frame.DEPTH_UBYTE, 2);
            Log.i(LOG_TAG, "create yuvImage");
        }


        File mediaStorageDir = new File("/sdcard/myAssistant/");
        if ( !mediaStorageDir.exists() ) {
            if ( !mediaStorageDir.mkdirs() ){
                Log.d("err", "camera - failed to create directory");
            }
        }
        latestFilePath = "/sdcard/myAssistant/" + System.currentTimeMillis() + ".mp4";


        Log.i(LOG_TAG, "ffmpeg_url: " + latestFilePath);
        recorder = new FFmpegFrameRecorder(latestFilePath, imageWidth, imageHeight, 0);
        recorder.setFormat("mp4");
        recorder.setSampleRate(sampleAudioRateInHz);
        // Set in the surface changed method
        recorder.setFrameRate(frameRate);

        Log.i(LOG_TAG, "recorder initialize success");


    }

    public void startRecording() {
        initRecorder();

        try {
            recorder.start();
            startTime = System.currentTimeMillis();
            recording = true;
        } catch(FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {

        if(recorder != null && recording) {
            if(RECORD_LENGTH > 0) {
                Log.v(LOG_TAG, "Writing frames");
                try {
                    int firstIndex = imagesIndex % samples.length;
                    int lastIndex = (imagesIndex - 1) % images.length;
                    if(imagesIndex <= images.length) {
                        firstIndex = 0;
                        lastIndex = imagesIndex - 1;
                    }
                    if((startTime = timestamps[lastIndex] - RECORD_LENGTH * 1000000L) < 0) {
                        startTime = 0;
                    }
                    if(lastIndex < firstIndex) {
                        lastIndex += images.length;
                    }
                    for(int i = firstIndex; i <= lastIndex; i++) {
                        long t = timestamps[i % timestamps.length] - startTime;
                        if(t >= 0) {
                            if(t > recorder.getTimestamp()) {
                                recorder.setTimestamp(t);
                            }
                            recorder.record(images[i % images.length]);
                        }
                    }

                    firstIndex = samplesIndex % samples.length;
                    lastIndex = (samplesIndex - 1) % samples.length;
                    if(samplesIndex <= samples.length) {
                        firstIndex = 0;
                        lastIndex = samplesIndex - 1;
                    }
                    if(lastIndex < firstIndex) {
                        lastIndex += samples.length;
                    }
                    for(int i = firstIndex; i <= lastIndex; i++) {
                        recorder.recordSamples(samples[i % samples.length]);
                    }
                } catch(FFmpegFrameRecorder.Exception e) {
                    Log.v(LOG_TAG, e.getMessage());
                    e.printStackTrace();
                }
            }

            recording = false;
            Log.v(LOG_TAG, "Finishing recording, calling stop and release on recorder");
            try {
                recorder.stop();
                recorder.release();
            } catch(FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            recorder = null;

        }
    }


    private void setMuteAll(boolean mute) {
        AudioManager manager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        int[] streams = new int[] { AudioManager.STREAM_ALARM,
                AudioManager.STREAM_DTMF, AudioManager.STREAM_MUSIC,
                AudioManager.STREAM_RING, AudioManager.STREAM_SYSTEM,
                AudioManager.STREAM_VOICE_CALL };

        for (int stream : streams)
            manager.setStreamMute(stream, mute);
    }






    public void start_camera(){
        Log.d("cam", "start_camera " + (mCamera == null));
        if(mCamera != null)
            return;


        if( mTextureView == null){
            mTextureView = new TextureView(getActivity());
            mTextureView.setSurfaceTextureListener(this);
            camera_preview.addView(mTextureView);
            return;
        }
        int camera = 1;
        mCamera = Camera.open(camera);
        try {
            setCameraDisplayOrientation(camera, mCamera);
            mCamera.setPreviewTexture(getSurface());
            mCamera.startPreview();
            mCamera.setPreviewCallback(getPreviewCallback());
            AppController.getInstance().mCameraReady = true;

        } catch (IOException ioe) {
            Log.d("cam", "ioe" + ioe.getMessage());
            // Something bad happened
        }
        startRecording();
        handler.postDelayed(cameraTimer, 20000);


    }

    public void stop_camera(){
        if(mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            AppController.getInstance().mCameraReady = false;

        }
    }

    public void destroy(){
        releaseMediaRecorder();
        stop_camera();
        if( mTextureView != null){
            camera_preview.removeAllViews();
            mTextureView = null;
        }
    }

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {

            Log.d("preview", "startTime:"+startTime);
            if(startTime == 0) {
                startTime = System.currentTimeMillis();
                return;
            }
            if(RECORD_LENGTH > 0) {
                int i = imagesIndex++ % images.length;
                yuvImage = images[i];
                timestamps[i] = 1000 * (System.currentTimeMillis() - startTime);
            }
            /* get video data */
            if(yuvImage != null && recording) {
                ((ByteBuffer) yuvImage.image[0].position(0)).put(data);

                if(RECORD_LENGTH <= 0) {
                    try {
                        Log.v(LOG_TAG, "Writing Frame");
                        long t = 1000 * (System.currentTimeMillis() - startTime);
                        if(t > recorder.getTimestamp()) {
                            recorder.setTimestamp(t);
                        }
                        recorder.record(yuvImage);
                    } catch(FFmpegFrameRecorder.Exception e) {
                        Log.v(LOG_TAG, e.getMessage());
                        e.printStackTrace();
                    }
                }
            }





            Log.d("Cam", "previewCallback");
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

    public List<BitmapMotionTransfer> getBitmapMotionTransfer() {
        return bitmapMotionTransfer;
    }

    @Override
    public void addImageListener(BitmapMotionTransfer bitmapMotion ){

        for (BitmapMotionTransfer transfer : bitmapMotionTransfer)
            if(transfer == bitmapMotion)
                return;

        bitmapMotionTransfer.add(bitmapMotion);
    }

    @Override
    public void removeImageListener(BitmapMotionTransfer bitmapMotion ){
        bitmapMotionTransfer.remove(bitmapMotion);
    }

//    public void setBitmapMotionTransfer(CameraSettingsDialogFragment.BitmapMotionTransfer bitmapMotionTransfer) {
//        this.bitmapMotionTransfer = bitmapMotionTransfer;
//    }

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
//                    AppController.getInstance().getBasaManager().getEventManager().addEvent(new );


                }
                if(!getBitmapMotionTransfer().isEmpty()) {
                    final Bitmap b = ImageProcessing.rgbToBitmap(img, width, height);
                    for(final BitmapMotionTransfer  transfer : getBitmapMotionTransfer()){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                    transfer.onBitMapAvailable(b);

                            }
                        });
                    }

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

                AppController.getInstance().getBasaManager().getBasaSensorManager().setMotionSensorDetected(isDetected);

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
