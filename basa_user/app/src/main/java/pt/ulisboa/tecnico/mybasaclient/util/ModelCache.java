package pt.ulisboa.tecnico.mybasaclient.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Type;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.User;


public class ModelCache<T> {

    public void saveModel(T list, String TAG){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
        if(!TAG.equals(Global.DATA_USER)){

            User user = User.getLoggedUser();
            TAG = TAG + "|"+user.getUuid();

        }


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
        if(!TAG.equals(Global.DATA_USER)){

            User user = User.getLoggedUser();
            TAG = TAG + "|"+user.getUuid();

        }
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
