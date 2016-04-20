package pt.ulisboa.tecnico.basa.manager;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.Status;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.ui.MainActivity;

/**
 * Created by Sampaio on 16/04/2016.
 */
public class WebServerManager {

    private MainActivity activity;




    public WebServerManager(MainActivity activity) {
        this.activity = activity;
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

    public void stopServer(){
        AppController app = AppController.getInstance();
        if(app.getServer() != null) {
            app.getServer().stop();
            app.setServer(null);
        }
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void setActivity(MainActivity activity) {
        this.activity = activity;
    }

























}
