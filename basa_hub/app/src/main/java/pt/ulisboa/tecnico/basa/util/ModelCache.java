package pt.ulisboa.tecnico.basa.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.LightOnAction;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LocationTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.SpeechTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TemperatureTrigger;

public class ModelCache<T> {

    public void saveModel(T list, String TAG){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
        String json = new Gson().toJson(list);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TAG, json);
        editor.commit();
        Log.d("myapp", "json:" +json);
    }

    public T loadModel(Type type, String TAG){
        return loadModel( type, TAG, "");
    }

    public T loadModel(Type type, String TAG, String defaultValue){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());

        String contacts = "";
        try {
            contacts = sp.getString(TAG, defaultValue);
        }catch (Exception e){
            Log.d("myapp", "error:" + e.getMessage());
        }
        if (contacts.equals("")) {
            return null;
        } else {
            Log.d("loadModel", "loadModel->" + contacts);
            T contactsList = new Gson().fromJson(contacts, type);
            return contactsList;
        }
    }

    public static boolean keyExists(String key){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
        return sp.contains(key);
    }



    public List<Recipe> loadRecipes(){

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
        String recipeString = sp.getString(Global.OFFLINE_RECIPES, "");

        if(recipeString.isEmpty()){
            return new ArrayList<Recipe>();
        }
        Log.d("json", "recipeString:"+recipeString);
        RuntimeTypeAdapterFactory<TriggerAction> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(TriggerAction.class, "type")
                .registerSubtype(LightOnAction.class)
                .registerSubtype(LocationTrigger.class)
                .registerSubtype(SpeechTrigger.class)
                .registerSubtype(TemperatureTrigger.class);

        Gson gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();

        Type listType = new TypeToken<List<Recipe>>(){}.getType();
        List<Recipe> fromJson = gson.fromJson(recipeString, listType);
        return fromJson;
    }
}
