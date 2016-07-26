package pt.ulisboa.tecnico.basa.manager;

import android.util.Log;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.model.Trigger;
import pt.ulisboa.tecnico.basa.model.TriggerAction;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventClap;
import pt.ulisboa.tecnico.basa.model.event.EventCustomSwitchPressed;
import pt.ulisboa.tecnico.basa.model.event.EventOccupantDetected;
import pt.ulisboa.tecnico.basa.model.event.EventTemperature;
import pt.ulisboa.tecnico.basa.model.event.EventVoice;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class EventManager {

    private BasaManager basaManager;
    private List<InterestEventAssociation> interests;
    private List<InterestEventAssociation> recipes;
    public EventManager(BasaManager basaManager) {
        interests = new ArrayList<>();
        recipes = new ArrayList<>();
        this.basaManager = basaManager;
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
        Log.d("EVENT", " register interests.size():" + interests.size() );

        this.interests.add(interest);
        Log.d("EVENT", " register interest:" + interest.getType() );
    }

    public void removeInterest(InterestEventAssociation interest){
        this.interests.remove(interest);
        Log.d("EVENT", " removeInterest :" + interest.getType() );
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
        List<Recipe> recipes = new ModelCache<List<Recipe>>().loadModel(new TypeToken<List<Recipe>>(){}.getType(), Global.OFFLINE_RECIPES);
        if(recipes == null)
            recipes = new ArrayList<>();


        for(Recipe re: recipes) {
            final Recipe recipeFinal = re;
            switch (re.getTriggerId()) {
                case Trigger.CLAP:
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
                case Trigger.TEMPERATURE:

                    registerInterestRecipe(new InterestEventAssociation(Event.TEMPERATURE, new EventManager.RegisterInterestEvent() {
                        @Override
                        public void onRegisteredEventTriggered(Event event) {
                            if (event instanceof EventTemperature) {
                                double temp = ((EventTemperature) event).getTemperature();

                                if (recipeFinal.isTriggerConditionBigger()) {
                                    //Temp ≥ 25
                                    if (temp >= Integer.parseInt(recipeFinal.getConditionTrigger())) {
                                        doAction(recipeFinal);
                                    }

                                }
                                if (recipeFinal.isTriggerConditionLess()) {
                                    //Temp ≤ 25
                                    if (temp <= Integer.parseInt(recipeFinal.getConditionTrigger())) {
                                        doAction(recipeFinal);
                                    }

                                }
                            }
                        }
                    }, 0));

                    break;
                case Trigger.SWITCH:
                    Log.d("initSavedValues", "SWITCH ");

                    registerInterestRecipe(new InterestEventAssociation(Event.CUSTOM_SWITCH, new EventManager.RegisterInterestEvent() {
                        @Override
                        public void onRegisteredEventTriggered(Event event) {
                            Log.d("initSavedValues", "SWITCH onRegisteredEventTriggered");
                            if (event instanceof EventCustomSwitchPressed) {
                                int id = ((EventCustomSwitchPressed) event).getId();

                                //se for o switch pretendido
                                if (recipeFinal.getSelectedTrigger().get(0) == id) {
                                    doAction(recipeFinal);
                                }
                            }
                        }
                    }, 0));
                    break;
                case Trigger.VOICE:
                    Log.d("initSavedValues", "VOICE ");
                    registerInterestRecipe(new InterestEventAssociation(Event.VOICE, new RegisterInterestEvent() {
                        @Override
                        public void onRegisteredEventTriggered(Event event) {
                            if (event instanceof EventVoice) {
                                List<String> voices = ((EventVoice) event).getVoice();

                                Log.d("voice", "getConditionTriggerValue: " + recipeFinal.getConditionTriggerValue());
                                //se for o switch pretendido
                                for (String voice : voices) {
                                    if (recipeFinal.getConditionTriggerValue().equals(voice)) {
                                        Log.d("voice", "VOICE: correct voice ");
                                        doAction(recipeFinal);
                                    }
                                }
                            }

                        }
                    },0));

                    break;
                case Trigger.EMAIL:

                    break;

            }
        }
    }


    public void doAction(Recipe re){
        int lightId;
        switch (re.getActionId()){

            case TriggerAction.TEMPERATURE:

                break;
            case TriggerAction.LIGHT_ON:


                for(int id: re.getSelectedAction()){
                    if(getBasaManager().getLightingManager() != null)
                        getBasaManager().getLightingManager().turnONLight(id, true, true);
                }

                break;
            case TriggerAction.LIGHT_OFF:
                for(int id: re.getSelectedAction()){
                    if(getBasaManager().getLightingManager() != null)
                        getBasaManager().getLightingManager().turnOFFLight(id, true, true);
                }
                break;

            case TriggerAction.VOICE:

                String say = re.getConditionEventValue();
                getBasaManager().getTextToSpeechManager().speak(say);


                break;
            case TriggerAction.EMAIL:

                break;
        }
    }


    public void stop(){
        Log.d("EVENT", "EventManager stop ");
        interests.clear();
        recipes.clear();
        interests = null;
        basaManager = null;
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
