package pt.ulisboa.tecnico.basa.model.recipe.action;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.DialogMultiSelect;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

/**
 * Created by Sampaio on 04/08/2016.
 */
public class LightOnAction extends TriggerAction {

    public final static int LIGHT_ON = 0;
    public final static int LIGHT_OFF = 1;

    public LightOnAction(int triggerActionId) {
        super(triggerActionId);


        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put("Turn on light", LIGHT_ON);
        alt.put("Turn off light", LIGHT_OFF);

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

                    String[] valuesList = getLights();
                    boolean[] checkedValues = getLightsSelected();

                    new DialogMultiSelect(ctx,  valuesList, checkedValues, (choiceNum == LIGHT_ON)? "Turn on light" : "Turn off light", new DialogMultiSelect.DialogMultiSelectResponse() {
                        @Override
                        public void onSucess(boolean[] checkedValues) {
                            List<String> selectedMulti = new ArrayList<String>();
                            for(int i = 0; i<  checkedValues.length; i++){
                                if(checkedValues[i])
                                    selectedMulti.add(i+"");
                            }

                            List<String> param = new ArrayList<>();
                            param.add(choiceNum+"");
                            param.addAll(selectedMulti);
                            triggerActionParameterSelected.onTriggerOrActionParameterSelected(param);


                        }
                    }).show();


//                    new DialogEditText.TextSelected() {
//                        @Override
//                        public void onTextSelected(String text) {
//
//
//
//                        }
//                    }).show();



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

    public String[] getLights(){

        int numLights = AppController.getInstance().getDeviceConfig().getEdupNumLight();
        List<String> result = new ArrayList<>();
        for(int i = 1; i<=numLights; i++){
            result.add("Light "+i);
        }
        String[] array = new String[result.size()];
        return result.toArray(array);
    }

    public boolean[] getLightsSelected(){
        int numLights = AppController.getInstance().getDeviceConfig().getEdupNumLight();
        boolean[] result = new boolean[numLights];
        for(int i = 0; i < numLights; i++){
            result[i] = false;
        }
        return result;
    }

    @Override
    public int getColor() {
        return Color.parseColor("#FFCD30");
    }

    @Override
    public String getParameterTitle() {
        String msg = "";
        if(getParameters().size() > 0){

            int choice = getParametersInt(0);
            if(choice == LIGHT_ON)
                msg = "Turn on light ";
            else
                msg = "Turn off light ";
            if(getParameters().size() > 1) {
                msg += "(";
                for (int i = 1; i < getParameters().size(); i++) {
                    msg += " " + (getParametersInt(i) + 1);
                }
                msg += " )";
            }
        }

        return msg;
    }
}
