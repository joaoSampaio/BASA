package pt.ulisboa.tecnico.basa.model.recipe;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

public abstract class TriggerAction {

    public final static int TEMPERATURE = 0;
    public final static int LIGHT_ON = 1;
    public final static int SPEECH = 2;
    public final static int EMAIL = 3;

    public final static int CLAP = 100;
    public final static int SWITCH = 102;
    public final static int USER_LOCATION = 106;
    public final static int TALK = 107;
    public static final int TRIGGER = 0;
    public static final int TRIGGER_ACTION = 1;


//    @SerializedName("CLASSNAME")
    public String type;


    private int triggerActionId;
    private String title;
    private int resId;
    private Map<String, Object> alternatives;
    private List<String> parameters;



    public TriggerAction(int triggerActionId, String title, int resId) {
        this.triggerActionId = triggerActionId;
        this.title = title;
        this.resId = resId;
        parameters = new ArrayList<>();
        alternatives = new HashMap<>();
        type = this.getClass().getSimpleName();
//        type = this.getClass().getName();

    }

    public TriggerAction(int triggerActionId) {
        this.triggerActionId = triggerActionId;
        this.title = getTitle(triggerActionId);
        this.resId = getResId(triggerActionId);
        parameters = new ArrayList<>();
        alternatives = new HashMap<>();
        type = this.getClass().getSimpleName();
    }

    public abstract int getColor();

    public abstract String getParameterTitle();

    public abstract View.OnClickListener getListener(Context ctx, TriggerActionParameterSelected triggerActionParameterSelected);

    public static int getResId(int triggerId){
        int resId = -1;
        switch (triggerId){

            case TEMPERATURE:
                resId = R.drawable.ic_temperature_trigger;
                break;
            case TriggerAction.LIGHT_ON:
                resId = R.drawable.ic_light_on;
                break;
            case TriggerAction.SPEECH:
                resId = R.drawable.ic_speech;
                break;
            case TriggerAction.TALK:
                resId = R.drawable.ic_talk;
                break;
            case TriggerAction.EMAIL:
                resId = R.drawable.ic_mail;
                break;
            case CLAP:
                resId = R.drawable.ic_clap;
                break;
            case SWITCH:
                resId = R.drawable.ic_switch_on;
                break;
            case USER_LOCATION:
                resId = R.drawable.ic_user_location;
                break;
        }
        return resId;
    }

    public static String getTitle(int triggerId) {
        String msg = "";
        switch (triggerId){
            case TriggerAction.TEMPERATURE:
                msg = "Temperature";
                break;
            case TriggerAction.LIGHT_ON:
                msg = "Turn Light";
                break;
            case TriggerAction.SPEECH:
                msg = "Speech";
                break;
            case CLAP:
                msg = "Clap";
                break;
            case SWITCH:
                msg = "Light ON";
                break;
            case EMAIL:
                msg = "Email";
                break;
            case USER_LOCATION:
                msg = "Location";
                break;

        }
        return msg;
    }

    public int getTriggerActionId() {
        return triggerActionId;
    }

    public void setTriggerActionId(int triggerActionId) {
        this.triggerActionId = triggerActionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public Map<String, Object> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(Map<String, Object> alternatives) {
        this.alternatives = alternatives;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public int getParametersInt(int position) {
        return Integer.parseInt( getParameters().get(position));
    }


    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}


