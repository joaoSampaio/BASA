package pt.ulisboa.tecnico.basa.model.recipe;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

public abstract class TriggerAction {

    public final static int TRIGGER_TEMPERATURE = 1;
    public final static int TRIGGER_LIGHT_SENSOR = 2;
    public final static int TRIGGER_MOTION_SENSOR = 3;
    public final static int TRIGGER_SPEECH = 4;
    public final static int TRIGGER_USER_LOCATION = 5;
    public final static int TRIGGER_TIME = 6;
    public final static int TRIGGER_LIGHT_STATE = 7;

    public final static int ACTION_LIGHT_ON = 101;
    public final static int ACTION_TALK = 102;
    public final static int ACTION_CHANGE_TEMPERATURE = 103;










    public static final int TRIGGER = 0;
    public static final int TRIGGER_ACTION = 1;


//    @SerializedName("CLASSNAME")
    public String type;


    private int triggerActionId;
    private String title;
    private int resId;
    private LinkedHashMap<String, Object> alternatives;
    private List<String> parameters;
    private String description;



    public TriggerAction(int triggerActionId, String title, int resId) {
        this.triggerActionId = triggerActionId;
        this.title = title;
        this.resId = resId;
        parameters = new ArrayList<>();
        alternatives = new LinkedHashMap<>();
        type = this.getClass().getSimpleName();
//        type = this.getClass().getName();

    }

    public TriggerAction(int triggerActionId) {
        this.triggerActionId = triggerActionId;
        this.title = getTitle(triggerActionId);
        this.resId = getResId(triggerActionId);
        parameters = new ArrayList<>();
        alternatives = new LinkedHashMap<>();
        type = this.getClass().getSimpleName();
    }

    public abstract int getColor();

    public abstract String getParameterTitle();

    public abstract View.OnClickListener getListener(Context ctx, TriggerActionParameterSelected triggerActionParameterSelected);

    public abstract void setUpCustomView(ViewGroup parent);
    public abstract void destroyCustomView();



    public static int getResId(int triggerId){
        int resId = -1;
        switch (triggerId){

            case TRIGGER_TEMPERATURE:
                resId = R.drawable.ic_temperature_trigger;
                break;
            case TriggerAction.ACTION_LIGHT_ON:
                resId = R.drawable.ic_light_on;
                break;
            case TriggerAction.TRIGGER_SPEECH:
                resId = R.drawable.ic_speech;
                break;
            case TriggerAction.ACTION_TALK:
                resId = R.drawable.ic_talk;
                break;
            case TRIGGER_USER_LOCATION:
                resId = R.drawable.ic_user_location;
                break;
            case TRIGGER_LIGHT_SENSOR:
                resId = R.drawable.ic_brightness;
                break;
            case TRIGGER_MOTION_SENSOR:
                resId = R.drawable.ic_motion2;
                break;
            case ACTION_CHANGE_TEMPERATURE:
                resId = R.drawable.ic_temperature_trigger;
                break;

            case TRIGGER_TIME:
                resId = R.drawable.ic_time_trigger;
                break;

            case TRIGGER_LIGHT_STATE:
                resId = R.drawable.ic_light_on;
                break;

        }
        return resId;
    }

    public static int getInvertedResId(int triggerId){
        int resId = -1;
        switch (triggerId){

            case TRIGGER_TEMPERATURE:
                resId = R.drawable.ic_temperature_trigger_inv;
                break;
            case TriggerAction.ACTION_LIGHT_ON:
                resId = R.drawable.ic_light_on;
                break;
            case TriggerAction.TRIGGER_SPEECH:
                resId = R.drawable.ic_speech;
                break;
            case TriggerAction.ACTION_TALK:
                resId = R.drawable.ic_talk;
                break;


            case TRIGGER_USER_LOCATION:
                resId = R.drawable.ic_user_location;
                break;
            case TRIGGER_LIGHT_SENSOR:
                resId = R.drawable.ic_brightness_inv;
                break;
            case TRIGGER_MOTION_SENSOR:
                resId = R.drawable.ic_motion_inv;
                break;
            case ACTION_CHANGE_TEMPERATURE:
                resId = R.drawable.ic_temperature_trigger_inv;
                break;
            case TRIGGER_TIME:
                resId = R.drawable.ic_time_trigger;
                break;
            case TRIGGER_LIGHT_STATE:
                resId = R.drawable.ic_light_on;
                break;

        }
        return resId;
    }

    public static String getTitle(int triggerId) {
        String msg = "";
        switch (triggerId){
            case TriggerAction.TRIGGER_TEMPERATURE:
                msg = "Temperature";
                break;
            case TriggerAction.ACTION_LIGHT_ON:
                msg = "Turn Light";
                break;
            case TriggerAction.TRIGGER_SPEECH:
                msg = "Speech";
                break;
            case ACTION_TALK:
                msg = "Say";
                break;

            case TRIGGER_USER_LOCATION:
                msg = "Location";
                break;
            case TRIGGER_LIGHT_SENSOR:
                msg = "Light lvl";
                break;
            case TRIGGER_MOTION_SENSOR:
                msg = "Motion sensor";
                break;
            case ACTION_CHANGE_TEMPERATURE:
                msg = "Change Temperature";
                break;
            case TRIGGER_TIME:
                msg = "Date time";
                break;
            case TRIGGER_LIGHT_STATE:
                msg = "Light state";
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
        return getResId(triggerActionId);
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public Map<String, Object> getAlternatives() {
        return alternatives;
    }

    public void setAlternatives(LinkedHashMap<String, Object> alternatives) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}


