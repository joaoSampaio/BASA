package pt.ulisboa.tecnico.basa.manager;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.EventHistory;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventBrightness;
import pt.ulisboa.tecnico.basa.model.event.EventChangeTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventClap;
import pt.ulisboa.tecnico.basa.model.event.EventCustomSwitchPressed;
import pt.ulisboa.tecnico.basa.model.event.EventLightSwitch;
import pt.ulisboa.tecnico.basa.model.event.EventOccupantDetected;
import pt.ulisboa.tecnico.basa.model.event.EventSpeech;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.event.EventUserLocation;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.LightOnAction;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LightSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LocationTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.MotionSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TimeTrigger;
import pt.ulisboa.tecnico.basa.ui.secondary.EventHistoryFragment;

public class EventManager {

    private BasaManager basaManager;
    private List<InterestEventAssociation> interests;
    private List<InterestEventAssociation> recipes;
    private Handler handler;
    private final static int PERIOD = 2000;
    private Runnable run;
    private List<Runnable> timerRunnable;
    private EventHistoryFragment.UpdateHistory updateHistory;


    public EventManager(BasaManager basaManager) {
        interests = new ArrayList<>();
        recipes = new ArrayList<>();
        this.timerRunnable = new ArrayList<>();
        this.basaManager = basaManager;
        this.handler = new Handler();
        this.run = new Runnable() {
            @Override
            public void run() {
                addEvent(new EventTime(System.currentTimeMillis()));

                handler.postDelayed(this, PERIOD);

            }
        };
        handler.post(run);
        Log.d("EVENT", "EventManager new ");
//        this.setUpCalender();
    }

