package pt.ulisboa.tecnico.basa;

import android.graphics.Color;

/**
 * Created by joaosampaio on 23-02-2016.
 */
public class Global {
    public static final String SSID = "Connectify-me";
//    public static final String SSID = "eduroam";
    public static final int PORT = 5001;


    public final static int TAG_TEMPERATURE_FRAGMENT = 0;



    public final static int PAGE_LIGHTS = 0;
    public final static int PAGE_TEMPERATURE = 1;
    public final static int PAGE_OPTIONS = 2;

    public static final String skipTop = "skipTop";
    public static final String skipBottom = "skipBottom";
    public static final String skipLeft = "skipLeft";
    public static final String skipRight = "skipRight";


    public static final String OFFLINE_RECIPES = "OFFLINE_RECIPES";
    public static final String OFFLINE_LOCATION = "OFFLINE_LOCATION";
    public static final String OFFLINE_WEATHER = "OFFLINE_WEATHER";
    public static final String OFFLINE_TEMPERATURE_OUTPUT = "OFFLINE_TEMPERATURE_OUTPUT";
    public static final String OFFLINE_IP_TEMPERATURE = "OFFLINE_IP_TEMPERATURE";
    public static final String OFFLINE_USERS = "OFFLINE_USERS";


    public static final int COLOR_HEAT = Color.parseColor("#F57F17");
    public static final int COLOR_COLD = Color.parseColor("#ff33b5e5");

}
