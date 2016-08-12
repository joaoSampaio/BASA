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
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

/**
 * Created by Sampaio on 03/08/2016.
 */
public class MotionSensorTrigger extends TriggerAction {

    public final static int MOVEMENT = 0;


    public final static String MOVEMENT_STRING = "Movement is detected";


    public MotionSensorTrigger(int triggerId) {
        super(triggerId);

        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put(MOVEMENT_STRING, MOVEMENT);

        super.setAlternatives(alt);
    }


    @Override
    public View.OnClickListener getListener(Context ctx, final TriggerActionParameterSelected triggerActionParameterSelected) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object choice = v.getTag();
                if(choice != null){
                    int choiceNum = (int) choice;
                    List<String> param = new ArrayList<>();
                    param.add(choiceNum+"");
                    triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);

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
//            String value = (String)getParameters().get(1);

        }

        return msg;
    }

}
