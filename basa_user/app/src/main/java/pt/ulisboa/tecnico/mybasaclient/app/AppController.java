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



    @Override
    public void onCreate() {
        super.onCreate();


        mInstance = this;
        AppController.context = getApplicationContext();
        EstimoteSDK.enableDebugLogging(true);

//long scanPeriodMillis, long waitTimeMillis
//        scanPeriodMillis - How long to perform Bluetooth Low Energy scanning?
//                waitTimeMillis - How long to wait until performing next scanning?

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



}