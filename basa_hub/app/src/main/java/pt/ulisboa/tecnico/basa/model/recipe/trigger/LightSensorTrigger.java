package pt.ulisboa.tecnico.basa.model.recipe.trigger;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.DialogEditText;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

/**
 * Created by Sampaio on 03/08/2016.
 */
public class LightSensorTrigger extends TriggerAction {

    public final static int LIGHT = 0;
    public final static String LIGHT_STRING = "Light sensor is";

    public LightSensorTrigger(int triggerId) {
        super(triggerId);

        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put(LIGHT_STRING, LIGHT);
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

                    new DialogEditText(ctx, "Select light level (lx)", "Light level:", new DialogEditText.TextSelected() {
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
    public int  getColor() {
        return Color.parseColor("#ffdb4d");
    }

    @Override
    public String getParameterTitle() {

        String msg = LIGHT_STRING;
        if(getParameters().size() == 2){

            int choice = Integer.parseInt( getParameters().get(0));
            String value = getParameters().get(1);

            msg += " " + value;

        }

        return msg;
    }


}
