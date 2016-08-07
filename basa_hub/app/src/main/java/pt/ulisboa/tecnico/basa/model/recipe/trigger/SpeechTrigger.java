package pt.ulisboa.tecnico.basa.model.recipe.trigger;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.DialogEditText;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

/**
 * Created by Sampaio on 03/08/2016.
 */
public class SpeechTrigger extends TriggerAction {

    public final static int SPEECH_FULL = 0;
    public final static String SPEECH_FULL_STRING = "Voice command is";

    public SpeechTrigger(int triggerId) {
        super(triggerId);

        Map<String, Object> alt = new HashMap<>();
        alt.put(SPEECH_FULL_STRING, SPEECH_FULL);
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

                    new DialogEditText(ctx, "Voice command", "command:", new DialogEditText.TextSelected() {
                        @Override
                        public void onTextSelected(String text) {

                            List<String> param = new ArrayList<>();
                            param.add(choiceNum+"");
                            param.add(text);
                            triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);

                        }
                    }, InputType.TYPE_CLASS_TEXT).show();
                }
            }
        };
    }

    @Override
    public int  getColor() {
        return Color.parseColor("#FF00D118");
    }

    @Override
    public String getParameterTitle() {

        String msg = SPEECH_FULL_STRING;
        if(getParameters().size() == 2){

            int choice = Integer.parseInt( getParameters().get(0));
            String value = getParameters().get(1);

            msg += " " + value;

        }

        return msg;
    }


}
