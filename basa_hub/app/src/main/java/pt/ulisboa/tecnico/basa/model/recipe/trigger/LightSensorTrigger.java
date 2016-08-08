package pt.ulisboa.tecnico.basa.model.recipe.trigger;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.DialogEditText;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;

public class LightSensorTrigger extends TriggerAction {

    public final static int LIGHT_ABOVE = 0;
    public final static int LIGHT_BELLOW= 1;
    public final static String LIGHT_ABOVE_STRING = "Light sensor above or equal";
    public final static String LIGHT_BELLOW_STRING = "Light sensor bellow";
    private transient Handler handler;
    private transient Runnable run;

    public LightSensorTrigger(int triggerId) {
        super(triggerId);

        LinkedHashMap<String, Object> alt = new LinkedHashMap<>();
        alt.put(LIGHT_ABOVE_STRING, LIGHT_ABOVE);
        alt.put(LIGHT_BELLOW_STRING, LIGHT_BELLOW);
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
                }
            }
        };
    }


    public void setUpCustomView(ViewGroup parent){

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_brightness, parent, false);

        final TextView textViewLux = (TextView)v.findViewById(R.id.textViewLux);
        parent.addView(v);
        handler = new Handler();
        run = new Runnable() {
            @Override
            public void run() {
                textViewLux.setText("Current Lux: " + AppController.getInstance().getBasaManager().getBasaSensorManager().getCurrentLightLvl());
                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(run, 1000);

    }

    public void destroyCustomView(){
        if(handler != null){
            handler.removeCallbacks(run);
        }
    }


    @Override
    public int  getColor() {
        return Color.parseColor("#ffdb4d");
    }

    @Override
    public String getParameterTitle() {

        String msg = LIGHT_ABOVE_STRING;
        if(getParameters().size() == 2){

            int choice = Integer.parseInt( getParameters().get(0));
            if(choice == LIGHT_BELLOW)
                msg = LIGHT_BELLOW_STRING;
            String value = getParameters().get(1);

            msg += " " + value;

        }

        return msg;
    }


}
