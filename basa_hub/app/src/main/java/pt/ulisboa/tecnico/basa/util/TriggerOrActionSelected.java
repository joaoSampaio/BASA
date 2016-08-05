package pt.ulisboa.tecnico.basa.util;

import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;

/**
 * Created by Sampaio on 03/08/2016.
 */
public interface TriggerOrActionSelected {

    void onTriggerSelected(TriggerAction trigger);

    void onActionSelected(TriggerAction action);
}
