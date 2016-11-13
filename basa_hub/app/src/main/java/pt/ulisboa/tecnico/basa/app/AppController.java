package pt.ulisboa.tecnico.basa.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.util.Log;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Utils;
import com.estimote.sdk.eddystone.Eddystone;
import com.google.gson.reflect.TypeToken;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.BroadcastReceiver.OnScreenOffReceiver;
import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.backgroundServices.KioskService;
import pt.ulisboa.tecnico.basa.manager.BasaManager;
import pt.ulisboa.tecnico.basa.model.AllStatisticalData;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.model.EventHistory;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.util.ModelCache;


public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();
    private static Context context;
    private static AppController mInstance;
    private BeaconManager beaconManager;
    private String idEdge;
    private String namespace;
    private int temperature;
    public int width;
    public int height;
    public int widthPreview;
    public int heightPreview;
    public boolean mCameraReady;
    public int skipTop, skipBottom, skipLeft, skipRight;
    public float mThreshold;
    public int timeScanPeriod;

    private BasaManager basaManager;
    private AllStatisticalData statisticalData;

    private PowerManager.WakeLock wakeLock;
    private OnScreenOffReceiver onScreenOffReceiver;
    private BasaDeviceConfig deviceConfig;

    private List<Recipe> customRecipes;

    private List<EventHistory> history;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        temperature = -99;
        AppController.context = getApplicationContext();
        EstimoteSDK.enableDebugLogging(true);
        JodaTimeAndroid.init(this);
        registerKioskModeScreenOffReceiver();
        startKioskService();
    }

    private void startKioskService() { // ... and this method
        startService(new Intent(this, KioskService.class));
    }

    private void registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        registerReceiver(onScreenOffReceiver, filter);
    }

    public PowerManager.WakeLock getWakeLock() {
        if(wakeLock == null) {
            // lazy loading: first call, create wakeLock via PowerManager.
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "wakeup");
        }
        return wakeLock;
    }

    public void beaconStart(){
        Log.d(TAG, "beaconStart:" );

        if(getDeviceConfig().getTemperatureChoice() == BasaDeviceConfig.TEMPERATURE_TYPE_MONITOR_BEACON) {

            beaconManager = new BeaconManager(getApplicationContext());
            beaconManager.setBackgroundScanPeriod(1300, 25000);
            beaconManager.setForegroundScanPeriod(13000,1000);


            namespace = getDeviceConfig().getBeaconUuidTemperature();


            beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
                @Override
                public void onEddystonesFound(List<Eddystone> list) {
                    Log.d(TAG, "list:" + list.size());
                    for (Eddystone eddy : list) {
                        Log.d(TAG, eddy.namespace + ": eddy namespace:");
                        if (eddy.namespace.equals(namespace)) {
                            Log.d(TAG, "eddy.telemetry != null:" + (eddy.telemetry != null));
                            if (eddy.telemetry != null) {
                                Log.d(TAG, "temperatura:" + eddy.telemetry.temperature);
                                Utils.Proximity proximity = Utils.computeProximity(eddy);
                                temperature =  (int)eddy.telemetry.temperature;
                                double accuracy = Utils.computeAccuracy(eddy);
                                Log.d("temp", "proximity.toString():" + proximity.toString());
                                Log.d("temp", "accuracy:" + accuracy);
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
        }else{
            Log.d(TAG, "BLE disabled");
        }
    }

    public void setDeviceConfig(BasaDeviceConfig deviceConfig, boolean save) {
        this.deviceConfig = deviceConfig;
        if(save)
            BasaDeviceConfig.save(deviceConfig);
    }

    public void saveDeviceConfig() {
        BasaDeviceConfig.save(deviceConfig);
    }

    public BasaDeviceConfig getDeviceConfig() {
        if(deviceConfig == null){
            deviceConfig = BasaDeviceConfig.getConfig();
        }
        return deviceConfig;
    }

    public void stopEddystoneScanning(){
        if(beaconManager != null)
            beaconManager.stopEddystoneScanning(idEdge);
    }

    public void beaconDisconect(){
        stopEddystoneScanning();
        if(beaconManager != null)
            beaconManager.disconnect();
    }


    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return AppController.context;
    }


    public BasaManager getBasaManager() {

        if(basaManager == null){
            Log.d("getBasaManager", "getBasaManager is null ");
            basaManager = BasaManager.getInstance();
//            basaManager.start();
        }

        return basaManager;
    }


    public List<Recipe> getCustomRecipes(){
        if(customRecipes == null){
            customRecipes = new ModelCache<List<Recipe>>().loadRecipes();
        }
        return customRecipes;
    }

    public void saveCustomRecipes(List<Recipe> list){
        new ModelCache<List<Recipe>>().saveModel(list, Global.OFFLINE_RECIPES);
    }


    public void saveHistory(List<EventHistory> list){
        new ModelCache<List<EventHistory>>().saveModel(list, Global.OFFLINE_HISTORY);
    }

    public List<EventHistory> getHistory() {

        if(history == null){
            history = new ModelCache<List<EventHistory>>().loadModel(new TypeToken<List<EventHistory>>(){}.getType(), Global.OFFLINE_HISTORY);
            if(history == null)
                history = new ArrayList<>();
        }
        return history;
    }


    public AllStatisticalData getStatisticalData(){
        if(statisticalData == null)
            statisticalData = AllStatisticalData.load();
        return statisticalData;
    }

    private String serverKey = null;
    public String getServerKey(){
        if(serverKey == null){
            serverKey = "AIzaSyCY4HnknljxJX_CoZHuMVO6TEslMs2_tNo";
//            serverKey = getResources().getString(R.string.google_api_key);
        }
        return serverKey;
    }

    public int getTemperature() {
        return temperature;
    }
}