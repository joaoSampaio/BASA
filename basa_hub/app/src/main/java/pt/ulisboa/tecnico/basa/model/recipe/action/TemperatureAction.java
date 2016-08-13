package pt.ulisboa.tecnico.basa.model.recipe.action;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.DialogEditText;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

/**
 * Created by Sampaio on 03/08/2016.
 */
public class TemperatureAction extends TriggerAction {

    public final static int TEMPERATURE_ABOVE = 0;
    public final static int TEMPERATURE_BELOW = 1;


    public TemperatureAction(int triggerId) {
        super(triggerId);

        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put("Set temperature above", TEMPERATURE_ABOVE);
        alt.put("Set temperature below", TEMPERATURE_BELOW);
        super.setAlternatives(alt);
    }

    @Override
    public View.OnClickListener getListener(final Context ctx, final TriggerActionParameterSelected triggerActionParameterSelected) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object choice = v.getTag();
                if(choice != null){
                    final int choiceNum = (int) choice;

                    new DialogEditText(ctx, "Set temperature", "temperature:", new DialogEditText.TextSelected() {
                        @Override
                        public void onTextSelected(String text) {

                            List<String> param = new ArrayList<>();
                            param.add(choiceNum+"");
                            param.add(text);
                            triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);

                        }
                    }).show();


                    //getParameters().add(choiceNum);

                }
            }
        };
    }

    @Override
    public void setUpCustomView(ViewGroup parent) {

    }

    @Override
    public void destroyCustomView() {

    }

    @Override
    public int  getColor() {
        return Color.parseColor("#ff33b5e5");
    }

    @Override
    public String getParameterTitle() {

        String msg = "Set temperature above ";
        if(getParameters().size() == 2){

            int choice = Integer.parseInt( getParameters().get(0));
            String value = getParameters().get(1);

            if(choice == TEMPERATURE_BELOW)
                msg = "Set temperature below ";
            msg += value;

        }

        return msg;
    }


}
