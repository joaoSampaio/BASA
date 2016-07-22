package pt.ulisboa.tecnico.basa.backgroundServices;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pt.ulisboa.tecnico.basa.ui.Launch2Activity;

public class KioskService extends Service {

    private static final long INTERVAL = TimeUnit.SECONDS.toMillis(2); // periodic interval to check in seconds -> 2 seconds
    private static final String TAG = KioskService.class.getSimpleName();
    private static final String PREF_KIOSK_MODE = "pref_kiosk_mode";
    private boolean hasStarted = false;
    private Thread t = null;
    private Context ctx = null;
    private boolean running = false;

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping service 'KioskService'");
        running =false;
        hasStarted = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Starting service 'KioskService'");
        running = true;
        ctx = this;
        if(hasStarted) {
            Log.i(TAG, "service already running 'KioskService'");
            return Service.START_STICKY;

        }
        // start a thread that periodically checks if your app is in the foreground
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    handleKioskMode();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.i(TAG, "Thread interrupted: 'KioskService'");
                    }
                }while(running);
                stopSelf();
            }
        });
        hasStarted = true;
        t.start();
        return Service.START_STICKY;
    }

    private void handleKioskMode() {
        // is Kiosk Mode active?

        if(isKioskModeActive(ctx)) {

//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);

            // is App in background?
            boolean isInBackground = isInBackground();
            Log.i(TAG, "isInBackground():"+isInBackground);
            if(isInBackground) {
                restoreApp(); // restore!
            }
        }
    }

    private boolean isInBackground() {
        try {
            ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
            String mPackageName;

            for(ActivityManager.RunningTaskInfo t: am.getRunningTasks(5)){
                Log.i(TAG, "t:"+t.topActivity.getPackageName() + " numRunning:"+t.numRunning);
            }

            if(Build.VERSION.SDK_INT > 20){
                mPackageName = am.getRunningAppProcesses().get(0).processName;

                for(ActivityManager.RunningAppProcessInfo p: am.getRunningAppProcesses()){
                    Log.i(TAG, "p:"+p.processName + " importace:"+p.importance);
                }
//                return (!ctx.getApplicationContext().getPackageName().equals(mPackageName) ||
//                        (ctx.getApplicationContext().getPackageName().equals(mPackageName) && am.getRunningAppProcesses().get(0).importance > 100));
            }
//            else{
//                mPackageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
//            }
//            Log.i(TAG, "mPackageName():"+mPackageName);
//            return (!ctx.getApplicationContext().getPackageName().equals(mPackageName));

            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            return (!ctx.getApplicationContext().getPackageName().equals(componentInfo.getPackageName()));
        }catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }

    public void restoreApp() {
        // Restart activity
        Intent i = new Intent(ctx, Launch2Activity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_LAUNCHER);
        ctx.startActivity(i);
    }



    public static boolean isKioskModeActive(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_KIOSK_MODE, false);
    }

    public static void setKioskModeActive(final boolean active, final Context context) {
        Log.d(TAG,"setKioskModeActive:"+active);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_KIOSK_MODE, active).commit();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}