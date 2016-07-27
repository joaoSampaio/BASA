package pt.ulisboa.tecnico.basa.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.manager.BasaManager;
import pt.ulisboa.tecnico.basa.model.firebase.FirebaseBasaDevice;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;

/**
 * Created by Sampaio on 20/07/2016.
 */
public class FirebaseHelper {

    String TAG = "firebase";
    private DatabaseReference mDatabase;

    public FirebaseHelper() {
        this.mDatabase =  FirebaseDatabase.getInstance().getReference();;
    }

    public void registerDevice(String userId){

        FirebaseBasaDevice device = new FirebaseBasaDevice();
        device.setChangeTemperature(-1);
        device.setCurrentTemperature(25);
        device.setIp("o meu ip");
        device.setUuid(userId);
        Boolean[] lights = new Boolean[]{true, false, true};
        device.setLights(Arrays.asList(lights));

        mDatabase.child("devices").child(userId).setValue(device);

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


    public ValueEventListener getZoneDevicesListener(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                FirebaseBasaDevice firebaseBasaDevice = dataSnapshot.getValue(FirebaseBasaDevice.class);

                Log.d(TAG, "recebeu->"+dataSnapshot.toString());
                String id = firebaseBasaDevice.getUuid();

                BasaManager manager = AppController.getInstance().getBasaManager();
                if(manager.getLightingManager() != null && manager.getLightingManager().hasLightChanged(firebaseBasaDevice.getLights())){


                    boolean[] tmp = new boolean[firebaseBasaDevice.getLights().size()];
                    for(int i = 0; i < firebaseBasaDevice.getLights().size(); i++) tmp[i] = firebaseBasaDevice.getLights().get(i);
                    manager.getLightingManager().setLightState(tmp, true, false);
                }


                if(manager.getTemperatureManager() != null){
                    manager.getTemperatureManager().onChangeTargetTemperature(firebaseBasaDevice.getChangeTemperature());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("devices").child(AppController.getInstance().getDeviceConfig().getUuid()).addValueEventListener(postListener);
        return postListener;
    }


}
