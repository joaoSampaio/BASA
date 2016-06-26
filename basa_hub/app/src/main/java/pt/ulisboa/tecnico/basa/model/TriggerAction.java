package pt.ulisboa.tecnico.basa.model;

import pt.ulisboa.tecnico.basa.R;

public class TriggerAction extends RecipeEvent {

    public final static int TEMPERATURE = 0;
    public final static int LIGHT_ON = 1;
    public final static int VOICE = 2;
    public final static int EMAIL = 3;
    public final static int LIGHT_OFF = 4;


    public TriggerAction(int triggerActionId, String title, int resId) {
        super(triggerActionId, title, resId);

    }

    public static int getResId(int triggerId){
        int resId = -1;
        switch (triggerId){

            case TriggerAction.TEMPERATURE:
                resId = R.drawable.ic_temperature_trigger;
                break;
            case TriggerAction.LIGHT_ON:
                resId = R.drawable.ic_light_on;
                break;
            case TriggerAction.LIGHT_OFF:
                resId = R.drawable.ic_light;
                break;

            case TriggerAction.VOICE:
                resId = R.drawable.ic_talk;
                break;
            case TriggerAction.EMAIL:
                resId = R.drawable.ic_mail;
                break;
        }
        return resId;
    }

    public static String getLabelFromId(int triggerId){
        String msg = "";
        switch (triggerId){
            case TriggerAction.TEMPERATURE:
                msg = "Enter temperature below";
                break;

            case TriggerAction.VOICE:
                msg = "Enter voice command below";
                break;

        }
        return msg;
    }

    //has three choices
    public static boolean isTriggerComplex(int triggerId){
        return (triggerId == TriggerAction.TEMPERATURE);

    }

    public static boolean isTriggerSimple(int triggerId){
        return ( triggerId == TriggerAction.VOICE);

    }

}


