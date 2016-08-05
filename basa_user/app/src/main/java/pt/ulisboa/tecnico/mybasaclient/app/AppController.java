package pt.ulisboa.tecnico.mybasaclient.app;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.eddystone.Eddystone;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.UserLocation;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.UpdateLocationService;
import pt.ulisboa.tecnico.mybasaclient.ui.ScanNetworkFragment;
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
    private boolean isBLEStarted;

    private ScanNetworkFragment.ScanResultAvailableListener scanResultAvailableListener;

    @Override
    public void onCreate() {
        super.onCreate();


        mInstance = this;
        AppController.context = getApplicationContext();

        EstimoteSDK.enableDebugLogging(true);
        isBLEStarted = false;

    }


    public void beaconStart(){
        Log.d("temp", "beaconStart:" );


        if(!isBLEStarted) {
            isBLEStarted = true;
            BluetoothAdapter mBluetoothAdapter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                mBluetoothAdapter = mBluetoothManager.getAdapter();
            } else {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }

            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
            }


            beaconManager = new BeaconManager(getApplicationContext());

            beaconManager.setBackgroundScanPeriod(1000, 5000);
            beaconManager.setForegroundScanPeriod(1000, 5000);


            beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
                @Override
                public void onEddystonesFound(List<Eddystone> list) {
                    Log.d(TAG, "list:" + list.size());
                    for (Eddystone eddy : list) {
                        Log.d(TAG, eddy.namespace + ": eddy namespace:");


                        for (Zone zone : loadZones()) {

                            for (BasaDevice device : zone.getDevices()) {
                                for (String beaconId : device.getBeaconUuids()) {
                                    Log.d(TAG, ": device:" + beaconId.toLowerCase() + " found:" + eddy.namespace.toLowerCase());

                                    if (beaconId.toLowerCase().equals(eddy.namespace.toLowerCase())) {
                                        //enviar mensagem
                                        Log.d(TAG, ": is near office sending msg...");
                                        new UpdateLocationService(device.getUrl(), new UserLocation(true, UserLocation.TYPE_OFFICE), new CallbackFromService() {
                                            @Override
                                            public void success(Object response) {}
                                            @Override
                                            public void failed(Object error) {}
                                        }).execute();
                                        break;
                                    }
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
    }

    public void stopEddystoneScanning(){
        beaconManager.stopEddystoneScanning(idEdge);
    }

    public void beaconDisconect(){
        if(beaconManager != null){
            stopEddystoneScanning();
            beaconManager.disconnect();
        }
        isBLEStarted = false;

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

    public ScanNetworkFragment.ScanResultAvailableListener getScanResultAvailableListener() {
        return scanResultAvailableListener;
    }

    public void setScanResultAvailableListener(ScanNetworkFragment.ScanResultAvailableListener scanResultAvailableListener) {
        this.scanResultAvailableListener = scanResultAvailableListener;
    }
}