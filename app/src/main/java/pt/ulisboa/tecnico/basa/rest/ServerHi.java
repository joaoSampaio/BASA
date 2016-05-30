package pt.ulisboa.tecnico.basa.rest;

/**
 * Created by Sampaio on 30/05/2016.
 */
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Process;
import android.util.Log;

import com.estimote.sdk.repackaged.gson_v2_3_1.com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.util.MulticastLockSingleton;
import spark.Request;
import spark.Response;
import spark.Route;

//import org.slf4j.LoggerFactory;

import static spark.Spark.*;
public class ServerHi {


    public ServerHi(){
        Log.d("ServerHi", "ServerHi");
        new Thread(new TesteHttp()).start();
    }

//    @RequestMapping(method = RequestMethod.GET)
//    public String home() {
//        RestTemplate restTemplate = new RestTemplate();
//        logger.info("Spring Android Showcase");
//        return "home";
//    }


    public static void getCert() {
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
    }

    private class TesteHttp implements Runnable {
        /*
         * Defines the code to run for this task.
         */

        private Context ctx;

        public TesteHttp() {

        }

        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
            Log.d("ServerHi", "Runnable");
            //port(5001);
            setPort(5001);
            getCert();


        }
    }


}
