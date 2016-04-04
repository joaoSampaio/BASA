package pt.ulisboa.tecnico.basa.manager;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.Event;
import pt.ulisboa.tecnico.basa.model.EventCustomSwitchPressed;
import pt.ulisboa.tecnico.basa.model.EventOccupantDetected;
import pt.ulisboa.tecnico.basa.model.EventTemperature;
import pt.ulisboa.tecnico.basa.model.EventVoice;
import pt.ulisboa.tecnico.basa.model.InterestEventAssociation;

public class EventManager {

private List<InterestEventAssociation> interests;

    public EventManager() {
        interests = new ArrayList<>();
    }

    public void addEvent(Event event){
        Log.d("EVENT", "****"+eventToString(event)+"****: ");
        for (InterestEventAssociation interest: interests){
            if(interest.isType(event.getType())){
                interest.getInterest().onRegisteredEventTriggered(event);
            }
        }
    }

    public void registerInterest(InterestEventAssociation interest){
        this.interests.add(interest);
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
}
