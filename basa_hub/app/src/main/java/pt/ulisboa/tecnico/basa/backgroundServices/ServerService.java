package pt.ulisboa.tecnico.basa.backgroundServices;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.rest.WebServerBASA;
import pt.ulisboa.tecnico.basa.ui.MainActivity;

/**
 * Created by sampaio on 06-07-2016.
 */
public class ServerService extends Service {

    private boolean isStarted;
    private BackgroundThread run;
    private Context ctx;
    private MainActivity activity;
    int time = 0;
    Handler handler;
    WebServerBASA server;
    private final IBinder mBinder = new LocalBinder();
    private int width, height;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public ServerService getService(){
            return ServerService.this;
        }
    }

    //Here Activity register to the service as Callbacks client
    public void registerClient(MainActivity activity){
        this.activity = activity;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("servico", "onCreate");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SharedPreferences sp = getSharedPreferences("tAPPitz", Activity.MODE_PRIVATE);


        isStarted = false;
        ctx = AppController.getInstance().getApplicationContext();
        run = new BackgroundThread(false);

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("servico", "onStartCommand");
        //check internet
        String origin = "";
        if(intent != null) {
            Bundle extras = intent.getExtras();

            if (extras != null) {
                origin = extras.getString("origin", "");
            }
        }


        if (!isStarted) {
            // loggin
            isStarted = true;
            if(origin.equals("receiver"))
                run.setFromReceiver(true);
            time = 0;
            handler = new Handler();
            new Thread(run).start();


//                    handler.post(run);
        }



        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("servico", "onDestroy");
        isStarted = false;
    }


    public static boolean isWifiAvailable(){

        ConnectivityManager cm =
                (ConnectivityManager) AppController.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

//        ConnectivityManager connManager = (ConnectivityManager) AppController.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        return mWifi.isConnected();
    }





    public void stopserver(){
        Log.d("servico", "stopserver");
//        AppController app = AppController.getInstance();
        if(handler != null)
            handler.removeCallbacksAndMessages(null);
        if(server != null) {
            server.stopServer();
            server = null;
        }
        stopSelf();
    }



    class BackgroundThread implements Runnable{

        private boolean fromReceiver;

        public BackgroundThread(boolean fromReceiver) {
            this.fromReceiver = fromReceiver;
        }

        public void setFromReceiver(boolean fromReceiver) {
            this.fromReceiver = fromReceiver;
        }

        @Override
        public void run() {
            Log.d("servico", "Inicio do RUN");


//            launchServer();
            server = new WebServerBASA(activity);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    time+=1;
                    Log.d("servico", "time:"+time);
                    if(time > 2000){
                        stopserver();
//                        server.stopServer();
                        isStarted = false;
                        stopSelf();
                    }else{
                        handler.postDelayed(this, 1000);
                    }

                }
            },1000);


            }

    }

}