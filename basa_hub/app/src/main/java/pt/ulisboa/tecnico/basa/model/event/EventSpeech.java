package pt.ulisboa.tecnico.basa.model.event;


import java.util.List;

public class EventSpeech extends Event {

    private List<String> voice;


    public EventSpeech(List<String> voice) {
        super(SPEECH);
        this.voice = voice;
    }

    public List<String> getVoice() {
        return voice;
    }

}
