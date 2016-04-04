package pt.ulisboa.tecnico.basa.model;

import pt.ulisboa.tecnico.basa.R;

public class Trigger extends RecipeEvent {

    public final static int CLAP = 0;
    public final static int TEMPERATURE = 1;
    public final static int SWITCH = 2;
    public final static int VOICE = 3;
    public final static int EMAIL = 4;
    public final static int HAND_PROXIMITY = 5;

    public Trigger(int triggerId, String title, int resId) {
        super(triggerId, title, resId);
    }

    public static int getResId(int triggerId){
        int resId = -1;
        switch (triggerId){
            case Trigger.CLAP:
                resId = R.drawable.ic_clap;
                break;
            case Trigger.TEMPERATURE:
                resId = R.drawable.ic_temperature_trigger;
                break;
            case Trigger.SWITCH:
                resId = R.drawable.ic_switch_on;
                break;
            case Trigger.VOICE:
                resId = R.drawable.ic_voice;
                break;
            case Trigger.EMAIL:
                resId = R.drawable.ic_clap;
                break;
            case Trigger.HAND_PROXIMITY:
                resId = R.drawable.ic_hand_proximity;
                break;
        }
        return resId;
    }

    public static String getLabelFromId(int triggerId){
        String msg = "";
        switch (triggerId){
            case Trigger.TEMPERATURE:
                msg = "Enter temperature below";
                break;

            case Trigger.VOICE:
                msg = "Enter voice command below";
                break;

        }
        return msg;
    }

    //has three choices
    public static boolean isTriggerComplex(int triggerId){
        return (triggerId == Trigger.TEMPERATURE);

    }

    public static boolean isTriggerSimple(int triggerId){
        return ( triggerId == Trigger.VOICE);

    }

}


