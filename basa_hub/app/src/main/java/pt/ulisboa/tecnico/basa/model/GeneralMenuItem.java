package pt.ulisboa.tecnico.basa.model;

import pt.ulisboa.tecnico.basa.R;

/**
 * Created by Sampaio on 12/08/2016.
 */
public class GeneralMenuItem {

    public final static int IFTTT = 0;
    public final static int SETTINGS = 1;
    public final static int INTRUDER = 2;
    public final static int SETTINGS_TEMP = 3;
    public final static int REGISTER = 4;
    public final static int HISTORY = 5;

    private int id;


    public GeneralMenuItem(int id) {
        this.id = id;
    }

    public  String getTitle(){
        return getTitle(id);
    }

    public static String getTitle(int idMenu){
        String msg = "not available";
        switch (idMenu){

            case IFTTT:
                msg = "Recipes";
                break;
            case SETTINGS:
                msg = "Settings";
                break;
            case INTRUDER:
                msg = "Intruder detection";
                break;
            case SETTINGS_TEMP:
                msg = "Temp & Light";
                break;
            case REGISTER:
                msg = "User Registration";
                break;
            case HISTORY:
                msg = "Event History";
                break;

        }
        return msg;
    }

    public int getResId(){
        return getResId(id);
    }

    public static int getResId(int idMenu){
        int resid = 0;
        switch (idMenu){

            case IFTTT:
                resid = R.drawable.ic_ifttt;
                break;
            case SETTINGS:
                resid =  R.drawable.ic_settings;
                break;
            case INTRUDER:
                resid =  R.drawable.ic_intruder;
                break;
            case SETTINGS_TEMP:
                resid =  R.drawable.ic_temperature_light;
                break;
            case REGISTER:
                resid =  R.drawable.ic_new_user;
                break;
            case HISTORY:
                resid =  R.drawable.ic_history;
                break;

        }
        return resid;
    }

    public int getId() {
        return id;
    }
}