    public void addEvent(Event event){
        Log.d("EVENT", "****" + eventToString(event) + "****: ");

        try {
            for (InterestEventAssociation interest: interests){
                if(interest.isType(event.getType())){
                    interest.getInterest().onRegisteredEventTriggered(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            for (InterestEventAssociation interest: recipes){
                if(interest.isType(event.getType())){
                    interest.getInterest().onRegisteredEventTriggered(event);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addEventToHistory(event);
    }

    private void addEventToHistory(Event event){

        String result = "";
        switch (event.getType()){
            case Event.OCCUPANT_DETECTED:
                if(((EventOccupantDetected)event).isDetected())
                    result = "Movement detected";
                break;
            case Event.SPEECH:
                result = "Voice cmd: " + ((EventSpeech)event).getVoice();
                break;

            case Event.USER_LOCATION:
                if (event instanceof EventUserLocation) {
                    EventUserLocation location = (EventUserLocation) event;
                    User user = AppController.getInstance().getBasaManager().getUserManager().getUser(location.getUserId());
                    String name = user != null ? user.getName() : "unknown";
                    if (location.getLocation() == EventUserLocation.TYPE_BUILDING && location.isInBuilding() && location.isFirstArrive()) {
                        result = "User " + name + " arrived in building";
                    }
                    if (location.getLocation() == EventUserLocation.TYPE_BUILDING && !location.isInBuilding()) {
                        result = "User " + name + " left the building";
                    }

                    if (location.getLocation() == EventUserLocation.TYPE_OFFICE && location.isInBuilding() && location.isFirstArrive()) {

                        result = "User " + name + " arrived in office";
                    }

                    if (location.getLocation() == EventUserLocation.TYPE_OFFICE && !location.isInBuilding()) {

                        result = "User " + name + " left the office";
                    }
                    if(!result.isEmpty())
                    AppController.getInstance().getStatisticalData().addOccupantEvent(((EventUserLocation)event));
                }

                break;
            case Event.LIGHT:
                result = "Light (" + ((EventLightSwitch)event).getLightNum() + ") is " + (((EventLightSwitch)event).isOn()? "on" : "off");
                AppController.getInstance().getStatisticalData().addLightsEvent(((EventLightSwitch)event));

                break;
            case Event.CHANGE_TEMPERATURE:
                result = "Temperature set to " + ((EventChangeTemperature)event).getTargetTemperature() + " Cº" ;
                break;
        }
        if(!result.isEmpty()){
            Log.d("EVENT","User:---->getUpdateHistory() != null:" + (getUpdateHistory() != null) );
            AppController.getInstance().getHistory().add(0,new EventHistory(result));
            if(getUpdateHistory() != null)
                getUpdateHistory().onUpdateHistory();

        }

    }


    public void registerInterestRecipe(InterestEventAssociation interest){
        this.recipes.add(interest);
    }

    public void removeInterestRecipe(InterestEventAssociation interest){
        this.recipes.remove(interest);
    }

    public void registerInterest(InterestEventAssociation interest){
        try {
            this.interests.add(interest);
        }catch (Exception e){
            Log.d("Exception","registerInterest");
        }
    }

    public void removeInterest(InterestEventAssociation interest){
        this.interests.remove(interest);
    }


    public interface RegisterInterestEvent{
        public void onRegisteredEventTriggered(Event event);
    }

    public static String eventToString(Event event){
        String result = "unknown";
        switch (event.getType()){
            case Event.OCCUPANT_DETECTED:
                result = "OCCUPANT_DETECTED->" + ((EventOccupantDetected)event).isDetected();
                break;
            case Event.TEMPERATURE:
                result = "TRIGGER_TEMPERATURE->" + ((EventTemperature)event).getTemperature();
                break;
            case Event.SPEECH:
                result = "TRIGGER_SPEECH->" + ((EventSpeech)event).getVoice();
                break;
            case Event.CUSTOM_SWITCH:
                result = "CUSTOM_SWITCH Pressed->" + ((EventCustomSwitchPressed)event).getId();
                break;
            case Event.CLAP:
                result = "CLAP ->";
                break;
            case Event.BRIGHTNESS:
                result = "BRIGHTNESS ->";
                break;
            case Event.LIGHT:
                result = "LIGHT ->" + ((EventLightSwitch)event).getLightNum() + " is " + ((EventLightSwitch)event).isOn();
                break;
            case Event.CHANGE_TEMPERATURE:
                result = "ACTION_CHANGE_TEMPERATURE  target->" + ((EventChangeTemperature)event).getTargetTemperature() ;
                break;

            case Event.USER_LOCATION:
                result = "TRIGGER_USER_LOCATION -> isInBuilding:" + ((EventUserLocation)event).isInBuilding() + " getLocation:" + ((EventUserLocation)event).getLocation();
                break;
        }

        return result;
    }

    public void reloadSavedRecipes(){
        recipes.clear();
        clearTimerRunnable();
        initSavedRecipes();
    }


    private void initSavedRecipes(){
        List<Recipe> recipes = AppController.getInstance().getCustomRecipes();
        if(recipes == null)
            recipes = new ArrayList<>();


        for(Recipe re: recipes) {
            if(!re.isActive())
                continue;
            final Recipe recipeFinal = re;
            for (final TriggerAction trigger : re.getTriggers()) {
                switch (trigger.getTriggerActionId()) {
                    case TriggerAction.TRIGGER_USER_LOCATION:
                        registerInterestRecipe(new InterestEventAssociation(Event.USER_LOCATION, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventUserLocation) {
                                    EventUserLocation location = (EventUserLocation)event;

                                    String t = "sss";
                                    //User arrives at building
                                    if(location.getLocation() == EventUserLocation.TYPE_BUILDING
                                            && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.ARRIVES_BUILDING
                                            && location.isFirstArrive()){
                                        Log.d(t,"User arrives at building" );

                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);

                                        //needs return because inside building would also be triggered
                                        return;
                                    }

                                    //User arrives at office
                                    if(location.getLocation() == EventUserLocation.TYPE_OFFICE
                                            && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.ARRIVES_OFFICE
                                            && location.isFirstArrive()){
                                        Log.d(t,"User arrives at office" );
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);

                                        //needs return because inside office would also be triggered
                                        return;
                                    }

                                    //User is inside office
                                    if(location.getLocation() == EventUserLocation.TYPE_OFFICE && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.INSIDE_OFFICE){
                                        Log.d(t,"User is inside office" );
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }

                                    //User is inside building
                                    if(location.getLocation() == EventUserLocation.TYPE_BUILDING && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.INSIDE_BUILDING){

                                        Log.d(t,"User is inside building" );
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }

                                    //User leaves building
                                    if(location.getLocation() == EventUserLocation.TYPE_BUILDING && !location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.EXIT_BUILDING){
                                        Log.d(t,"User leaves building" );
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }

                                    //User leaves office
                                    if(location.getLocation() == EventUserLocation.TYPE_OFFICE && !location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.EXIT_OFFICE){
                                        Log.d(t,"User leaves office" );
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }



                                    //No user inside building
                                    int numBuilding = AppController.getInstance().getBasaManager().getUserManager().numActiveUsersBuilding();
                                    if(trigger.getParametersInt(0) == LocationTrigger.NO_USER_IN_BUILDING && numBuilding == 0){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }

                                    //No user inside office
                                    int numOffice = AppController.getInstance().getBasaManager().getUserManager().numActiveUsersOffice();
                                    if(trigger.getParametersInt(0) == LocationTrigger.NO_USER_IN_OFFICE && numOffice == 0){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }



                                }
                            }
                        }, 0));

                        break;

                    case TriggerAction.CLAP:
                        registerInterestRecipe(new InterestEventAssociation(Event.CLAP, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventClap) {

                                    if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                        doAction(recipeFinal);


                                }
                            }
                        }, 0));
                        break;

                    case TriggerAction.TRIGGER_SPEECH:
                        registerInterestRecipe(new InterestEventAssociation(Event.SPEECH, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventSpeech) {
                                    EventSpeech speech = (EventSpeech)event;
                                    for( String s : speech.getVoice()){
                                        if(s.toLowerCase().equals(trigger.getParameters().get(1).toLowerCase())){
                                            if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                                doAction(recipeFinal);
                                            break;
                                        }
                                    }
                                }
                            }
                        }, 0));
                        break;
                    case TriggerAction.TRIGGER_LIGHT_SENSOR:
                        registerInterestRecipe(new InterestEventAssociation(Event.BRIGHTNESS, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventBrightness) {
                                    EventBrightness mLight = (EventBrightness)event;

                                    if(LightSensorTrigger.LIGHT_BELLOW == trigger.getParametersInt(0) &&
                                            mLight.getmBrightness() < trigger.getParametersInt(1)){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }

                                    if(LightSensorTrigger.LIGHT_ABOVE == trigger.getParametersInt(0) &&
                                            mLight.getmBrightness() >= trigger.getParametersInt(1)){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }
                                }
                            }
                        }, 0));

                        break;
                    case TriggerAction.TRIGGER_TEMPERATURE:

                        break;

                    case TriggerAction.TRIGGER_MOTION_SENSOR:

                        registerInterestRecipe(new InterestEventAssociation(Event.OCCUPANT_DETECTED, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventOccupantDetected) {
                                    EventOccupantDetected motion = (EventOccupantDetected)event;
                                    if(motion.isDetected() && trigger.getParametersInt(0) == MotionSensorTrigger.MOVEMENT){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    } else if(!motion.isDetected() && trigger.getParametersInt(0) == MotionSensorTrigger.NO_MOVEMENT
                                            && trigger.getParametersInt(1) <= motion.getSecondsNoMovement()){

                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);

                                    }



                                }
                            }
                        }, 0));

                        break;

                    case TriggerAction.TRIGGER_TIME:



                        List<Long> timesToRun = ((TimeTrigger)trigger).getTimerDate();

                        Log.d("time", "Eventmanager SystemClock.uptimeMillis():" + SystemClock.uptimeMillis());
                        Log.d("time", "Eventmanager TriggerAction.TRIGGER_TIME:" + timesToRun.get(0));

                        for (Long time : timesToRun){
//                            LocalDateTime today = LocalDateTime.now();
                            long timeShift = time - System.currentTimeMillis();
                            Log.d("time", "Eventmanager timeShift:" + timeShift);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                        doAction(recipeFinal);
                                    handler.postDelayed(this, 7*24*60*60*1000);
                                }
                            },timeShift);
                        }

                        break;

