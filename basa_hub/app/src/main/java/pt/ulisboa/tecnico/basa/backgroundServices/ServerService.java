package pt.ulisboa.tecnico.basa.backgroundServices;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
import pt.ulisboa.tecnico.basa.model.Status;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistration;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationAnswer;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationToken;
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
    AsyncHttpServer server;
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



    public void launchServer(){
        server = new AsyncHttpServer();

        String msg = "";
        try {
            server.get("/users/me", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                    User user = new User("Joao");
                    Gson gson = new Gson();
                    String json = gson.toJson(user);

                    response.send(json);
                }
            });

            server.post("/register", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                    String body = request.getBody().get().toString();
                    Log.d("webserver", "request.body():"+body);

                    Gson gson = new Gson();


                    try {
                        final UserRegistration userRegistration = gson.fromJson(body, new TypeToken<UserRegistration>() {
                        }.getType());
                        if(UserRegistrationToken.isTokenValid(userRegistration.getToken())) {

                            activity.getBasaManager().getUserManager().registerNewUser(userRegistration.getUsername(), userRegistration.getEmail(), userRegistration.getUuid());

                            UserRegistrationAnswer answer = new UserRegistrationAnswer();

                            response.send( "{\"status\": true, \"data\": "+gson.toJson(answer)+"}");

                        }
                    } catch (UserRegistrationException e) {
                        e.printStackTrace();
                    }

                    response.send("{\"status\": false}");


                }
            });


            server.post("/users", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    Gson gson = new Gson();
                    Status status = null;
                    try {
                        String received = request.getBody().get().toString();
                        Log.d("app", "post received:" + received);

                        User user = gson.fromJson(received, User.class);
                        if(user.getName() != null)
                            status = new Status(true, "Welcome "+user.getName());
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    if(status == null)
                        status = new Status(false, "Sorry there was an error");
                    String json = gson.toJson(status);

                    response.send(json);
                }
            });

            server.post("/broadcast", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    try {
                        Log.d("webserver", "request.getBody().length():" + request.getBody().length());
                        String received = request.getBody().get().toString();

                        Log.d("webserver", "post received:" + received);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("webserver", "Exception:");

                    }


                    response.send("ok");
                }
            });

            // listen on port 5000
            server.listen(5000);

            //WifiManager wm = (WifiManager) activity.getSystemService(Activity.WIFI_SERVICE);
            //String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            msg = "The server is running in -> " + "algo" + ":5000";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "The server was not launched!";
        } finally {
            Log.d("servico", msg);
        }
    }

    public void stopserver(){
        Log.d("servico", "stopserver");
//        AppController app = AppController.getInstance();
        if(server != null) {
            server.stop();
            server = null;
        }

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


            launchServer();
//            final WebServerBASA server = new WebServerBASA(activity);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    time+=1;
                    Log.d("servico", "time:"+time);
                    if(time > 50){
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