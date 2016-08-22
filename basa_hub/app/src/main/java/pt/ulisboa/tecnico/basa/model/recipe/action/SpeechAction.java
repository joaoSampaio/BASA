package pt.ulisboa.tecnico.basa.model.recipe.action;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.DialogEditText;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

/**
 * Created by Sampaio on 04/08/2016.
 */
public class SpeechAction extends TriggerAction {

    public final static int SPEECH = 0;

    public SpeechAction(int triggerActionId) {
        super(triggerActionId);


        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put("Say ...", SPEECH);

        super.setAlternatives(alt);

    }


    @Override
    public View.OnClickListener getListener(final Context ctx, final TriggerActionParameterSelected triggerActionParameterSelected) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object choice = v.getTag();
                if(choice != null){
                    final int choiceNum = (choice instanceof Double)? ((Double)choice).intValue() : (int) choice;

                    new DialogEditText(ctx, "Say ...", "text to be said:", new DialogEditText.TextSelected() {
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
    public void setUpCustomView(ViewGroup parent) {

    }

    @Override
    public void destroyCustomView() {

    }



    @Override
    public int getColor() {
        return Color.parseColor("#FFCD30");
    }

    @Override
    public String getParameterTitle() {
        String msg = "Say ";
        if(getParameters().size() > 0){

            int choice = getParametersInt(0);
            String value = getParameters().get(1);

            msg += " \"" + value + "\"";
//            if(getParameters().size() > 1) {
//                msg += "\"";
//                for (int i = 1; i < getParameters().size(); i++) {
//                    msg += " " + (getParametersInt(i) + 1);
//                }
//                msg += "\"";
//            }
        }

        return msg;
    }
}
