package pt.ulisboa.tecnico.mybasaclient.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.model.firebase.FirebaseBasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.firebase.UserFirebase;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.UserLocation;

/**
 * Created by Sampaio on 20/07/2016.
 */
public class FirebaseHelper {

    String TAG = "main";
    private DatabaseReference mDatabase;
    private MainActivity activity;

    public FirebaseHelper() {
        this.mDatabase =  FirebaseDatabase.getInstance().getReference();
    }

    public FirebaseHelper(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

    public void registerUser(String userId, String name, String email){

        UserFirebase user = new UserFirebase(name, userId, email);
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void changeTemperature(String deviceId, int temperature){

        mDatabase.child("devices").child(deviceId).child("changeTemperature").setValue(temperature);

    }

    public void changeLights(String deviceId, List<Boolean> lights){

        mDatabase.child("devices").child(deviceId).child("lights").setValue(lights);

    }

    public void enableLiveStream(String deviceId, boolean enable){

        mDatabase.child("devices").child(deviceId).child("record").setValue(enable);

    }

    public void updateLocation(String deviceId, String userId, UserLocation location){

        mDatabase.child("location").child(deviceId).child(userId).setValue(location);

    }



    public List<ValueEventListener> getZoneDevicesListener(final String deviceId){

//        ValueEventListener lightsListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // Get Post object and use the values to update the UI
//
//                GenericTypeIndicator<List<Boolean>> t = new GenericTypeIndicator<List<Boolean>>() {};
//                List<Boolean> lights = dataSnapshot.getValue(t);
//                List<Zone> zones = AppController.getInstance().loadZones();
//
//                Zone current = AppController.getInstance().getCurrentZone();
//                for (Zone z: zones) {
//                    for (BasaDevice d : z.getDevices()) {
//                        if (d.getId().equals(deviceId)) {
//                            d.setNumLights(lights.size());
//                            d.setLights(lights);
//                            if(getActivity() != null){
//                                for(GenericCommunicationToFragment generic: getActivity().getGenericCommunicationList()){
//                                    generic.onDataChanged(deviceId);
//                                }
//                                if(getActivity().getCommunicationHomeFragment() != null
//                                        && current != null
//                                        && current.getName().equals(z.getName()))
//                                    getActivity().getCommunicationHomeFragment().updateZone(false);
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };


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
                            if(getActivity() != null){

                                for(GenericCommunicationToFragment generic: getActivity().getGenericCommunicationList()){
                                    generic.onDataChanged(deviceId);
                                }
                                if(getActivity().getCommunicationHomeFragment() != null)
                                    getActivity().getCommunicationHomeFragment().updateZone(false);
                            }
                            return;
                        }
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

        List<ValueEventListener> list = new ArrayList<>();
        list.add(mDatabase.child("devices").child(deviceId).addValueEventListener(postListener));

        return list;
    }


    public DatabaseReference getmDatabase() {
        return mDatabase;
    }
}
