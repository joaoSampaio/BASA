package pt.ulisboa.tecnico.basa.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;


import java.lang.reflect.Type;

import pt.ulisboa.tecnico.basa.app.AppController;

public class ModelCache<T> {

    public void saveModel(T list, String TAG){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
        String json = new Gson().toJson(list);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TAG, json);
        editor.commit();
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

}
