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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.detection.IMotionDetection;
import pt.ulisboa.tecnico.basa.detection.ImageProcessing;
import pt.ulisboa.tecnico.basa.detection.RgbMotionDetection;
import pt.ulisboa.tecnico.basa.manager.VideoManager;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.BitmapMotionTransfer;
import pt.ulisboa.tecnico.basa.util.StorageHelper;

/**
 * Created by joaosampaio on 21-02-2016.
 */
public class CameraHelper implements TextureView.SurfaceTextureListener, CameraBasa {

    public static final int VIDEO_LENGTH = 30000;

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
    private long timeOldVideo = 0;
    private long timeCurrent = 0;
    private String latestFilePath = "";
    private String latestFileName = "";
    private Camera.Size sizePreview;
    private boolean recording = false;

    private boolean startRecording = false;
    private boolean callPreview = true;
    private Handler handler;

    public CameraHelper(Launch2Activity act) {
        this.activity = act;
        handler = new Handler();
        recording = false;
        camera_preview = (FrameLayout)act.findViewById(R.id.camera_preview);
        bitmapMotionTransfer = new ArrayList<>();
        setUpSize();
        detector = new RgbMotionDetection();
        mediaRecorder = new MediaRecorder();
        setMuteAll(true);

        String storage = StorageHelper.isExternalStorageReadableAndWritable()? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() : Environment.getDataDirectory().getAbsolutePath();
        File mediaStorageDir = new File(storage + File.separator + "myAssistant/");
        if ( !mediaStorageDir.exists() ) {
            if ( !mediaStorageDir.mkdirs() ){
                Log.d("err", "camera - failed to create directory");
            }
        }

        AppController.getInstance().getBasaManager().getVideoManager().setCommandVideoCamera(new VideoManager.CommandVideoCamera() {
            @Override
            public void startRecording() {
                if(!recording) {
                    startRecording = true;
                    startRecord();
                }
            }

            @Override
            public void stopRecording() {
                startRecording = false;
            }
        });

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
            if (mediaRecorder != null){
                mediaRecorder.stop();

                mediaRecorder.reset();   // clear recorder configuration
                mediaRecorder.release(); // release the recorder object
                mediaRecorder = null;
                mCamera.lock();           // lock camera for later use
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMuteAll(boolean mute) {
        AudioManager manager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        int[] streams = new int[] { AudioManager.STREAM_ALARM,
                AudioManager.STREAM_DTMF,
                AudioManager.STREAM_RING, AudioManager.STREAM_SYSTEM,
                AudioManager.STREAM_VOICE_CALL };
        for (int stream : streams)
            manager.setStreamMute(stream, mute);
    }

    private boolean prepareMediaRecorder(){

        if(mediaRecorder == null)
            mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);

        mediaRecorder.setVideoFrameRate(15);

        String storage = StorageHelper.isExternalStorageReadableAndWritable()? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() : Environment.getDataDirectory().getAbsolutePath();
        File mediaStorageDir = new File(storage + File.separator + "myAssistant/history/");
//        File mediaStorageDir = new File("/sdcard/myAssistant/");
        if ( !mediaStorageDir.exists() ) {
            if ( !mediaStorageDir.mkdirs() ){
                Log.d("err", "camera - failed to create directory");
            }
        }
        latestFileName = System.currentTimeMillis()/1000 + ".mp4";
        latestFilePath = storage + File.separator + "myAssistant/history/" + latestFileName;

        mediaRecorder.setOutputFile(latestFilePath);

        mediaRecorder.setMaxDuration(VIDEO_LENGTH); // Set max duration 5 sec.
        mediaRecorder.setMaxFileSize(5000000); // Set max file size 5M


        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    public void startRecord(){
        Log.d("camera", "startRecord:");

        if(recording){
            Log.d("camera", "startRecord false, already recording");
            return;
        }

        if(!prepareMediaRecorder()){
            Toast.makeText(getActivity(),
                    "Fail in prepareMediaRecorder()!\n - Ended -",
                    Toast.LENGTH_LONG).show();
        }

        mediaRecorder.start();
        recording = true;
        getActivity().findViewById(R.id.viewRecording).setVisibility(View.VISIBLE);
        startRecording = false;
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Log.v("camera","Maximum Duration Reached");
                    mediaRecorder.reset();
                    String path = new String(latestFilePath);
                    String filename = new String(latestFileName);
                    recording = false;
//                    if(getActivity() != null)
                    getActivity().findViewById(R.id.viewRecording).setVisibility(View.GONE);
                    if(startRecording) {
                        startRecord();
                    }

                    if(AppController.getInstance().getBasaManager().getVideoManager() != null){
                        AppController.getInstance().getBasaManager().getVideoManager().addNewHistoryVideo(path, filename.replace(".mp4", ""), filename);
                    }

                }
            }
        });
    }



    public void start_camera(){
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
            CameraHelper.setCameraDisplayOrientation(camera, mCamera);
            sizePreview = mCamera.getParameters().getPreviewSize();
            mTextureView.getLayoutParams().width = sizePreview.width;
            mTextureView.getLayoutParams().height = sizePreview.height;
            mTextureView.requestLayout();
            mCamera.setPreviewTexture(getSurface());
            mCamera.startPreview();
//            mCamera.setPreviewCallback(getPreviewCallback());
            AppController.getInstance().mCameraReady = true;


            handler.postDelayed(timerVideoFrame, 100);

        } catch (IOException ioe) {
            Log.d("cam", "ioe" + ioe.getMessage());
            // Something bad happened
        }
        //startRecord();
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
        AppController.getInstance().getBasaManager().getVideoManager().setCommandVideoCamera(null);
        handler.removeCallbacks(timerVideoFrame);
        releaseMediaRecorder();
        stop_camera();
        if( mTextureView != null){
            camera_preview.removeAllViews();
            mTextureView = null;
        }
    }

    private Runnable timerVideoFrame = new Runnable() {
        @Override
        public void run() {
            long t1 = System.currentTimeMillis();
            Bitmap pic = mTextureView.getBitmap();

            if(AppController.getInstance().getBasaManager().getVideoManager().isLiveStream()) {
                String storage = StorageHelper.isExternalStorageReadableAndWritable() ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() : Environment.getDataDirectory().getAbsolutePath();
                final String latestFileNameF = System.currentTimeMillis() / 100 + ".jpeg";
                final String latestFilePathF = storage + File.separator + "myAssistant/" + latestFileNameF;
                new SavePhotoThread(latestFilePathF, pic, new SavePhotoThread.PhotoSaved() {
                    @Override
                    public void onPhotoBeenSaved(Uri file) {
                        if (AppController.getInstance().getBasaManager().getVideoManager() != null) {
                            AppController.getInstance().getBasaManager().getVideoManager().addNewLivePhoto(latestFilePathF, latestFileNameF.replace(".jpeg", ""), latestFileNameF);
                        }
                    }
                }).start();
            }
            processCameraFrame(pic);
            handler.postDelayed(this, 1000);
        }

    };


    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
