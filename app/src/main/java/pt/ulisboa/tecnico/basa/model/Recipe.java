package pt.ulisboa.tecnico.basa.model;

import java.util.List;

/**
 * Created by joaosampaio on 27-03-2016.
 */
public class Recipe {

    private int triggerId;
    private int actionId;
    private String conditionTriggerValue;
    private String conditionEventValue;
    private String conditionTrigger;
    private String conditionEvent;
    private List<Integer> selectedMulti;

    public Recipe(int triggerId, int actionId) {
        this.triggerId = triggerId;
        this.actionId = actionId;
    }

    public Recipe(int triggerId, int actionId, String conditionTriggerValue, String conditionEventValue) {
        this.triggerId = triggerId;
        this.actionId = actionId;
        this.conditionTriggerValue = conditionTriggerValue;
        this.conditionEventValue = conditionEventValue;
    }

    public Recipe(int triggerId, int actionId, String conditionTriggerValue, String conditionEventValue, String conditionTrigger, String conditionEvent) {
        this.triggerId = triggerId;
        this.actionId = actionId;
        this.conditionTriggerValue = conditionTriggerValue;
        this.conditionEventValue = conditionEventValue;
        this.conditionTrigger = conditionTrigger;
        this.conditionEvent = conditionEvent;
    }

    public int getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(int triggerId) {
        this.triggerId = triggerId;
    }

    public int getActionId() {
        return actionId;
    }

    public void setActionId(int actionId) {
        this.actionId = actionId;
    }

    public String getConditionTriggerValue() {
        return conditionTriggerValue;
    }

    public void setConditionTriggerValue(String conditionTriggerValue) {
        this.conditionTriggerValue = conditionTriggerValue;
    }

    public String getConditionEventValue() {
        return conditionEventValue;
    }

    public void setConditionEventValue(String conditionEventValue) {
        this.conditionEventValue = conditionEventValue;
    }

    public String getConditionTrigger() {
        return conditionTrigger;
    }

    public void setConditionTrigger(String conditionTrigger) {
        this.conditionTrigger = conditionTrigger;
    }

    public String getConditionEvent() {
        return conditionEvent;
    }

    public void setConditionEvent(String conditionEvent) {
        this.conditionEvent = conditionEvent;
    }

    public List<Integer> getSelectedMulti() {
        return selectedMulti;
    }

    public void setSelectedMulti(List<Integer> selectedMulti) {
        this.selectedMulti = selectedMulti;
    }

    public boolean isTriggerConditionBigger(){
        return conditionTrigger.equals("≥");
    }

    public boolean isTriggerConditionLess(){
        return conditionTrigger.equals("≤");
    }
}
