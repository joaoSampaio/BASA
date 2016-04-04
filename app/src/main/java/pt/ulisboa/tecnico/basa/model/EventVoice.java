package pt.ulisboa.tecnico.basa.model;



public class EventVoice extends Event {

    private String voice;


    public EventVoice(int type, String voice) {
        super(type);
        this.voice = voice;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }
}
