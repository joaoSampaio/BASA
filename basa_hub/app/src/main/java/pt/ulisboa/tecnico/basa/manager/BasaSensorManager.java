package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventBrightness;
import pt.ulisboa.tecnico.basa.model.event.EventOccupantDetected;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.Pojo.FcmNotificationData;
import pt.ulisboa.tecnico.basa.rest.services.SendNotificationService;

/**
 * Created by Sampaio on 05/08/2016.
 */
public class BasaSensorManager implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mLight;
    private float mLightLvl = -1;
    private InterestEventAssociation interestTime;
    private long timeLastMovement = 0;
    public final static long NO_MOVEMENT_PERIOD = 20000; //20s
    private long timeLastNoMovement = 0;
    private boolean latestMotionReading;
    private long timeStartUp = System.currentTimeMillis();

    public BasaSensorManager() {
        mSensorManager = (SensorManager) AppController.getAppContext().getSystemService(Context.SENSOR_SERVICE);
        setUpLightSensor();
        setUpMotionSensor();

    }


    private void setUpMotionSensor(){
        timeLastMovement = System.currentTimeMillis();
        timeLastNoMovement = System.currentTimeMillis();
        latestMotionReading = false;

    }

    public void setMotionSensorDetected(boolean isDetected){
        long current = System.currentTimeMillis();

        if((current - timeStartUp) < 5000)
        {
            Log.d("BasaSensorManager", "ignoring first 5s since start app");
            return;
        }

        if (isDetected) {


            String topic = AppController.getInstance().getDeviceConfig().getUuid();
            String senderID = topic;
            int code = FcmNotificationData.MOVEMENT_DETECTED;
            if(AppController.getInstance().getBasaManager().getUserManager().numActiveUsersOffice() == 0
                    && (current - timeLastNoMovement) > 60000 ) {

                new SendNotificationService(topic, "Movement has been detected", senderID, code, new CallbackMultiple() {
                    @Override
                    public void success(Object response) {

                    }

                    @Override
                    public void failed(Object error) {

                    }
                }).execute();
            }

            timeLastMovement = current;
            timeLastNoMovement = current;
            AppController.getInstance().getBasaManager().getEventManager().addEvent(new EventOccupantDetected(true));
            latestMotionReading = true;



        }else {
            latestMotionReading = false;
            if((current - timeLastNoMovement > NO_MOVEMENT_PERIOD)){
                AppController.getInstance().getBasaManager().getEventManager().addEvent(new EventOccupantDetected(false, (int)(current - timeLastMovement)/1000));

                timeLastNoMovement = System.currentTimeMillis();
            }
        }
    }


    private void setUpLightSensor(){
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

    public int getTimeSinceLastMovement(){
        long current = System.currentTimeMillis();
        return (int)(current - timeLastMovement)/1000;

    }

    public boolean isLatestMotionReading() {
        return latestMotionReading;
    }
}
