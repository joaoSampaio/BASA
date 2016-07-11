package pt.ulisboa.tecnico.basa.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import java.util.Date;
import java.util.List;

import pt.ulisboa.tecnico.basa.manager.BasaManager;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.ui.MainActivity;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static Context context;
    private static AppController mInstance;
    private BeaconManager beaconManager;
    private String idEdge;
    private MainActivity.InterfaceToActivity interfaceToActivity;
    public AsyncHttpServer server;
    private String namespace = "edd1ebeac04e5defa017";
    String uuid = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    public int currentCameraId;
    public int width;
    public int height;
    public int widthPreview;
    public int heightPreview;
    public boolean mCameraReady;
    public int skipTop, skipBottom, skipLeft, skipRight;
    public float mThreshold;
    public int timeScanPeriod;

    public BasaManager basaManager;


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





//        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
//            @Override
//            public void onEddystonesFound(List<Eddystone> list) {
//                Log.d("temp", "list:" + list.size());
//                Log.d("temp", namespace +": wanted namespace:");
//                for (Eddystone eddy: list) {
//                    Log.d("temp", eddy.namespace +": eddy namespace:");
//                    if(eddy.namespace.equals(namespace)){
//                        Log.d("temp", "eddy.telemetry != null:" + (eddy.telemetry != null));
//                        if(eddy.telemetry != null){
//                            Log.d("servico", "temperatura:" + eddy.telemetry.temperature);
//                            Utils.Proximity proximity = Utils.computeProximity(eddy);
//                            Log.d("servico", "basaManager != null:" + (basaManager != null));
//                            if(basaManager != null) {
//                                basaManager.getEventManager().addEvent(new EventTemperature(Event.TEMPERATURE, eddy.telemetry.temperature, -1));
//
//                            }
//
//                            double accuracy = Utils.computeAccuracy(eddy);
//                            Log.d("temp", "proximity.toString():" + proximity.toString());
//                            Log.d("temp", "accuracy:" +accuracy);
//                            if(interfaceToActivity != null){
//                                interfaceToActivity.updateTemperature(eddy.telemetry.temperature);
//                            }
//                        }
//                    }
//                }
//
//
//            }
//        });
//
//        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
//            @Override
//            public void onServiceReady() {
//                idEdge = beaconManager.startEddystoneScanning();
//            }
//        });
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

    public MainActivity.InterfaceToActivity getInterfaceToActivity() {
        return interfaceToActivity;
    }

    public void setInterfaceToActivity(MainActivity.InterfaceToActivity interfaceToActivity) {
        this.interfaceToActivity = interfaceToActivity;
    }

    public AsyncHttpServer getServer() {
        return server;
    }

    public void setServer(AsyncHttpServer server) {
        this.server = server;
    }


    public void onTimerIntent(){
        if(interfaceToActivity != null){
            interfaceToActivity.getManager().getEventManager().addEvent(new EventTime(new Date()));
        }
    }

}