package pt.ulisboa.tecnico.mybasaclient.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.model.firebase.FirebaseBasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.firebase.UserFirebase;

/**
 * Created by Sampaio on 20/07/2016.
 */
public class FirebaseHelper {

    String TAG = "main";
    private DatabaseReference mDatabase;
    private MainActivity activity;

    public FirebaseHelper(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void registerUser(String userId, String name){

        UserFirebase user = new UserFirebase(name, userId);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void changeTemperature(String deviceId, int temperature){

        mDatabase.child("devices").child(deviceId).child("changeTemperature").setValue(temperature);

    }

    public void changeLights(String deviceId, List<Boolean> lights){

        mDatabase.child("devices").child(deviceId).child("lights").setValue(lights);

    }

//    public void getZoneDevicesOnce(String deviceId){
//
//        mDatabase.child("users").child(deviceId).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        // Get user value
//                        FirebaseBasaDevice firebaseBasaDevice = dataSnapshot.getValue(FirebaseBasaDevice.class);
//                        List<Zone> zones = AppController.getInstance().loadZones();
//                        String id = firebaseBasaDevice.getUuid();
//
//                        for (Zone z: zones) {
//                            for (BasaDevice d: z.getDevices()) {
//                                if(d.getId().equals(id)){
//                                    d.setLatestTemperature(firebaseBasaDevice.getCurrentTemperature());
//                                    d.setNumLights(firebaseBasaDevice.getLights().size());
//                                    d.setLights(firebaseBasaDevice.getLights());
//
//                                    if(getActivity() != null && getActivity().getCommunicationHomeFragment() != null){
//                                        getActivity().getCommunicationHomeFragment().updateZone();
//                                    }
//                                    return;
//                                }
//                            }
//                        }
//                        // ...
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
//                    }
//                });
//    }


    public ValueEventListener getZoneDevicesListener(String deviceId){

//        mDatabase.child("devices").child(deviceId).child("changeTemperature").setValue(temperature);


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                FirebaseBasaDevice firebaseBasaDevice = dataSnapshot.getValue(FirebaseBasaDevice.class);

                Log.d("fire", "recebeu->"+dataSnapshot.toString());
                String id = firebaseBasaDevice.getUuid();
                List<Zone> zones = AppController.getInstance().loadZones();

                for (Zone z: zones) {
                    for (BasaDevice d: z.getDevices()) {
                        if(d.getId().equals(id)){
                            d.setLatestTemperature(firebaseBasaDevice.getCurrentTemperature());
                            d.setNumLights(firebaseBasaDevice.getLights().size());
                            d.setLights(firebaseBasaDevice.getLights());
                            d.setChangeTemperature(firebaseBasaDevice.getChangeTemperature());
                            if(getActivity() != null && getActivity().getCommunicationHomeFragment() != null){
                                getActivity().getCommunicationHomeFragment().updateZone(false);
                            }
                            if(getActivity() != null){

                                for(GenericCommunicationToFragment generic: getActivity().getGenericCommunicationList()){
                                    generic.onDataChanged();
                                }
                                if(getActivity().getCommunicationHomeFragment() != null)
                                    getActivity().getCommunicationHomeFragment().updateZone(false);
                            }
                            return;
                        }
                    }
                }

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mDatabase.child("devices").child(deviceId).addValueEventListener(postListener);

        return postListener;
    }


    public DatabaseReference getmDatabase() {
        return mDatabase;
    }
}
