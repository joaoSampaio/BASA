package pt.ulisboa.tecnico.basa.manager;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
import pt.ulisboa.tecnico.basa.model.Status;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistration;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationAnswer;
import pt.ulisboa.tecnico.basa.model.registration.UserRegistrationToken;
import pt.ulisboa.tecnico.basa.rest.WebServerBASA;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;

/**
 * Created by Sampaio on 16/04/2016.
 */
public class WebServerManager {

    private Launch2Activity activity;
    private WebServerBASA server;



    public WebServerManager(Launch2Activity activity) {
        this.activity = activity;
//        launchServer();
        server = new WebServerBASA(activity);
    }

    public void launchServer(){
        AppController app = AppController.getInstance();
        if(app.server == null)
            app.server = new AsyncHttpServer();
        AsyncHttpServer server = app.server;

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

                            getActivity().getBasaManager().getUserManager().registerNewUser(userRegistration.getUsername(), userRegistration.getEmail(), userRegistration.getUuid());

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

            WifiManager wm = (WifiManager) getActivity().getSystemService(Activity.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            msg = "The server is running in -> " + ip + ":5000";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "The server was not launched!";
        } finally {
            Log.d("webserver", msg);
        }
    }

    public void destroy(){
        AppController app = AppController.getInstance();
        if(app.getServer() != null) {
            app.getServer().stop();
            app.setServer(null);
        }
        if(server != null) {
            server.stopServer();
            server = null;
        }
    }

    public Launch2Activity getActivity() {
        return activity;
    }

    public void setActivity(Launch2Activity activity) {
        this.activity = activity;
    }

























}
