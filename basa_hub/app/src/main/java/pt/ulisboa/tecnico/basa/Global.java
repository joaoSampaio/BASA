package pt.ulisboa.tecnico.basa;

import android.graphics.Color;

/**
 * Created by joaosampaio on 23-02-2016.
 */
public class Global {
    public static final String SSID = "Connectify-me";
    public static final String FIREBASE_URL = "https://basa-2a0c9.firebaseio.com";
    public static final String FIREBASE_EMAIL = "FIREBASE_EMAIL";
    public static final String FIREBASE_PASS = "FIREBASE_PASS";
    public static final int PORT = 5002;


    public final static int TAG_TEMPERATURE_FRAGMENT = 0;



    public final static int PAGE_LIGHTS = 0;
    public final static int PAGE_TEMPERATURE = 1;
    public final static int PAGE_OPTIONS = 2;

    public final static int PAGE_SETUP = 0;
    public final static int PAGE_SETTINGS = 1;

    public final static int PAGE_SETUP_WELCOME = 0;
    public final static int PAGE_SETUP_BASIC = 1;
    public final static int PAGE_SETUP_DATABASE = 2;
    public final static int PAGE_SETUP_LIGHT_TEMP = 3;
    public final static int PAGE_SETUP_LOCATION = 4;
    public final static int PAGE_SETUP_SECURITY = 5;

    public static final String skipTop = "skipTop";
    public static final String skipBottom = "skipBottom";
    public static final String skipLeft = "skipLeft";
    public static final String skipRight = "skipRight";


    public static final String OFFLINE_DEVICE_CONFIG = "OFFLINE_DEVICE_CONFIG";
    public static final String OFFLINE_RECIPES = "OFFLINE_RECIPES4";
    public static final String OFFLINE_WEATHER = "OFFLINE_WEATHER";
    public static final String OFFLINE_TEMPERATURE_OUTPUT = "OFFLINE_TEMPERATURE_OUTPUT";
    public static final String OFFLINE_IP_TEMPERATURE = "OFFLINE_IP_TEMPERATURE";
    public static final String OFFLINE_USERS = "OFFLINE_USERS";
    public static final String OFFLINE_TOKEN = "OFFLINE_TOKEN";

    public static final String KEY_SALT = "KEY_SALT";

    public static final int COLOR_HEAT = Color.parseColor("#F57F17");
    public static final int COLOR_COLD = Color.parseColor("#ff33b5e5");

}
