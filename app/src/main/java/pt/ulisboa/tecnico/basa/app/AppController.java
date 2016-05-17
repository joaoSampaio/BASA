package pt.ulisboa.tecnico.basa.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.eddystone.Eddystone;
import com.koushikdutta.async.http.server.AsyncHttpServer;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import pt.ulisboa.tecnico.basa.TemperatureActivity;
import pt.ulisboa.tecnico.basa.model.EventTime;
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
    public int currentCameraId;
    public int width;
    public int height;
    public int widthPreview;
    public int heightPreview;
    public boolean mCameraReady;
    public int skipTop, skipBottom, skipLeft, skipRight;
    public float mThreshold;
    public int timeScanPeriod;


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
        beaconManager.setBackgroundScanPeriod(13000, 25000);
        beaconManager.setForegroundScanPeriod(14000,0);
        beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override
            public void onEddystonesFound(List<Eddystone> list) {
                Log.d("temp", "list:" + list.size());
                Log.d("temp", namespace +": wanted namespace:");
                for (Eddystone eddy: list) {
                    Log.d("temp", eddy.namespace +": eddy namespace:");
                    if(eddy.namespace.equals(namespace)){
                        Log.d("temp", "eddy.telemetry != null:" + (eddy.telemetry != null));
                        if(eddy.telemetry != null){
                            Log.d("temp", "temperatura:" + eddy.telemetry.temperature);
                            Utils.Proximity proximity = Utils.computeProximity(eddy);
                            double accuracy = Utils.computeAccuracy(eddy);
                            Log.d("temp", "proximity.toString():" + proximity.toString());
                            Log.d("temp", "accuracy:" +accuracy);
                            if(interfaceToActivity != null){
                                interfaceToActivity.updateTemperature(eddy.telemetry.temperature);
                            }
                        }
                    }
                }


            }
        });

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                idEdge = beaconManager.startEddystoneScanning();
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