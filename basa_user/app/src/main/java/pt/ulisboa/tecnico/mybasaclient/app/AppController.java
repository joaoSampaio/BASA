package pt.ulisboa.tecnico.mybasaclient.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static Context context;
    private static AppController mInstance;
    private BeaconManager beaconManager;
    private String idEdge;
    private String namespace = "edd1ebeac04e5defa017";
    public int currentCameraId;
    public int width;
    public int height;

    private User loggedUser;
    private List<Zone> zones;
    private Zone currentZone;
    private BasaDevice currentDevice;

    @Override
    public void onCreate() {
        super.onCreate();


        mInstance = this;
        AppController.context = getApplicationContext();

        EstimoteSDK.enableDebugLogging(true);


    }


    public void beaconStart(){
        Log.d("temp", "beaconStart:" );


        beaconManager = new BeaconManager(getApplicationContext());

        beaconManager.setBackgroundScanPeriod(1300, 25000);
        beaconManager.setForegroundScanPeriod(1000,5000);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                Log.d("temp", "list:" + beacons.size());
                if (beacons.size() != 0) {
                    Beacon beacon = beacons.get(0);
                    Utils.Proximity proximity = Utils.computeProximity(beacon);
                    Log.d("temp", "proximity:" + proximity);
                    Log.d("temp", "beacon:" + beacon.toString());
                    Log.d("temp", "getProximityUUID:" + beacon.getProximityUUID());
                    // ...
                }
            }
        });


        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(new Region("regiao", null, null , null));
            }
        });

    }

    public void stopEddystoneScanning(){
        beaconManager.stopEddystoneScanning(idEdge);
    }

    public void beaconDisconect(){
        if(beaconManager != null)
        beaconManager.disconnect();
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return AppController.context;
    }


    public User getLoggedUser(){
        if(loggedUser == null){
            loggedUser = User.getLoggedUser();
        }
        return loggedUser;
    }

    public void setLoggedUser(User user){
        this.loggedUser = user;
        User.saveUser(user);
    }


    public List<Zone> loadZones(){
        if(this.zones == null){
            Log.d("home", "loadZones is null:");
            this.zones = Zone.loadZones();
            Log.d("home", "loadZones is :" + (this.zones!= null));
        }
        return this.zones;
    }

    public void saveZones(List<Zone> zones){
        Log.d("home", "saveZones:" + (zones != null));
        if(zones != null) {
            Zone.saveZones(zones);
//            this.zones.clear();
//            this.zones.addAll(zones);
        }
    }

    public boolean isEmptyZones(){
        return loadZones().isEmpty();
    }

    public Zone getCurrentZone(){
        if(this.currentZone == null)
            this.currentZone = Zone.getCurrentZone();
        return this.currentZone;
    }

    public void saveCurrentZone(Zone zone){
        this.currentZone = zone;
        if(zone != null)
            Zone.saveCurrentZone(zone);
    }

    public BasaDevice getCurrentDevice(){
        if(this.currentDevice == null)
            this.currentDevice = BasaDevice.getCurrentDevice();
        return this.currentDevice;
    }

    public void saveCurrentDevice(BasaDevice device){
        this.currentDevice = device;
        if(device != null)
            new ModelCache<String>().saveModel(device.getId(), Global.DATA_CURRENT_DEVICE);
    }

    public void saveUser(User user){
        this.loggedUser = user;
        User.saveUser(user);
    }

    public void saveData(){
        this.saveCurrentDevice(this.currentDevice);
        this.saveCurrentZone(this.currentZone);
        this.saveZones(this.zones);
        this.saveUser(this.loggedUser);
    }

    public void resetData(){
        this.currentDevice = null;
        this.currentZone = null;
        this.zones = null;
    }

}