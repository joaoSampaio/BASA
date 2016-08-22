package pt.ulisboa.tecnico.basa.model.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pt.ulisboa.tecnico.basa.model.recipe.action.LightOnAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.SpeechAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.TemperatureAction;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LightSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LocationTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.MotionSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.SpeechTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TemperatureTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TimeTrigger;
import pt.ulisboa.tecnico.basa.util.RuntimeTypeAdapterFactory;

/**
 * Created by joaosampaio on 27-03-2016.
 */
public class Recipe {

    private List<TriggerAction> triggers;
    private List<TriggerAction> actions;

    private boolean active;

    private String id;
    private String description;

    public Recipe() {
        triggers = new ArrayList<>();
        actions = new ArrayList<>();
        this.id = UUID.randomUUID().toString();
    }

    public Recipe(List<TriggerAction> triggers, List<TriggerAction> actions) {
        this.triggers = triggers;
        this.actions = actions;
        this.id = UUID.randomUUID().toString();
    }

    public String getTriggersDescription(){
        String msg = "";
        String tmp;
        for(int i=0; i< triggers.size(); i++){
            tmp = triggers.get(i).getParameterTitle();
            if(i > 0){
                msg += " and ";
            }
            msg += Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);

        }


        return msg;
    }

    public String getActionsDescription(){
        String msg = "";
        String tmp;
        for(int i=0; i< actions.size(); i++){
            tmp = actions.get(i).getParameterTitle();
            if(i > 0){
                msg += " and ";
            }
            msg += Character.toLowerCase(tmp.charAt(0)) + tmp.substring(1);

        }


        return msg;
    }


    public String getRecipeDescription(){

        String msg = "If ";
        msg += getTriggersDescription();
        msg += ", then ";
        msg += getActionsDescription();

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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public static Recipe findRecipeById(List<Recipe> recipes, String idRecipe){
        for(Recipe recipe : recipes){
            if(recipe.getId().equals(idRecipe))
                return recipe;
        }
        return null;
    }


    public Recipe createCopy(){

        Gson gson = new Gson();
        String recipeString = gson.toJson(this);

        RuntimeTypeAdapterFactory<TriggerAction> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(TriggerAction.class, "type")
                .registerSubtype(LightOnAction.class)
                .registerSubtype(LocationTrigger.class)
                .registerSubtype(SpeechTrigger.class)
                .registerSubtype(LightSensorTrigger.class)
                .registerSubtype(MotionSensorTrigger.class)
                .registerSubtype(SpeechAction.class)
                .registerSubtype(TimeTrigger.class)
                .registerSubtype(TemperatureAction.class)
                .registerSubtype(TemperatureTrigger.class);

        gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();


        Type listType = new TypeToken<Recipe>(){}.getType();
        Recipe copy = gson.fromJson(recipeString, listType);
        return copy;
    }

}
