package pt.ulisboa.tecnico.basa.manager;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.TreeMap;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventOccupantDetected;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.firebase.FirebaseFileLink;
import pt.ulisboa.tecnico.basa.util.FirebaseHelper;
import pt.ulisboa.tecnico.basa.util.StorageHelper;

/**
 * Created by Sampaio on 16/04/2016.
 */
public class VideoManager {

    private TreeMap<String, FirebaseFileLink> liveVideo;
    private long timeStartLiveStreaming = 0;
    private boolean liveStream = false;
    private Handler handler;
    private InterestEventAssociation interest;
    private BasaManager basaManager;
    private CommandVideoCamera commandVideoCamera;
    private long timeLastMovement = 0;
    private String storage;
    private Runnable timerLiveStream = new Runnable() {
        @Override
        public void run() {
            long timeSince = System.currentTimeMillis() - timeStartLiveStreaming;
            if(liveStream && timeSince < 250000){
                //is set to live stream ans has been updated in last 25s

                handler.postDelayed(this, 25000);
            }else{
                liveStream = false;
                new FirebaseHelper().enableLiveStreaming(liveStream);
            }
        }
    };
    public VideoManager(BasaManager basaManager){
        this.basaManager = basaManager;
        liveVideo = new TreeMap <>();
        handler = new Handler();
        storage = StorageHelper.isExternalStorageReadableAndWritable() ? Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() : Environment.getDataDirectory().getAbsolutePath();


        interest = new InterestEventAssociation(Event.OCCUPANT_DETECTED, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {
                if (event instanceof EventOccupantDetected) {
                    EventOccupantDetected motion = (EventOccupantDetected)event;

                    if(motion.isDetected()){
                        timeLastMovement = System.currentTimeMillis();
                        startVideoRecording();
                    }
                }
            }
        }, 0);

        basaManager.getEventManager().registerInterest(interest);



    }

    public void destroy(){

        if(handler != null) {
            handler.removeCallbacks(timerLiveStream);
            handler = null;
        }
        basaManager = null;
    }

    public void addNewLivePhoto(String path, final String time, final String filename){



        if(BasaDeviceConfig.getConfig().isFirebaseEnabled() && liveStream) {
            FirebaseHelper mHelperFire = new FirebaseHelper();
            mHelperFire.uploadFile(path, Global.VIDEO_HISTORY, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("video", "upload link:"+downloadUrl.getPath());

                    FirebaseFileLink link = new FirebaseFileLink(downloadUrl.toString(), FirebaseAuth.getInstance().getCurrentUser().getUid()+"/video-live/" + filename);
                    link.setCreatedAt(Long.parseLong(time)*1000);
                    liveVideo.put(time, link);

                    new FirebaseHelper().writeNewVideoStreaming(liveVideo);
                    deleteFileDisk(filename, Global.VIDEO_LIVE);
                }
            });
            if(liveVideo.size() > 5){
                //remove oldest
                removeOldestVideo();
            }
        }else{
            deleteFileDisk(filename, Global.VIDEO_LIVE);
        }
    }

    public void addNewHistoryVideo(final String path, final String time, final String filename){

        if(BasaDeviceConfig.getConfig().isFirebaseEnabled()) {
            FirebaseHelper mHelperFire = new FirebaseHelper();
            mHelperFire.uploadFile(path, Global.VIDEO_HISTORY, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("video", "upload link:"+downloadUrl.getPath());

                    final String videoUrl = downloadUrl.toString();
                    String videoPath = storage + File.separator + "myAssistant/history/"+filename;


                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    final String thumbnail = "thumb_" + time + ".jpeg";
                    new FirebaseHelper().uploadHistoryVideoThumbnail(data, thumbnail, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Log.d("video", "upload link:"+downloadUrl.getPath());

                            String pathFile = FirebaseAuth.getInstance().getCurrentUser().getUid()+"/video-history/" + filename;
                            String pathThumbnail = FirebaseAuth.getInstance().getCurrentUser().getUid()+"/video-history/" + thumbnail;

                            final String thumbnailUrl = downloadUrl.toString();

                            int duration = 0;
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            //use one of overloaded setDataSource() functions to set your data source
                            retriever.setDataSource(AppController.getAppContext(), Uri.fromFile(new File(path)));
                            String timeD = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            duration = Integer.parseInt(timeD)/1000;


                            FirebaseFileLink link = new FirebaseFileLink(videoUrl, pathFile, thumbnailUrl, pathThumbnail, duration, Long.parseLong(time));
                            new FirebaseHelper().writeNewVideoHistory(time, link);
                            deleteFileDisk(filename, Global.VIDEO_HISTORY);
                        }
                    });
                }
            });
        }else{
            deleteFileDisk(filename, Global.VIDEO_HISTORY);
        }
    }


    public String getOldestVideo(){
        return liveVideo.firstEntry().getKey();
    }

    private void removeOldestVideo(){
        String old = getOldestVideo();
        String path = liveVideo.get(old).getPathFile();
        liveVideo.remove(old);
        new FirebaseHelper().deleteFile(path);
    }

    private boolean deleteFileDisk(String filename, int type){

//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "myAssistant/");
        String dir = storage + File.separator + "myAssistant/history/";
        if(type == Global.VIDEO_LIVE)
            dir = storage + File.separator + "myAssistant/";

        File f0 = new File(dir, filename);
        return f0.delete();

    }

    public void enableLiveStreaming(Boolean enable){
        timeStartLiveStreaming = System.currentTimeMillis();

        Log.d("video", "enable != null:"+(enable != null));
        liveStream = enable;
    }

    public void startVideoRecording(){
        if(getCommandVideoCamera() != null)
            getCommandVideoCamera().startRecording();
    }

    public CommandVideoCamera getCommandVideoCamera() {
        return commandVideoCamera;
    }

    public void setCommandVideoCamera(CommandVideoCamera commandVideoCamera) {
        this.commandVideoCamera = commandVideoCamera;
    }

    public boolean isLiveStream() {
        return liveStream;
    }

    public interface CommandVideoCamera{

        void startRecording();

        void stopRecording();

    }


}
