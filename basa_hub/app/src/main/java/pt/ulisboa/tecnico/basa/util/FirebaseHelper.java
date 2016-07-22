package pt.ulisboa.tecnico.basa.util;

import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;

import pt.ulisboa.tecnico.basa.model.firebase.FirebaseBasaDevice;

/**
 * Created by Sampaio on 20/07/2016.
 */
public class FirebaseHelper {

    String TAG = "main";
    private DatabaseReference mDatabase;

    public FirebaseHelper(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void registerUser(String userId){

        FirebaseBasaDevice device = new FirebaseBasaDevice();
        device.setChangeTemperature(-1);
        device.setCurrentTemperature(25);
        device.setIp("o meu ip");
        device.setUuid(userId);
        Boolean[] lights = new Boolean[]{true, false, true};
        device.setLights(Arrays.asList(lights));

        mDatabase.child("devices").child(userId).setValue(device);




//        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Get user value
//                        FirebaseBasaDevice user = dataSnapshot.getValue(FirebaseBasaDevice.class);
//
//                        // ...
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                    }
//                });
    }


}
