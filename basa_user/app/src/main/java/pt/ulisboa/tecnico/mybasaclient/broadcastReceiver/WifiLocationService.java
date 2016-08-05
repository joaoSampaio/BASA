package pt.ulisboa.tecnico.mybasaclient.broadcastReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Timer;

import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.UserLocation;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.UpdateLocationService;


/**
 * Created by joaosampaio on 23-02-2016.
 */
public class WifiLocationService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private static final long UPDATE_INTERVAL = 1 * 15 * 1000; //15s
    private static final long DELAY_INTERVAL = 0;
    private PowerManager.WakeLock wl;
    private Handler handler;
    private Timer timer;
    WifiManager mWifiManager;

private int misses;

    @Override
    public void onCreate() {
        Log.d("wifi_Service", "STARTING SERVICE");
        super.onCreate();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        handler = new Handler();
        misses = 0;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BASA");
        wl.acquire();



        handler.post(new Runnable() {
            @Override
            public void run() {

                Log.d("wifi2", "RUN");
                //DO YOUR CODE
                boolean hasFound = false;
                mWifiManager.startScan();
                List<ScanResult> mScanResults =  mWifiManager.getScanResults();
                List<Zone> zones = AppController.getInstance().loadZones();
                Log.d("wifi2", "mScanResults:"+ mScanResults.size());
                boolean foundDevice = false;
                for(Zone zone : zones){

                    for(BasaDevice device : zone.getDevices()){
                        foundDevice = false;
                        for (ScanResult result:mScanResults) {
                            if(foundDevice)
                                break;
                            Log.d("wifi2", "result.BSSID:" + result.BSSID);
                            for(String mac : device.getMacAddress()){
                                Log.d("wifi2", ":wifi device:" + mac.toLowerCase() + " found:" + result.BSSID.toLowerCase());
                                if(mac.toLowerCase().equals(result.BSSID.toLowerCase())){
                                    Log.d("wifi2", ": is in building sending msg...");
                                    //enviar mensagem
                                    new UpdateLocationService(device.getUrl(), new UserLocation(true, UserLocation.TYPE_BUILDING), new CallbackFromService() {
                                        @Override
                                        public void success(Object response) {}
                                        @Override
                                        public void failed(Object error) {}
                                    }).execute();
                                    hasFound = true;
                                    foundDevice = true;
                                    break;
                                }
                            }
                        }
                    }

                }

                if(!hasFound)
                    misses++;
                else {
                    AppController.getInstance().beaconStart();

                }
                Log.d("wifi2", "misses:" + misses);
                if(misses >= 20) {
                    misses = 0;
                    wl.release();
                    stopSelf();
                }else{
                    handler.postDelayed(this, UPDATE_INTERVAL * (misses+1));
                }

            }

        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("wifi22", "onStartCommand SERVICE");


        super.onStartCommand(intent, flags, startId);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        timer.cancel();

        super.onDestroy();
    }





    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public WifiLocationService getServiceInstance(){
            return WifiLocationService.this;
        }
    }

    private void createNotification(String ssid, String mac, Context ctx) {
        Log.d("wifi", "createNotification");
        Notification n = new NotificationCompat.Builder(ctx)
                .setContentTitle("Wifi Connection")
                .setContentText("visible network: " + ssid)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You're connected to " + ssid + " at " + mac))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000})
                .build();
        ((NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE))
                .notify(0, n);
    }

}
