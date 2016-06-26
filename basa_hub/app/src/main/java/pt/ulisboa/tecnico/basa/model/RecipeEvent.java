package pt.ulisboa.tecnico.basa.model;

import pt.ulisboa.tecnico.basa.R;

public class RecipeEvent {


    private int eventId;
    private String title;
    private int resId;

    public RecipeEvent(int eventId, String title, int resId) {
        this.eventId = eventId;
        this.title = title;
        this.resId = resId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int triggerId) {
        this.eventId = triggerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }



}


