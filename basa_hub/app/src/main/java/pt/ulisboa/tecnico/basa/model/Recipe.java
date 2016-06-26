package pt.ulisboa.tecnico.basa.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joaosampaio on 27-03-2016.
 */
public class Recipe {

    private int triggerId;
    private int actionId;
    private String conditionTriggerValue = "";
    private String conditionEventValue = "";
    private String conditionTrigger = "";
    private String conditionEvent = "";
    private List<Integer> selectedTrigger, selectedAction;
    private String shortName;
    private String description;

    public Recipe() {
        triggerId = -1;
        actionId = -1;

    }
    public Recipe(int triggerId, int actionId) {
        this.triggerId = triggerId;
        this.actionId = actionId;
        this.selectedTrigger = new ArrayList<>();
        this.selectedAction = new ArrayList<>();
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

    public List<Integer> getSelectedTrigger() {
        return selectedTrigger;
    }

    public void setSelectedTrigger(List<Integer> selectedTrigger) {
        this.selectedTrigger = selectedTrigger;
    }

    public List<Integer> getSelectedAction() {
        return selectedAction;
    }

    public void setSelectedAction(List<Integer> selectedAction) {
        this.selectedAction = selectedAction;
    }

    public boolean isTriggerConditionBigger(){
        return conditionTrigger.equals("≥");
    }

    public boolean isTriggerConditionLess(){
        return conditionTrigger.equals("≤");
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
