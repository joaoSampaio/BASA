package pt.ulisboa.tecnico.basa.util;

import android.app.Activity;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.text.format.Formatter;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.manager.BasaManager;
import pt.ulisboa.tecnico.basa.model.firebase.FirebaseBasaDevice;
import pt.ulisboa.tecnico.basa.model.firebase.FirebaseFileLink;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;

/**
 * Created by Sampaio on 20/07/2016.
 */
public class FirebaseHelper {

    String TAG = "firebase";
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

    public FirebaseHelper() {
        this.mDatabase =  FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
    }

    public void registerDevice(String userId){

        WifiManager wm = (WifiManager) AppController.getAppContext().getSystemService(Activity.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        FirebaseBasaDevice device = new FirebaseBasaDevice();
        device.setChangeTemperature(-1);
        device.setCurrentTemperature(25);
        device.setMacList(AppController.getInstance().getDeviceConfig().getMacList());
        device.setBeaconList(AppController.getInstance().getDeviceConfig().getBeaconList());
        device.setIp(ip+ ":" + Global.PORT);
        device.setUuid(userId);
        Boolean[] lights = new Boolean[]{false, false, false};
        device.setLights(Arrays.asList(lights));

        mDatabase.child("devices").child(userId).setValue(device);

    }

    public void updateDeviceLocationList(){
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("macList").setValue(AppController.getInstance().getDeviceConfig().getMacList());
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("beaconList").setValue(AppController.getInstance().getDeviceConfig().getBeaconList());
    }


    public void setLatestTemperature(int temperature){
        Log.d(TAG, "setLatestTemperature->"+temperature);
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("currentTemperature").setValue(temperature);

    }


    public void changeTemperature(int temperature){

        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("changeTemperature").setValue(temperature);

    }

    public void changeLights( boolean[] lights){
        List<Boolean> l = new ArrayList<>();
        for(boolean b: lights)
            l.add(new Boolean(b));
        changeLights(l);
    }

    public void changeLights( List<Boolean> lights){

        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("lights").setValue(lights);

    }

    public void checkIfDeviceExists(final CallbackMultiple<Boolean, Boolean> listener){
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listener.success(dataSnapshot != null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.success(false);
            }
        });
    }


    public List<ValueEventListener> getDeviceListener(){

        List<ValueEventListener> listeners = new ArrayList<>();


        ValueEventListener lightListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                GenericTypeIndicator<List<Boolean>> t = new GenericTypeIndicator<List<Boolean>>() {};

                List<Boolean> lights = dataSnapshot.getValue(t);

                Log.d(TAG, "lightListener->"+dataSnapshot.toString());
                BasaManager manager = AppController.getInstance().getBasaManager();
                if(manager.getLightingManager() != null && manager.getLightingManager().hasLightChanged(lights)){

                    boolean[] tmp = new boolean[lights.size()];
                    for(int i = 0; i < lights.size(); i++) tmp[i] = lights.get(i);
                        manager.getLightingManager().setLightState(tmp, true, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        ValueEventListener temperatureListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Integer changeTemperature = dataSnapshot.getValue(Integer.class);

                Log.d(TAG, "temperatureListener->"+dataSnapshot.toString());

                BasaManager manager = AppController.getInstance().getBasaManager();

                if(manager.getTemperatureManager() != null){
                    manager.getTemperatureManager().onChangeTargetTemperature(changeTemperature);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };

        ValueEventListener liveStreamListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Boolean live = dataSnapshot.getValue(Boolean.class);
                if(live != null) {
                    Log.d(TAG, "liveStreamListener->" + dataSnapshot.toString());

                    BasaManager manager = AppController.getInstance().getBasaManager();

                    if (manager.getVideoManager() != null) {
                        manager.getVideoManager().enableLiveStreaming(live);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };


//        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).addValueEventListener(lightListener);
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("lights").addValueEventListener(lightListener);
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("changeTemperature").addValueEventListener(temperatureListener);
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).child("record").addValueEventListener(liveStreamListener);

        listeners.add(lightListener);
        listeners.add(temperatureListener);
        listeners.add(liveStreamListener);

        return listeners;
    }

    public void enableLiveStreaming(boolean record){

        String uuid = AppController.getInstance().getDeviceConfig().getUuid();
        mDatabase.child("devices").child(uuid).child("record").setValue(record);
    }

    public void writeNewVideoStreaming(TreeMap<String, FirebaseFileLink> liveVideo){

        String uuid = AppController.getInstance().getDeviceConfig().getUuid();
        mDatabase.child("live").child(uuid).setValue(liveVideo);
    }

    public void writeNewVideoHistory(String filename, FirebaseFileLink link){

        String uuid = AppController.getInstance().getDeviceConfig().getUuid();
        mDatabase.child("history").child(uuid).child(filename).setValue(link);
    }


    public void uploadHistoryVideoThumbnail(byte[] data, String filename, OnSuccessListener<UploadTask.TaskSnapshot> listener) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            StorageReference storageRef = storage.getReferenceFromUrl("gs://basa-2a0c9.appspot.com");
            StorageReference riversRef = storageRef.child(user.getUid()+"/videos/" + filename);
            UploadTask uploadTask = riversRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(listener);
        }
    }

    public void uploadFile(String path, int type, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        ///user videos

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String fileType = type == Global.VIDEO_LIVE ? user.getUid()+"/video-live/" : user.getUid()+"/videos/";

            StorageReference storageRef = storage.getReferenceFromUrl("gs://basa-2a0c9.appspot.com");
            Uri file = Uri.fromFile(new File(path));
            StorageReference riversRef = storageRef.child(fileType + file.getLastPathSegment());
            UploadTask uploadTask = riversRef.putFile(file);

// Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(listener);
        }
    }

    public void deleteFile(String path) {
        ///user videos

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            StorageReference storageRef = storage.getReferenceFromUrl("gs://basa-2a0c9.appspot.com");
            // Create a reference to the file to delete
            StorageReference desertRef = storageRef.child(path);

// Delete the file
            desertRef.delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }
    }


}
