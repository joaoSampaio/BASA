package pt.ulisboa.tecnico.mybasaclient.manager;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.util.FirebaseHelper;

/**
 * Created by Sampaio on 22/07/2016.
 */
public class DeviceManager {

    private Map<String , ValueEventListener> firebaseListenners;
    private FirebaseHelper mHelper;

    public DeviceManager(FirebaseHelper mHelper){
        this.mHelper = mHelper;
        this.firebaseListenners = new TreeMap<>();

    }

    public FirebaseHelper getmHelper() {
        return mHelper;
    }


    public void changeTemperature(int temperature){
        BasaDevice device = AppController.getInstance().getCurrentDevice();
        mHelper.changeTemperature(device.getId(), temperature);

    }

    public void changeLights(List<Boolean> lights){
        BasaDevice device = AppController.getInstance().getCurrentDevice();
        mHelper.changeLights(device.getId(), lights);

    }

    public void setCurrentZone(Zone zone){

        if(isZoneDifferent(zone)) {
            Log.d("fire", "isZoneDifferent-> true " + zone.getName());
            clearAllListeners();
            for (BasaDevice device : zone.getDevices()) {
                firebaseListenners.put(device.getId(), mHelper.getZoneDevicesListener(device.getId()));
            }
        }
    }

    //returns true if the zone different from the map
    private boolean isZoneDifferent(Zone zone){

        for (BasaDevice device: zone.getDevices()) {
            if(!firebaseListenners.containsKey(device.getId())) {
                return true;
            }
        }

        return zone.getDevices().size() != firebaseListenners.size();

    }

    public void clearAllListeners(){
        Log.d("fire", "clearAllListeners");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        for (Map.Entry<String, ValueEventListener> entry: firebaseListenners.entrySet()) {
            mDatabase.removeEventListener(entry.getValue());
        }
        firebaseListenners.clear();

    }

}
