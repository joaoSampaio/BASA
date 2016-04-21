package pt.ulisboa.tecnico.basa.model;


import java.util.List;

public class EventVoice extends Event {

    private List<String> voice;


    public EventVoice(List<String> voice) {
        super(Event.VOICE);
        this.voice = voice;
    }

    public List<String> getVoice() {
        return voice;
    }

}
