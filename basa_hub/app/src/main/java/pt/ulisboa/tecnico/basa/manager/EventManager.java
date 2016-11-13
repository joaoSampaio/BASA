package pt.ulisboa.tecnico.basa.manager;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.EventHistory;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventBrightness;
import pt.ulisboa.tecnico.basa.model.event.EventChangeTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventCustomSwitchPressed;
import pt.ulisboa.tecnico.basa.model.event.EventLightSwitch;
import pt.ulisboa.tecnico.basa.model.event.EventMotion;
import pt.ulisboa.tecnico.basa.model.event.EventSpeech;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.event.EventUserLocation;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.LightOnAction;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LightSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LightStateTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LocationTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.MotionSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TemperatureTrigger;
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
        //Log.d("EVENT", "****" + eventToString(event) + "****: ");

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
            case Event.MOTION:
                if(((EventMotion)event).isDetected())
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
            case Event.TEMPERATURE:
                if (event instanceof EventTemperature) {
                    AppController.getInstance().getStatisticalData().addTemperatureEvent(((EventTemperature) event));
                }
                break;

            case Event.LIGHT:
                result = "Light (" + ((EventLightSwitch)event).getLightNum() + ") is " + (((EventLightSwitch)event).isOn()? "on" : "off");
                AppController.getInstance().getStatisticalData().addLightsEvent(((EventLightSwitch)event));

                break;
            case Event.BRIGHTNESS:
                AppController.getInstance().getStatisticalData().addLightLvlEvent(((EventBrightness) event));

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

            Log.d("EVENT", "registerInterest******************************************(interest != null): " + (interest != null));
            if(interest == null)
                throw new RuntimeException();


            this.interests.add(interest);
        }catch (Exception e){
            e.printStackTrace();
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
        String result = "unknown:" + event.getType();
        switch (event.getType()){
            case Event.MOTION:
                result = "MOTION->" + ((EventMotion)event).isDetected();
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
            case Event.TIME:
                result = "TIME -> ";
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

                        registerInterestRecipe(new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventTemperature) {
                                    EventTemperature temperature = (EventTemperature)event;

                                    if(TemperatureTrigger.TEMPERATURE_DROPS == trigger.getParametersInt(0)
                                            && temperature.getTemperature() < trigger.getParametersInt(1)){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }

                                    if(TemperatureTrigger.TEMPERATURE_RISES == trigger.getParametersInt(0)
                                            && temperature.getTemperature() > trigger.getParametersInt(1)){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);

                                    }


                                }
                            }
                        }, 0));

                        break;

                    case TriggerAction.TRIGGER_MOTION_SENSOR:

                        registerInterestRecipe(new InterestEventAssociation(Event.MOTION, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventMotion) {
                                    EventMotion motion = (EventMotion)event;
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

                    case TriggerAction.TRIGGER_LIGHT_STATE:


                        registerInterestRecipe(new InterestEventAssociation(Event.LIGHT, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventLightSwitch) {

                                    LightStateTrigger lightStateTrigger = (LightStateTrigger)trigger;
                                    List<Integer> lightsOff = lightStateTrigger.lightsOff();
                                    List<Integer> lightsOn = lightStateTrigger.lightsOn();

                                    boolean[] lights = getBasaManager().getLightingManager().getLights();
                                    boolean allLightsCorrect = true;
                                    for(int on : lightsOn)
                                        if(!lights[on])
                                            allLightsCorrect = false;

                                    for(int off : lightsOff)
                                        if(lights[off])
                                            allLightsCorrect = false;

                                    if(allLightsCorrect){
                                        if(areOtherTriggersActive(recipeFinal.getTriggers(), trigger))
                                            doAction(recipeFinal);
                                    }
                                }
                            }
                        }, 0));




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
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AppController.getAppContext());
                    int numLights = Integer.parseInt(preferences.getString("light_number", "1"));
                    boolean[] lights = new boolean[numLights];

                    if(action.getParametersInt(0) == LightOnAction.LIGHT_ON){
                        for(int i=1; i< action.getParameters().size(); i++){
                            lights[action.getParametersInt(i)] = true;
                        }
                    }


                    getBasaManager().getLightingManager().setLightState(lights, true, true, true);


                    break;
                case TriggerAction.ACTION_TALK:
                    String msg = action.getParameters().get(1);
                    getBasaManager().getTextToSpeechManager().speak(msg);
                    break;

                case TriggerAction.ACTION_CHANGE_TEMPERATURE:
                    int value = action.getParametersInt(1);
                    getBasaManager().getTemperatureManager().changeTargetTemperatureFromUI(value);
                    break;

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

                int brightness = getBasaManager().getBasaSensorManager().getCurrentLightLvl();

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


            case TriggerAction.TRIGGER_LIGHT_STATE:


                LightStateTrigger lightStateTrigger = (LightStateTrigger)trigger;
                List<Integer> lightsOff = lightStateTrigger.lightsOff();
                List<Integer> lightsOn = lightStateTrigger.lightsOn();

                boolean[] lights = getBasaManager().getLightingManager().getLights();
                for(int on : lightsOn)
                    if(!lights[on])
                        return false;

                for(int off : lightsOff)
                    if(lights[off])
                        return false;

                return true;


            case TriggerAction.TRIGGER_TEMPERATURE:
                if(TemperatureTrigger.TEMPERATURE_DROPS == trigger.getParametersInt(0)
                        && getBasaManager().getTemperatureManager().getCurrentTemperature() < trigger.getParametersInt(1)){
                    return true;
                }

                if(TemperatureTrigger.TEMPERATURE_RISES == trigger.getParametersInt(0)
                        && getBasaManager().getTemperatureManager().getCurrentTemperature() > trigger.getParametersInt(1)){
                    return true;

                }
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


}
