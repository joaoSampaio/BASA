package pt.ulisboa.tecnico.basa.model.recipe.trigger;

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
public class LightStateTrigger extends TriggerAction {

    public final static int LIGHT_STATE = 0;

    public LightStateTrigger() {
        super(TRIGGER_LIGHT_STATE);


        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put("Select light state", LIGHT_STATE);


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

                    DialogMultiSelect dialog = new DialogMultiSelect(ctx,  valuesList, checkedValues, "Lights are ON/OFF", new DialogMultiSelect.DialogMultiSelectResponse() {
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
                    });
                    dialog.setAllowEmpty(true);
                    dialog.show();
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


            if(getParameters().size() > 1) {
                msg += "Lights (";
                for (int on : lightsOn()) {
                    msg += " " + (on + 1);
                }
                msg += " ) are ON";
            }

            List<Integer> missing = lightsOff();
            if(!missing.isEmpty()){
                if(!msg.isEmpty()){
                    msg +=  " & ";
                }
                msg += "Lights (";
                for (int m : missing) {
                    msg += " " + (m + 1);
                }
                msg += " ) are OFF";


            }
        }

        return msg;
    }

    public List<Integer> lightsOn() {
        List<Integer> on = new ArrayList<>();
        for (int i = 1; i <= (getParameters().size() - 1); i++) {
            on.add (getParametersInt(i));
        }

        return on;
    }

    public List<Integer> lightsOff() {

         List<Integer> missing = new ArrayList<>();

        int[] a = new int[getParameters().size()-1];
         for (int i = 1; i <= (getParameters().size()-1); i++) {
             a[i-1] = getParametersInt(i);
         }

        if(getParameters().size()-1 == 0){
            for (int i = 0; i <= AppController.getInstance().getDeviceConfig().getEdupNumLight()-1; i++) {
                missing.add(i);
            }
            return missing;
        }

//        if(getParameters().size() == 1){
//            missing.add(0);
//        }

        // before the array: numbers between first and a[0]-1
        for (int i = 0; i < a[0]; i++) {
            missing.add(i);
        }
        // inside the array: at index i, a number is missing if it is between a[i-1]+1 and a[i]-1
        for (int i = 1; i < a.length; i++) {
            for (int j = 1+a[i-1]; j < a[i]; j++) {
                missing.add(j);
            }
        }
        // after the array: numbers between a[a.length-1] and last
        for (int i = 1+a[a.length-1]; i <= AppController.getInstance().getDeviceConfig().getEdupNumLight()-1; i++) {
            missing.add(i);
        }

        return missing;
    }

}
