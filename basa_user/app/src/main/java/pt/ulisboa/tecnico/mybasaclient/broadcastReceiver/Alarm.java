package pt.ulisboa.tecnico.mybasaclient.broadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.UserLocation;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.UpdateLocationService;

public class Alarm extends BroadcastReceiver
{

    private static final String TAG = "Alarm";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.d("wifi", "onReceive alarm");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();

        checkLocation(context);
        // Put here YOUR code.
        Toast.makeText(context, "Alarm !!!!!!!!!!", Toast.LENGTH_LONG).show(); // For example

        wl.release();
    }

    public void setAlarm(Context context)
    {
        Log.d("wifi", "setAlarm");
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 1, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Log.d("wifi", "cancelAlarm");
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


    private void checkLocation(Context context){

        Log.d("wifi", "checkLocation");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
        int misses = sp.getInt("location_misses", 0);

        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean hasFound = false;
        mWifiManager.startScan();
        List<ScanResult> mScanResults =  mWifiManager.getScanResults();
        List<Zone> zones = AppController.getInstance().loadZones();
        Log.d(TAG, "mScanResults:"+ mScanResults.size());
        boolean foundDevice = false;
        for(Zone zone : zones){
            for(BasaDevice device : zone.getDevices()){
                foundDevice = false;
                for (ScanResult result:mScanResults) {
                    if(foundDevice)
                        break;
                    for(String mac : device.getMacAddress()){
                        if(mac.toLowerCase().equals(result.BSSID.toLowerCase())){
                            Log.d(TAG, ": is in building sending msg...");
                            //enviar mensagem
                            new UpdateLocationService(device, new UserLocation(true, UserLocation.TYPE_BUILDING), new CallbackFromService() {
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

        if(hasFound){
            misses = 0;
            AppController.getInstance().beaconStart();
        }else
        {
            misses++;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("location_misses", misses);
        editor.commit();

        //in the first 5 min has not found a valid location
        if(misses >= 5){
            AppController.getInstance().beaconDisconect();
            cancelAlarm(context);
        }

    }

}