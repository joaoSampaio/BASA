package pt.ulisboa.tecnico.basa.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import pt.ulisboa.tecnico.basa.app.AppController;

/**
 * Created by Sampaio on 26/04/2016.
 */
public class TimeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("time", "TimeBroadcastReceiver");
        AppController.getInstance().onTimerIntent();
    }
}
