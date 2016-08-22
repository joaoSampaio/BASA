package pt.ulisboa.tecnico.basa.model.recipe.trigger;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
public class MotionSensorTrigger extends TriggerAction {

    public final static int MOVEMENT = 0;
    public final static int NO_MOVEMENT = 1;

    public final static String MOVEMENT_STRING = "Movement detected";
    public final static String NO_MOVEMENT_STRING = "No movement detected in X seconds";

    public MotionSensorTrigger() {
        super(TRIGGER_MOTION_SENSOR);

        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put(MOVEMENT_STRING, MOVEMENT);
        alt.put(NO_MOVEMENT_STRING, NO_MOVEMENT);
        super.setAlternatives(alt);
        super.setDescription("The motion sensor is triggered when something changes in the field of view of the assistant.\n" +
                "It can be a person walking in front it, a strong light or a computer screen playing some video");
    }


    @Override
    public View.OnClickListener getListener(final Context ctx, final TriggerActionParameterSelected triggerActionParameterSelected) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object choice = v.getTag();
                if(choice != null){
                    final int choiceNum = (choice instanceof Double)? ((Double)choice).intValue() : (int) choice;

                    if(choiceNum == NO_MOVEMENT) {
                        new DialogEditText(ctx, "Number of seconds with no motion", "seconds:", new DialogEditText.TextSelected() {
                            @Override
                            public void onTextSelected(String text) {

                                List<String> param = new ArrayList<>();
                                param.add(choiceNum + "");
                                param.add(text);
                                triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);

                            }
                        }).show();
                    }else{
                        List<String> param = new ArrayList<>();
                        param.add(choiceNum+"");
                        triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);
                    }
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
        return Color.parseColor("#157EFB");
    }

    @Override
    public String getParameterTitle() {
        String msg = MOVEMENT_STRING;
        if(!getParameters().isEmpty()){

            Log.d("json", "choice:"+Integer.parseInt(getParameters().get(0)));
            int choice = Integer.parseInt( getParameters().get(0));

            if(choice == NO_MOVEMENT)
                msg = "No movement detected in "+ getParameters().get(1) + " seconds" ;


        }

        return msg;
    }

}