//                case Trigger.TRIGGER_TEMPERATURE:
//
//                    registerInterestRecipe(new InterestEventAssociation(Event.TRIGGER_TEMPERATURE, new EventManager.RegisterInterestEvent() {
//                        @Override
//                        public void onRegisteredEventTriggered(Event event) {
//                            if (event instanceof EventTemperature) {
//                                double temp = ((EventTemperature) event).getTemperature();
//
//                                if (recipeFinal.isTriggerConditionBigger()) {
//                                    //Temp ≥ 25
//                                    if (temp >= Integer.parseInt(recipeFinal.getConditionTrigger())) {
//                                        doAction(recipeFinal);
//                                    }
//
//                                }
//                                if (recipeFinal.isTriggerConditionLess()) {
//                                    //Temp ≤ 25
//                                    if (temp <= Integer.parseInt(recipeFinal.getConditionTrigger())) {
//                                        doAction(recipeFinal);
//                                    }
//
//                                }
//                            }
//                        }
//                    }, 0));
//
//                    break;
//                case Trigger.SWITCH:
//                    Log.d("initSavedValues", "SWITCH ");
//
//                    registerInterestRecipe(new InterestEventAssociation(Event.CUSTOM_SWITCH, new EventManager.RegisterInterestEvent() {
//                        @Override
//                        public void onRegisteredEventTriggered(Event event) {
//                            Log.d("initSavedValues", "SWITCH onRegisteredEventTriggered");
//                            if (event instanceof EventCustomSwitchPressed) {
//                                int id = ((EventCustomSwitchPressed) event).getId();
//
//                                //se for o switch pretendido
//                                if (recipeFinal.getSelectedTrigger().get(0) == id) {
//                                    doAction(recipeFinal);
//                                }
//                            }
//                        }
//                    }, 0));
//                    break;
//                case Trigger.TRIGGER_SPEECH:
//                    Log.d("initSavedValues", "TRIGGER_SPEECH ");
//                    registerInterestRecipe(new InterestEventAssociation(Event.TRIGGER_SPEECH, new RegisterInterestEvent() {
//                        @Override
//                        public void onRegisteredEventTriggered(Event event) {
//                            if (event instanceof EventSpeech) {
//                                List<String> voices = ((EventSpeech) event).getVoice();
//
//                                Log.d("voice", "getConditionTriggerValue: " + recipeFinal.getConditionTriggerValue());
//                                //se for o switch pretendido
//                                for (String voice : voices) {
//                                    if (recipeFinal.getConditionTriggerValue().equals(voice)) {
//                                        Log.d("voice", "TRIGGER_SPEECH: correct voice ");
//                                        doAction(recipeFinal);
//                                    }
//                                }
//                            }
//
//                        }
//                    },0));
//
//                    break;
                    case TriggerAction.EMAIL:

                        break;

                }
            }
        }
    }


    public void doAction(Recipe re){
        int lightId;
        for (TriggerAction action : re.getActions()) {
            switch (action.getTriggerActionId()) {

                case TriggerAction.TRIGGER_TEMPERATURE:

                    break;
                case TriggerAction.ACTION_LIGHT_ON:

                    if(action.getParametersInt(0) == LightOnAction.LIGHT_ON){
                        for(int i=1; i< action.getParameters().size(); i++){
                            getBasaManager().getLightingManager().turnONLight(action.getParametersInt(i), true, true);
                        }
                    } else{
                        for(int i=1; i< action.getParameters().size(); i++){
                            getBasaManager().getLightingManager().turnOFFLight(action.getParametersInt(i), true, true);
                        }
                    }


//
//                for(int id: re.getSelectedAction()){
//                    if(getBasaManager().getLightingManager() != null)
//                        getBasaManager().getLightingManager().turnONLight(id, true, true);
//                }

                    break;
                case TriggerAction.ACTION_TALK:
                    String msg = action.getParameters().get(1);
                    getBasaManager().getTextToSpeechManager().speak(msg);
                    break;

                case TriggerAction.ACTION_CHANGE_TEMPERATURE:
                    int value = action.getParametersInt(1);
                    getBasaManager().getTemperatureManager().changeTargetTemperature(value);
                    break;


//            case TriggerAction.LIGHT_OFF:
//                for(int id: re.getSelectedAction()){
//                    if(getBasaManager().getLightingManager() != null)
//                        getBasaManager().getLightingManager().turnOFFLight(id, true, true);
//                }
//                break;

//            case TriggerAction.TRIGGER_SPEECH:
//
//                String say = re.getConditionEventValue();
//                getBasaManager().getTextToSpeechManager().speak(say);
//
//
//                break;
//            case TriggerAction.EMAIL:
//
//                break;
            }
        }
    }


    public void stop(){
        Log.d("EVENT", "EventManager stop ");
        AppController.getInstance().saveHistory(AppController.getInstance().getHistory());
        handler.removeCallbacks(run);
        clearTimerRunnable();
        interests.clear();
        recipes.clear();
    }

    private void clearTimerRunnable(){
        for (Runnable r : timerRunnable)
            handler.removeCallbacks(r);
    }

    public BasaManager getBasaManager() {
        return basaManager;
    }

    public boolean areOtherTriggersActive(List<TriggerAction> triggers, TriggerAction except){
        for(TriggerAction trigger : triggers){
            if(trigger.equals(except))
                continue;
            if(!isTriggerActive(trigger)){
                return false;
            }
        }
        return true;
    }

    public boolean isTriggerActive(TriggerAction trigger){
        switch (trigger.getTriggerActionId()) {
            case TriggerAction.TRIGGER_USER_LOCATION:

                if ( trigger.getParametersInt(0) == LocationTrigger.INSIDE_BUILDING) {
                    return (AppController.getInstance().getBasaManager().getUserManager().numActiveUsersBuilding() > 0);
                }
                if (trigger.getParametersInt(0) == LocationTrigger.EXIT_BUILDING) {
                    return (AppController.getInstance().getBasaManager().getUserManager().numActiveUsersBuilding() == 0);
                }

                if (trigger.getParametersInt(0) == LocationTrigger.INSIDE_OFFICE) {
                    return (AppController.getInstance().getBasaManager().getUserManager().numActiveUsersOffice() > 0);
                }

                if (trigger.getParametersInt(0) == LocationTrigger.EXIT_OFFICE) {
                    return (AppController.getInstance().getBasaManager().getUserManager().numActiveUsersOffice() == 0);
                }

                int numBuilding = AppController.getInstance().getBasaManager().getUserManager().numActiveUsersBuilding();
                if(trigger.getParametersInt(0) == LocationTrigger.NO_USER_IN_BUILDING && numBuilding == 0){
                    return true;
                }

                int numOffice = AppController.getInstance().getBasaManager().getUserManager().numActiveUsersOffice();
                if(trigger.getParametersInt(0) == LocationTrigger.NO_USER_IN_OFFICE && numOffice == 0){
                    return true;
                }


                break;

            case TriggerAction.TRIGGER_LIGHT_SENSOR:

                int brightness = AppController.getInstance().getBasaManager().getBasaSensorManager().getCurrentLightLvl();

                if (LightSensorTrigger.LIGHT_BELLOW == trigger.getParametersInt(0) &&
                        brightness < trigger.getParametersInt(1)) {
                    return true;
                }

                if (LightSensorTrigger.LIGHT_ABOVE == trigger.getParametersInt(0) &&
                        brightness >= trigger.getParametersInt(1)) {
                    return true;
                }

                break;


            case TriggerAction.TRIGGER_MOTION_SENSOR:

                boolean detected = getBasaManager().getBasaSensorManager().isLatestMotionReading();
                int secondsNoMovement = getBasaManager().getBasaSensorManager().getTimeSinceLastMovement();

                if(detected && trigger.getParametersInt(0) == MotionSensorTrigger.MOVEMENT){
                    return true;
                } else if(!detected && trigger.getParametersInt(0) == MotionSensorTrigger.NO_MOVEMENT
                        && trigger.getParametersInt(1) <= secondsNoMovement){
                    return true;
                }

                break;


            case TriggerAction.TRIGGER_TEMPERATURE:

                break;
        }
        return false;
    }

    public EventHistoryFragment.UpdateHistory getUpdateHistory() {
        return updateHistory;
    }

    public void setUpdateHistory(EventHistoryFragment.UpdateHistory updateHistory) {
        this.updateHistory = updateHistory;
    }


    //    public void setUpCalender(){
//
//        Calendar midnightCalendar = Calendar.getInstance();
//
//        //set the time to midnight tonight
//
//        midnightCalendar.set(Calendar.HOUR_OF_DAY, 21);
//
//        midnightCalendar.set(Calendar.MINUTE, 0);
//
//        midnightCalendar.set(Calendar.SECOND, 0);
//
//        AlarmManager am = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
//
//        //create a pending intent to be called at midnight
//
//        PendingIntent midnightPI = PendingIntent.getService(getActivity(), 0, new Intent("pt.ulisboa.tecnico.basa.BroadcastReceiver.TimeBroadcastReceiver"), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //schedule time for pending intent, and set the interval to day so that this event will repeat at the selected time every day
//
//        am.setRepeating(AlarmManager.RTC_WAKEUP, midnightCalendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR, midnightPI);
//
//
//
//
//    }


}
