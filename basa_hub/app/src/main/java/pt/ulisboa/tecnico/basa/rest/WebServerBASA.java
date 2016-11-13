package pt.ulisboa.tecnico.basa.rest;

/**
 * Created by Sampaio on 30/05/2016.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.util.Log;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletResponse;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
import pt.ulisboa.tecnico.basa.manager.BasaManager;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;
import pt.ulisboa.tecnico.basa.model.DeviceStatus;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.model.UserLocation;
import pt.ulisboa.tecnico.basa.model.registration.BasaDeviceInfo;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistration;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationAnswer;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationToken;
import pt.ulisboa.tecnico.basa.rest.Pojo.ChangeTemperatureLights;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;
import pt.ulisboa.tecnico.basa.util.BitmapMotionTransfer;
import pt.ulisboa.tecnico.basa.util.ModelCache;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import static spark.Spark.get;
import static spark.Spark.post;
//import spark.Spark;

//import org.slf4j.LoggerFactory;
public class WebServerBASA {

    private final static String STARTING_TEXT = "7e7e0d02";
    private final static String ENDING_TEXT = "7f7f";
    private Launch2Activity activity;
    private BitmapMotionTransfer transfer;
    Future longRunningTaskFuture;
    ExecutorService threadPoolExecutor;
    Runnable runnable;
    Handler handler;
    Bitmap live;
    boolean isBitmapRegistered = false;

    public void setActivity(Launch2Activity activity) {
        if(this.activity != null)
            return;
        this.activity = activity;
        if(!isBitmapRegistered){
            if(getActivity() != null && getActivity().getmHelper() != null){
                isBitmapRegistered = true;
                getActivity().getmHelper().addImageListener(transfer);
            }
        }
    }

    public WebServerBASA(Launch2Activity activity){
        Log.d("webserver", "WebServerBASA");
        this.activity =  activity;
        transfer = new BitmapMotionTransfer() {
            @Override
            public void onBitMapAvailable(Bitmap bitmap) {
//                Log.d("servico", "onBitMapAvailable (live == bitmap)-> " + (live == bitmap));
                live = bitmap;
            }
        };
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("servico", "getActivity() != null" + (getActivity() != null));

                if(getActivity() != null && getActivity().getmHelper() != null){
                    isBitmapRegistered = true;
                    getActivity().getmHelper().addImageListener(transfer);
                }
            }
        });


        threadPoolExecutor = Executors.newSingleThreadExecutor();
        Runnable longRunningTask = new HttpRunnable();
        longRunningTaskFuture = threadPoolExecutor.submit(longRunningTask);

// At some point in the future, if you want to kill the task:

//        new Thread(new HttpRunnable()).start();

    }

    public void stopServer(){
        Log.d("servico", "stopserver");
        if(getActivity() != null && getActivity().getmHelper() != null) {
            getActivity().getmHelper().removeImageListener(transfer);
        }
        if(longRunningTaskFuture != null)
            longRunningTaskFuture.cancel(true);
        Spark.stop();
    }

    public Launch2Activity getActivity() {
        return activity;
    }


    public void endpoints() {
        get(new Route("/hello") {
            @Override
            public Object handle(Request request, Response response) {
                return "Hello Spark MVC Framework!";
            }
        });


        get(new Route("/live") {
            @Override
            public Object handle(Request request, Response response) {

                Log.d("servico", "live != null" + (live != null));

                if(live != null) {
                    byte[] data = null;
                    try {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        live.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        data = stream.toByteArray();
//                        data = Files.readAllBytes(path);
                    } catch (Exception e1) {

                        e1.printStackTrace();
                    }

                    HttpServletResponse raw = response.raw();
                    try {
                        raw.getOutputStream().write(data);
                        raw.getOutputStream().flush();
                        raw.getOutputStream().close();
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    return raw;

                }else{
                    return "{\"status\": false}";
                }
            }
        });

        get(new Route("/alive") {
            @Override
            public Object handle(Request request, Response response) {
                return "{\"status\": true}";
            }
        });

        post(new Route("/make-changes") {
            @Override
            public Object handle(Request request, Response response) {

                String body = request.body();

                Gson gson = new Gson();
                try {
                    final ChangeTemperatureLights changeTemperatureLights = gson.fromJson(body, new TypeToken<ChangeTemperatureLights>() {
                    }.getType());

                        if(AppController.getInstance().getBasaManager() != null){

                            final int temperature = changeTemperatureLights.getTargetTemperature();
                            final boolean[] lights = changeTemperatureLights.getLightsState();
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    BasaManager manager = AppController.getInstance().getBasaManager();
                                    if(temperature > 0 && manager.getTemperatureManager() != null)
                                        manager.getTemperatureManager().onChangeTargetTemperatureFromClient(temperature);
                                    if(lights != null && lights.length > 0 && manager.getLightingManager() != null)
                                        manager.getLightingManager().setLightState(lights, true, true, false);

                                }
                            });
                            response.status(200);
                            return "{\"status\": true}";
                        }



                    Log.d("servico", "4:");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                response.status(200);
                Log.d("servico", "{\"status\": false}");
                return "{\"status\": false}";
            }
        });

        get(new Route("/status") {
            @Override
            public Object handle(Request request, Response response) {


                Gson gson = new Gson();

                //verify permission in header
                try {

                    String session = request.headers("session");
//                    if(isRequestAuthorized(session)){


                        if(AppController.getInstance().getBasaManager() != null){
                            boolean[] lights = AppController.getInstance().getBasaManager()
                                    .getLightingManager().getLights();

                            Log.d("servico", "getLatestTemperature:" + (AppController.getInstance().getBasaManager()
                                    .getTemperatureManager().getCurrentTemperature()));

                            int temperature = (AppController.getInstance().getBasaManager()
                                    .getTemperatureManager().getCurrentTemperature() > 0)?
                                    AppController.getInstance().getBasaManager().getTemperatureManager()
                                            .getCurrentTemperature() : -100;
//                            double temperature = AppController.getInstance().basaManager.getTemperatureManager().getLatestTemperature().getTemperature();
                            DeviceStatus deviceStatus = new DeviceStatus(lights, temperature);

                            Log.d("servico", "3:");
                            response.status(200);
                            Log.d("servico", "{\"status\": true, \"data\": "+gson.toJson(deviceStatus)+"}");
                            return "{\"status\": true, \"data\": "+gson.toJson(deviceStatus)+"}";

                        }
//                    }
                    Log.d("servico", "4:");
                } catch (Exception e) {
                    e.printStackTrace();
                }



                return "{\"status\": false}";
            }
        });


        get(new Route("/register") {
            @Override
            public Object handle(Request request, Response response) {

                BasaDeviceConfig conf = AppController.getInstance().getDeviceConfig();
                BasaDeviceInfo device = new BasaDeviceInfo(conf.getUuid(), conf.getName(), conf.getDescription());
                Gson gson = new Gson();
                return "{\"status\": true, \"data\": "+gson.toJson(device)+"}";
            }
        });


        post(new Route("/location") {
            @Override
            public Object handle(Request request, Response response) {

                final String body = request.body();
                Log.d("servico", "request.body():"+request.body());




                final String session = request.headers("session-id");
                Log.d("servico", "session:"+session);

                if(session != null && AppController.getInstance().getBasaManager().getUserManager().getUser(session) != null){
                    Gson gson = new Gson();
                    final UserLocation userLocation = gson.fromJson(body, new TypeToken<UserLocation>() {}.getType());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            AppController.getInstance().getBasaManager().getUserManager().addUserHeartbeat(session, userLocation);
                        }
                    });


//                    Log.d("servico", "users:"+gson.toJson(User.getUsers()));
                    return "{\"status\": true}";
                }

                response.status(200);
                Log.d("servico", "{\"status\": false}");
                return "{\"status\": false}";
            }
        });



        post(new Route("/register") {
            @Override
            public Object handle(Request request, Response response) {

                String body = request.body();
                Log.d("servico", "request.body():"+request.body());

                Gson gson = new Gson();


                try {
                    final UserRegistration userRegistration = gson.fromJson(body, new TypeToken<UserRegistration>() {
                    }.getType());
                    Log.d("servico", "1:" + (getActivity() != null));
                    if(UserRegistrationToken.isTokenValid(userRegistration.getToken())) {
                        Log.d("servico", "2:"+(AppController.getInstance().getBasaManager() != null));
                        if(AppController.getInstance().getBasaManager() != null){
                            try {
                                AppController.getInstance().getBasaManager().getUserManager().registerNewUser(userRegistration.getUsername(), userRegistration.getEmail(), userRegistration.getUuid());

                            }catch (UserRegistrationException exception){
                                //vamos permitir
                            }
                            UserRegistrationAnswer answer = new UserRegistrationAnswer(AppController.getInstance().getDeviceConfig());
                            Log.d("servico", "3:");
                            response.status(200);
                            Log.d("servico", "{\"status\": true, \"data\": "+gson.toJson(answer)+"}");
                            return "{\"status\": true, \"data\": "+gson.toJson(answer)+"}";
                        }
//                        getActivity().getBasaManager().getUserManager().registerNewUser(userRegistration.getUsername(), userRegistration.getEmail(), userRegistration.getUuid());
                        Log.d("servico", "1.1:");


                    }
                    Log.d("servico", "4:");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("servico", "5:");
                response.status(200);
                Log.d("servico", "{\"status\": false}");
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
                                getActivity().getBasaManager().getLightingManager().setLightState(values, false, true, false);
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


    public boolean isRequestAuthorized(String uuid){
        List<User> users = new ModelCache<List<User>>().loadModel(new TypeToken<List<User>>(){}.getType(), Global.OFFLINE_USERS);

        return User.userUuidExists(users, uuid);
    }

    private class HttpRunnable implements Runnable {

        public HttpRunnable() {

        }

        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Log.d("webserver", "Runnable");
            int port = Global.PORT;
            try {
                Log.d("servico", "startserver");
                Spark.setPort(port);

                endpoints();
                WifiManager wm = (WifiManager) AppController.getAppContext().getSystemService(Activity.WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                Log.d("servico", "The server is running in -> " + ip + ":" + port);


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("servico", "falhou->------------------------------------------------------------------------------------- ");
            }


        }
    }


}
