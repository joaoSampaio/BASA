package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventBrightness;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.event.EventUserLocation;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;

/**
 * Created by Sampaio on 05/08/2016.
 */
public class BasaSensorManager implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLight;
    private float mLightLvl = -1;
    private InterestEventAssociation interestTime;

    public BasaSensorManager() {
        mSensorManager = (SensorManager) AppController.getAppContext().getSystemService(Context.SENSOR_SERVICE);

        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager.registerListener(this, mLight, 10000000);
        Log.d("BasaSensorManager", "start");


        interestTime = new InterestEventAssociation(Event.TIME, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {
                if(event instanceof EventTime){
                    long time = ((EventTime)event).getDate();


                    if(mLightLvl >= 0)
                    AppController.getInstance().getBasaManager().getEventManager()
                            .addEvent(new EventBrightness(new Integer((int)mLightLvl)));



                }
            }
        }, 0);
        AppController.getInstance().getBasaManager().getEventManager().registerInterest(interestTime);


    }

    public void destroy(){
        mSensorManager.unregisterListener(this);
    }


    public int getCurrentLightLvl(){
        return new Integer((int)mLightLvl);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        mLightLvl = lux;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
