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
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.List;
import java.util.Timer;

import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;


/**
 * Created by joaosampaio on 23-02-2016.
 */
public class WifiLocationService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private static final long UPDATE_INTERVAL = 1 * 15 * 1000; //15s
    private static final long DELAY_INTERVAL = 0;

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


        handler.post(new Runnable() {
            @Override
            public void run() {

                Log.d("wifi_Service", "RUN");
                //DO YOUR CODE
                boolean hasFound = false;
                mWifiManager.startScan();
                List<ScanResult> mScanResults =  mWifiManager.getScanResults();
                List<Zone> zones = AppController.getInstance().loadZones();
                for (ScanResult result:mScanResults) {
                    Log.d("wifi2", "result.BSSID:" + result.BSSID);

                    for(Zone zone : zones){

                        for(BasaDevice device : zone.getDevices()){
                            for(String mac : device.getMacAddress()){
                                if(mac.toLowerCase().equals(result.BSSID.toLowerCase())){
                                    //enviar mensagem

                                    hasFound = true;
                                    break;
                                }
                            }
                        }
                    }

                }

                if(!hasFound)
                    misses++;
                Log.d("wifi2", "misses:" + misses);
                if(misses >= 20) {
                    misses = 0;
                    stopSelf();
                }else{
                    handler.postDelayed(this, UPDATE_INTERVAL * (misses+1));
                }

            }

        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("wifi_Service", "onStartCommand SERVICE");


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
