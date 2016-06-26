package pt.ulisboa.tecnico.basa.backgroundServices;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;

/**
 * Created by joaosampaio on 23-02-2016.
 */
public class WifiLocationService extends Service {

    private final IBinder mBinder = new LocalBinder();

    private static final long UPDATE_INTERVAL = 1 * 15 * 1000; //15s
    private static final long DELAY_INTERVAL = 0;

    private Timer timer;
    WifiManager mWifiManager;

private int misses;

    @Override
    public void onCreate() {
        Log.d("wifi_Service", "STARTING SERVICE");
        super.onCreate();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        misses = 0;
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        Log.d("wifi_Service", "RUN");
                        //DO YOUR CODE
                        boolean hasFound = false;
                        mWifiManager.startScan();
                        List<ScanResult> mScanResults =  mWifiManager.getScanResults();
                        for (ScanResult result:mScanResults) {
                            Log.d("wifi2", "result.SSID:" + result.SSID);
                            if(result.SSID.equals(Global.SSID)) {
                                createNotification(result.SSID, result.BSSID, getApplicationContext());
                                hasFound = true;
                                break;
                            }
                        }

                        if(!hasFound)
                            misses++;
                        Log.d("wifi2", "misses:" + misses);
                        if(misses > 2) {
                            misses = 0;
                            stopSelf();
                        }

                    }
                },
                DELAY_INTERVAL,
                UPDATE_INTERVAL
        );
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
