package pt.ulisboa.tecnico.basa.model;



public class EventVoice extends Event {

    private String voice;


    public EventVoice(String voice) {
        super(Event.VOICE);
        this.voice = voice;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }
}