//            boolean doDetection = false;
//            if(recording && callPreview){
//                Log.d("camera", "inside preview");
//                callPreview = false;
//                doDetection = true;
//                startRecord();
//            }


            //processCameraFrame(data);

        }
    };


    private void processCameraFrame(byte[] data){
        if(timeOld == 0){
            timeOld = System.currentTimeMillis();
//            timeOldVideo = System.currentTimeMillis();
        }
        timeCurrent = System.currentTimeMillis();

//        if((timeCurrent - timeOldVideo)/1000 >= 0.2){
//
//        }
        long elapsedTimeNs = timeCurrent - timeOld;
        if (elapsedTimeNs/1000 >= AppController.getInstance().timeScanPeriod) {
            timeOld = timeCurrent;

            if (data == null) return;
            Camera.Size size = sizePreview;
            if (size == null) return;

            AppController.getInstance().widthPreview = size.width;
            AppController.getInstance().heightPreview = size.height;
//            if (!GlobalData.isPhoneInMotion()) {
            DetectionThread thread = new DetectionThread(data, size.width, size.height);
            thread.start();


//            }
        }
    }

    private void processCameraFrame(Bitmap data){
//        Log.d("camera", "processCameraFrame");
        if(timeOld == 0){
            timeOld = System.currentTimeMillis();
        }
        timeCurrent = System.currentTimeMillis();
        long elapsedTimeNs = timeCurrent - timeOld;
        if (elapsedTimeNs/1000 >= AppController.getInstance().timeScanPeriod) {
            timeOld = timeCurrent;
            Log.d("camera", "inside preview detection");
            if (data == null) return;

//            ImageProcessing.bitmapToRGB(data);
            AppController.getInstance().widthPreview = data.getWidth();
            AppController.getInstance().heightPreview = data.getHeight();


            DetectionThread thread = new DetectionThread(data);
            thread.start();
        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d("Cam", "onSurfaceTextureAvailable");
        this.surface = surfaceTexture;
        start_camera();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
        Log.d("Cam", "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        Log.d("Cam", "onSurfaceTextureDestroyed");
        stop_camera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
//        Log.d("Cam", "onSurfaceTextureUpdated");
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
        private Bitmap pic;
        private int width;
        private int height;

        public DetectionThread(byte[] data, int width, int height) {
            this.data = data;
            this.width = width;
            this.height = height;
        }

        public DetectionThread(Bitmap bitmap) {
            this.pic = bitmap;
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {

            if (!processing.compareAndSet(false, true)) return;
            try {

                int[] img = null;
                if(data != null)
                    img = ImageProcessing.decodeYUV420SPtoRGB(data, width, height);
                else
                    img = ImageProcessing.bitmapToRGB(pic);

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
