package pt.ulisboa.tecnico.basa.model.recipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaosampaio on 27-03-2016.
 */
public class Recipe {

    private List<TriggerAction> triggers;
    private List<TriggerAction> actions;

    private String shortName;
    private String description;

    public Recipe() {
        triggers = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public Recipe(List<TriggerAction> triggers, List<TriggerAction> actions) {
        this.triggers = triggers;
        this.actions = actions;
    }

    public String getRecipeDescription(){

        String msg = "If ";
        String tmp;
        for(TriggerAction trigger: triggers){
            tmp = trigger.getParameterTitle();
            msg += Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);
        }
        msg += ", then ";
        for(TriggerAction action: actions){
            tmp = action.getParameterTitle();
            msg += Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);
        }


        return msg;

    }


    public List<TriggerAction> getTriggers() {
        return triggers;
    }

    public List<TriggerAction> getActions() {
        return actions;
    }

    public void setActions(List<TriggerAction> actions) {
        this.actions = actions;
    }

    public void setTriggers(List<TriggerAction> triggers) {
        this.triggers = triggers;
    }


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
