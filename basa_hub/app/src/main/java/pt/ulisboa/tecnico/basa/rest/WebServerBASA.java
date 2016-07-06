package pt.ulisboa.tecnico.basa.rest;

/**
 * Created by Sampaio on 30/05/2016.
 */
import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.format.Formatter;
import android.util.Log;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistration;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationAnswer;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationToken;
import pt.ulisboa.tecnico.basa.ui.MainActivity;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;

//import org.slf4j.LoggerFactory;
public class WebServerBASA {

    private final static String STARTING_TEXT = "7e7e0d02";
    private final static String ENDING_TEXT = "7f7f";
    private MainActivity activity;
    Future longRunningTaskFuture;
    ExecutorService threadPoolExecutor;


    public WebServerBASA(MainActivity activity){
        Log.d("webserver", "WebServerBASA");
        this.activity =  activity;

        threadPoolExecutor = Executors.newSingleThreadExecutor();
        Runnable longRunningTask = new HttpRunnable();

// submit task to threadpool:
        longRunningTaskFuture = threadPoolExecutor.submit(longRunningTask);

// At some point in the future, if you want to kill the task:


//        new Thread(new HttpRunnable()).start();

    }

    public void stopServer(){
        Log.d("servico", "stopserver");
        if(longRunningTaskFuture != null)
            longRunningTaskFuture.cancel(true);
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void endpoints() {
        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello Spark MVC Framework!";
            }
        });

        get(new Route("/users") {
            @Override
            public Object handle(Request request, Response response) {

                User user = new User("Joao");
                Gson gson = new Gson();
                String json = gson.toJson(user);

                return json;
            }
        });

        post(new Route("/register") {
            @Override
            public Object handle(Request request, Response response) {

                String body = request.body();
                Log.d("webserver", "request.body():"+request.body());

                Gson gson = new Gson();


                try {
                    final UserRegistration userRegistration = gson.fromJson(body, new TypeToken<UserRegistration>() {
                    }.getType());
                    if(UserRegistrationToken.isTokenValid(userRegistration.getToken())) {

                        getActivity().getBasaManager().getUserManager().registerNewUser(userRegistration.getUsername(), userRegistration.getEmail(), userRegistration.getUuid());

                        UserRegistrationAnswer answer = new UserRegistrationAnswer();

                        response.status(200);
                        return "{\"status\": true, \"data\": "+gson.toJson(answer)+"}";

                    }
                } catch (UserRegistrationException e) {
                    e.printStackTrace();
                }

                response.status(200);
                return "{\"status\": false}";
            }
        });



        post(new Route("/broadcast") {
            @Override
            public Object handle(Request request, Response response) {

                String broadcast = request.body();
                Log.d("webserver", "request.body():"+request.body());

                if(broadcast.startsWith(STARTING_TEXT) && broadcast.endsWith(ENDING_TEXT)){
                    String valuableContent = broadcast.replace(STARTING_TEXT, "").replace(ENDING_TEXT, "");

                    String hexString = hexToASCII(valuableContent);
                    Log.d("webserver", "hexString:"+hexString);
//                    if(hexString.length() == 5){

                        char[] c = hexString.toCharArray();
                        final boolean[] values = {c[1] == '1', c[2] == '1', c[3] == '1'};

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("webserver", "values:"+values.toString());
                                getActivity().getBasaManager().getLightingManager().setLightState(values);
                            }
                        });
//                    }


                }

//7e7e0d0231313130307f7f

                response.status(200);
                return "lll";
            }
        });
    }

    private static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    private class HttpRunnable implements Runnable {

        public HttpRunnable() {

        }

        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
            Log.d("webserver", "Runnable");
            int port = Global.PORT;
            try {
                Log.d("servico", "startserver");
                setPort(port);
//                setSecure();
                endpoints();
                WifiManager wm = (WifiManager) AppController.getAppContext().getSystemService(Activity.WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                Log.d("webserver", "The server is running in -> " + ip + ":" + port);
                Log.d("servico", "The server is running in -> " + ip + ":" + port);


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("webserver", "falhou-> ");
            }


        }
    }


}
