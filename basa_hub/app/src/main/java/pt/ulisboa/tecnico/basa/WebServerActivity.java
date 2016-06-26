package pt.ulisboa.tecnico.basa;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import pt.ulisboa.tecnico.basa.model.Status;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.app.AppController;

public class WebServerActivity extends AppCompatActivity {
    private TextView txtwebserver;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webserver);

        txtwebserver = (TextView)findViewById(R.id.txtwebserver);
        findViewById(R.id.btn_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchServer();
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopServer();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }


    private void launchServer(){
        AppController app = AppController.getInstance();
        if(app.server == null)
            app.server = new AsyncHttpServer();
        AsyncHttpServer server = app.server;

        //List<WebSocket> _sockets = new ArrayList<WebSocket>();
        String msg = "";
        try {
            count = 0;
            server.get("/users/me", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                    User user = new User("Joao");
                    Gson gson = new Gson();
                    String json = gson.toJson(user);

                    response.send(json);
                    count++;
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
                    count++;
                }
            });

// listen on port 5000
            server.listen(5000);

            WifiManager wm = (WifiManager) getSystemService(Activity.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            msg = "The server is running in -> " + ip + ":5000";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "The server was not launched!";
        } finally {
            txtwebserver.setText(msg);
        }

    }

    private void stopServer(){
        AppController app = AppController.getInstance();
        if(app.getServer() != null) {
            app.getServer().stop();
            txtwebserver.setText("Server stopped");
            app.setServer(null);
        }
    }

}
