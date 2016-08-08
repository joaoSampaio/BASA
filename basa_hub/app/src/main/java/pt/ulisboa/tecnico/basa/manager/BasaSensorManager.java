package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import pt.ulisboa.tecnico.basa.app.AppController;

/**
 * Created by Sampaio on 05/08/2016.
 */
public class BasaSensorManager implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLight;

    public BasaSensorManager() {
        mSensorManager = (SensorManager) AppController.getAppContext().getSystemService(Context.SENSOR_SERVICE);

        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager.registerListener(this, mLight, 10000000);
        Log.d("BasaSensorManager", "start");
    }

    public void destroy(){
        mSensorManager.unregisterListener(this);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        Log.d("BasaSensorManager", "lux->: "+lux);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
