package pt.ulisboa.tecnico.basa.manager;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventClap;
import pt.ulisboa.tecnico.basa.model.event.EventCustomSwitchPressed;
import pt.ulisboa.tecnico.basa.model.event.EventOccupantDetected;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.model.event.EventUserLocation;
import pt.ulisboa.tecnico.basa.model.event.EventVoice;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.LightOnAction;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LocationTrigger;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class EventManager {

    private BasaManager basaManager;
    private List<InterestEventAssociation> interests;
    private List<InterestEventAssociation> recipes;
    private Handler handler;
    private final static int PERIOD = 2000;
    private Runnable run;

    public EventManager(BasaManager basaManager) {
        interests = new ArrayList<>();
        recipes = new ArrayList<>();

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
            int i = 0;
            for (InterestEventAssociation interest: interests){
                if(interest.isType(event.getType())){
                    interest.getInterest().onRegisteredEventTriggered(event);
                    i++;
                    Log.d("EVENT", "****i= " + i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            int i = 0;
            for (InterestEventAssociation interest: recipes){
                if(interest.isType(event.getType())){
                    interest.getInterest().onRegisteredEventTriggered(event);
                    i++;
                    Log.d("EVENT", "****i= " + i);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                result = "TEMPERATURE->" + ((EventTemperature)event).getTemperature();
                break;
            case Event.VOICE:
                result = "VOICE->" + ((EventVoice)event).getVoice();
                break;
            case Event.CUSTOM_SWITCH:
                result = "CUSTOM_SWITCH Pressed->" + ((EventCustomSwitchPressed)event).getId();
                break;
            case Event.CLAP:
                result = "CLAP ->";
                break;
        }

        return result;
    }

    public void reloadSavedRecipes(){
        recipes.clear();
        initSavedRecipes();
    }


    private void initSavedRecipes(){
        List<Recipe> recipes = new ModelCache<List<Recipe>>().loadRecipes();
        if(recipes == null)
            recipes = new ArrayList<>();


        for(Recipe re: recipes) {
            final Recipe recipeFinal = re;
            for (final TriggerAction trigger : re.getTriggers()) {
                switch (trigger.getTriggerActionId()) {
                    case TriggerAction.USER_LOCATION:
                        registerInterestRecipe(new InterestEventAssociation(Event.USER_LOCATION, new EventManager.RegisterInterestEvent() {
                            @Override
                            public void onRegisteredEventTriggered(Event event) {
                                if (event instanceof EventUserLocation) {
                                    EventUserLocation location = (EventUserLocation)event;

                                    if(location.getLocation() == EventUserLocation.TYPE_BUILDING && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.INSIDE_BUILDING){
                                        doAction(recipeFinal);
                                    }
                                    if(location.getLocation() == EventUserLocation.TYPE_BUILDING && !location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.EXIT_BUILDING){
                                        doAction(recipeFinal);
                                    }

                                    if(location.getLocation() == EventUserLocation.TYPE_OFFICE && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.INSIDE_OFFICE){
                                        doAction(recipeFinal);
                                    }

                                    if(location.getLocation() == EventUserLocation.TYPE_OFFICE && !location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.EXIT_OFFICE){
                                        doAction(recipeFinal);
                                    }

                                    if(location.getLocation() == EventUserLocation.TYPE_OFFICE && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.ARRIVES_OFFICE){
                                        doAction(recipeFinal);
                                    }

                                    if(location.getLocation() == EventUserLocation.TYPE_BUILDING && location.isInBuilding()
                                            && trigger.getParametersInt(0) == LocationTrigger.ARRIVES_BUILDING){
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
                                Log.d("initSavedValues", "SWITCH onRegisteredEventTriggered");
                                if (event instanceof EventClap) {

                                    doAction(recipeFinal);


                                }
                            }
                        }, 0));
                        break;
//                case Trigger.TEMPERATURE:
//
//                    registerInterestRecipe(new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
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
//                case Trigger.VOICE:
//                    Log.d("initSavedValues", "VOICE ");
//                    registerInterestRecipe(new InterestEventAssociation(Event.VOICE, new RegisterInterestEvent() {
//                        @Override
//                        public void onRegisteredEventTriggered(Event event) {
//                            if (event instanceof EventVoice) {
//                                List<String> voices = ((EventVoice) event).getVoice();
//
//                                Log.d("voice", "getConditionTriggerValue: " + recipeFinal.getConditionTriggerValue());
//                                //se for o switch pretendido
//                                for (String voice : voices) {
//                                    if (recipeFinal.getConditionTriggerValue().equals(voice)) {
//                                        Log.d("voice", "VOICE: correct voice ");
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

                case TriggerAction.TEMPERATURE:

                    break;
                case TriggerAction.LIGHT_ON:

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
//            case TriggerAction.LIGHT_OFF:
//                for(int id: re.getSelectedAction()){
//                    if(getBasaManager().getLightingManager() != null)
//                        getBasaManager().getLightingManager().turnOFFLight(id, true, true);
//                }
//                break;

//            case TriggerAction.VOICE:
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
        handler.removeCallbacks(run);
        interests.clear();
        recipes.clear();
    }

    public BasaManager getBasaManager() {
        return basaManager;
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
