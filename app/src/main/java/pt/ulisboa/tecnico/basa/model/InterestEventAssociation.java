package pt.ulisboa.tecnico.basa.model;

import pt.ulisboa.tecnico.basa.manager.EventManager;

/**
 * Created by joaosampaio on 21-03-2016.
 */
public class InterestEventAssociation {
    private int tag;
    private int type;
    private EventManager.RegisterInterestEvent interest;

    public InterestEventAssociation(int type, EventManager.RegisterInterestEvent interest, int tag) {
        this.type = type;
        this.interest = interest;
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public EventManager.RegisterInterestEvent getInterest() {
        return interest;
    }

    public void setInterest(EventManager.RegisterInterestEvent interest) {
        this.interest = interest;
    }

    public boolean isType(int type){
        return this.type == type;
    }

    public boolean isTag(int tag){
        return this.tag == tag;
    }


}
