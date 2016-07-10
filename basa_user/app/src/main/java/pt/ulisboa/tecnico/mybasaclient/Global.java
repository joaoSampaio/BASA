package pt.ulisboa.tecnico.mybasaclient;

import android.graphics.Color;

/**
 * Created by Sampaio on 28/06/2016.
 */
public class Global {

    public final static int USER = 0;
    public final static int HOME = 1;



    public final static int DIALOG_ADD_DEVICE = 2;
    public final static int DIALOG_ADD_ZONE_PART1 = 3;
    public final static int DIALOG_ADD_ZONE_PART2 = 4;
    public final static int DIALOG_SETTINGS_ZONE = 5;
    public final static int DIALOG_SETTINGS_ZONE_INFO = 6;
    public final static int DIALOG_DEVICE = 7;
    public final static int DIALOG_INFO = 8;
    public final static int DIALOG_ACCOUNT = 9;
    public final static int DIALOG_DEVICE_TEMPERATURE = 10;
    public final static int DIALOG_DEVICE_CAMERA = 11;
    public final static int DIALOG_DEVICE_LIGHT = 12;
    public final static int DIALOG_DEVICE_SETTINGS = 13;

    public final static String CURRENT_ZONE = "CURRENT_ZONE";
    public final static String DATA_CURRENT_DEVICE = "DATA_CURRENT_DEVICE";

    public final static String DATA_USER = "DATA_USER";
    public final static String DATA_ZONE = "DATA_ZONE";




    public static final int COLOR_HEAT = Color.parseColor("#F57F17");
    public static final int COLOR_COLD = Color.parseColor("#ff33b5e5");



    public final static String HUB_ENDPOINT_MAKE_CHANGES = "/make-changes";
    public final static String HUB_ENDPOINT_ALIVE = "/alive";
    public final static String HUB_ENDPOINT_STATUS = "/status";





}
