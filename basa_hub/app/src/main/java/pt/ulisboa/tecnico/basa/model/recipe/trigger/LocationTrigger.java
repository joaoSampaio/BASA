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
public class LocationTrigger extends TriggerAction {

    public final static int INSIDE_OFFICE = 0;
    public final static int INSIDE_BUILDING = 1;
    public final static int EXIT_OFFICE = 2;
    public final static int EXIT_BUILDING = 3;
    public final static int ARRIVES_OFFICE = 4;
    public final static int ARRIVES_BUILDING = 5;

    public final static String INSIDE_OFFICE_STRING = "User is inside office";
    public final static String INSIDE_BUILDING_STRING = "User is inside building";
    public final static String EXIT_OFFICE_STRING = "User leaves office";
    public final static String EXIT_BUILDING_STRING = "User leaves building";
    public final static String ARRIVES_OFFICE_STRING = "User arrives at office";
    public final static String ARRIVES_BUILDING_STRING = "User arrives at building";
    //////adicionar escolha de qual o utilizador se todos, se um especifico

    public LocationTrigger(int triggerId) {
        super(triggerId);

        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put(ARRIVES_OFFICE_STRING, ARRIVES_OFFICE);
        alt.put(ARRIVES_BUILDING_STRING, ARRIVES_BUILDING);

        alt.put(INSIDE_OFFICE_STRING, INSIDE_OFFICE);
        alt.put(INSIDE_BUILDING_STRING, INSIDE_BUILDING);
        alt.put(EXIT_OFFICE_STRING, EXIT_OFFICE);
        alt.put(EXIT_BUILDING_STRING, EXIT_BUILDING);
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
        return Color.parseColor("#000000");
    }

    @Override
    public String getParameterTitle() {
        String msg = "";
        if(!getParameters().isEmpty()){

            Log.d("json", "choice:"+Integer.parseInt(getParameters().get(0)));
            int choice = Integer.parseInt( getParameters().get(0));
//            String value = (String)getParameters().get(1);

            switch (choice){
                case INSIDE_OFFICE:
                    msg = INSIDE_OFFICE_STRING;
                    break;
                case INSIDE_BUILDING:
                    msg = INSIDE_BUILDING_STRING;
                    break;
                case EXIT_OFFICE:
                    msg = EXIT_OFFICE_STRING;
                    break;
                case EXIT_BUILDING:
                    msg = EXIT_BUILDING_STRING;
                    break;
                case ARRIVES_OFFICE:
                    msg = ARRIVES_OFFICE_STRING;
                    break;
                case ARRIVES_BUILDING:
                    msg = ARRIVES_BUILDING_STRING;
                    break;
            }
        }

        return msg;
    }

}
