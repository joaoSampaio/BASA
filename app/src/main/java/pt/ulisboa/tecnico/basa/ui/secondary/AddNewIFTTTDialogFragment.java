package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.model.Trigger;
import pt.ulisboa.tecnico.basa.model.TriggerAction;
import pt.ulisboa.tecnico.basa.ui.MainActivity;
import pt.ulisboa.tecnico.basa.util.ModelCache;


public class AddNewIFTTTDialogFragment extends DialogFragment implements View.OnClickListener {

    private View rootView;
    private ImageView action_trigger, action_event;
    private TextView action_condition, action_condition_value, action_event_condition, action_event_condition_value;
    private Spinner condition_spinner;
    private Button action_save_recipe;
    private IFTTTDialogFragment.NewRecipeCreated listener;

    Recipe recipe;

    public AddNewIFTTTDialogFragment() {
        // Required empty public constructor
    }



    public static AddNewIFTTTDialogFragment newInstance() {
        AddNewIFTTTDialogFragment fragment = new AddNewIFTTTDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ifttt_add_new, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){
        recipe = new Recipe();

        action_save_recipe = (Button)rootView.findViewById(R.id.action_save_recipe);
        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        textViewDescription.setText("IF This That That");
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

//        condition_spinner = (Spinner) rootView.findViewById(R.id.condition_spinner);
//        String[] items = new String[]{"≥", "≤"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, items);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        condition_spinner.setAdapter(adapter);
        action_event = (ImageView)rootView.findViewById(R.id.action_event);
        action_trigger = (ImageView)rootView.findViewById(R.id.action_trigger);
        action_condition = (TextView)rootView.findViewById(R.id.action_condition);
        action_condition_value = (TextView)rootView.findViewById(R.id.action_condition_value);
        action_event_condition = (TextView)rootView.findViewById(R.id.action_event_condition);
        action_event_condition_value = (TextView)rootView.findViewById(R.id.action_event_condition_value);
        action_trigger.setOnClickListener(this);
        action_save_recipe.setOnClickListener(this);


    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setTrigger(int triggerId){
        Log.d("log", "setTrigger:"+triggerId);
        int resId = Trigger.getResId(triggerId);

        if(Trigger.isTriggerComplex(triggerId)){
            //temperature
            action_condition.setVisibility(View.VISIBLE);
            action_condition.setText("≥");
            action_condition_value.setVisibility(View.VISIBLE);
            action_condition.setOnClickListener(this);
            action_condition_value.setOnClickListener(this);
        }else if(Trigger.isTriggerSimple(triggerId)){
            //voice
            action_condition.setVisibility(View.VISIBLE);
            if(triggerId == Trigger.SWITCH){
                action_condition.setText("num");
            }else
                action_condition.setText("is");

            action_condition_value.setVisibility(View.VISIBLE);
            action_condition_value.setOnClickListener(this);
            action_condition.setOnClickListener(null);
        }else {
            //all other
            action_condition.setVisibility(View.GONE);
            action_condition_value.setVisibility(View.GONE);
            action_condition.setOnClickListener(null);
            action_condition_value.setOnClickListener(null);
        }


        if(resId != -1) {
            Glide.with(this).load(resId)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(action_trigger);
        }
    }

    private void setAction(int actionId, List<Integer> selectedMulti){
        int resId = TriggerAction.getResId(actionId);

        if(TriggerAction.isTriggerComplex(actionId)){
            //temperature
            action_event_condition.setVisibility(View.VISIBLE);
            action_event_condition.setText("≥");
            action_event_condition_value.setVisibility(View.VISIBLE);
            action_event_condition.setOnClickListener(this);
            action_event_condition_value.setOnClickListener(this);
        }else if(TriggerAction.isTriggerSimple(actionId)){
            //voice && light ON
            action_event_condition.setVisibility(View.VISIBLE);

            action_event_condition.setText("is");
            action_event_condition_value.setVisibility(View.VISIBLE);
            action_event_condition_value.setOnClickListener(this);
            action_event_condition.setOnClickListener(null);
        }else {
            //all other
            if(actionId == TriggerAction.LIGHT_ON){
                action_event_condition.setVisibility(View.VISIBLE);
                String str = "[";
                for (Integer i: selectedMulti)
                    str+=(i+1)+",";
                str = str.substring(0, str.length()-1);
                str+="]";
                action_event_condition.setText(str);

            }else{
                action_event_condition.setVisibility(View.GONE);
                action_event_condition_value.setVisibility(View.GONE);
            }
            action_event_condition.setOnClickListener(null);
            action_event_condition_value.setOnClickListener(null);
        }




        if(resId != -1) {
            Glide.with(this).load(resId)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(action_event);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_condition:
                conditionDialog(TriggerIFTTTDialogFragment.TRIGGER);
                break;
            case R.id.action_condition_value:
                showChangeValueConditionDialog(TriggerIFTTTDialogFragment.TRIGGER);
                break;
            case R.id.action_trigger:
                showSelectAndEventDialog(TriggerIFTTTDialogFragment.TRIGGER);
                break;
            case R.id.action_event:
                showSelectAndEventDialog(TriggerIFTTTDialogFragment.TRIGGER_ACTION);
                break;
            case R.id.action_event_condition_value:
                showChangeValueConditionDialog(TriggerIFTTTDialogFragment.TRIGGER_ACTION);
                break;
            case R.id.action_event_condition:
                conditionDialog(TriggerIFTTTDialogFragment.TRIGGER_ACTION);
                break;
            case R.id.action_save_recipe:

                if(recipe.getTriggerId() >= 0 && recipe.getActionId() >= 0) {
//                    Recipe recipe = new Recipe(selectedTriggerId, selectedActionId);
                    if (Trigger.isTriggerComplex(recipe.getTriggerId())) {
//                        recipe.setConditionTrigger(conditionTrigger);
//                        recipe.setConditionTriggerValue(conditionTriggerValue);
                    } else if (Trigger.isTriggerSimple(recipe.getTriggerId())) {
//                        recipe.setConditionTriggerValue(conditionTriggerValue);
                    } else {

                    }

                    if (TriggerAction.isTriggerComplex(recipe.getActionId())) {
//                        recipe.setConditionEvent(conditionEvent);
//                        recipe.setConditionEventValue(conditionEventValue);
                    } else if (TriggerAction.isTriggerSimple(recipe.getActionId())) {
//                        recipe.setConditionEventValue(conditionEventValue);
                    } else {

                    }

//                    if(recipe.getActionId() == TriggerAction.LIGHT_ON || recipe.getTriggerId() == Trigger.SWITCH){
//                        recipe.setSelectedMulti(selectedMulti);
//                    }



                    List<Recipe> recipes = new ModelCache<List<Recipe>>().loadModel(new TypeToken<List<Recipe>>(){}.getType(), Global.OFFLINE_RECIPES);
                    if(recipes == null)
                        recipes = new ArrayList<>();
                    recipes.add(recipe);
                    new ModelCache<List<Recipe>>().saveModel(recipes, Global.OFFLINE_RECIPES);
                    if(listener != null)
                        listener.onNewRecipe();
                    ((MainActivity)getActivity()).getEventManager().reloadSavedRecipes();
                    getDialog().dismiss();

                }else{
                    Toast.makeText(getActivity(), "Please select both the trigger and action", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void setClickListener(View view){
        view.setOnClickListener(this);
    }

    private void showSelectAndEventDialog(int type){
        TriggerIFTTTDialogFragment newFragment = TriggerIFTTTDialogFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String TAG = (type == TriggerIFTTTDialogFragment.TRIGGER)? "select_trigger" : "select_action";
        Fragment prev = getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        newFragment.setListener(new CommunicationIFTTT() {
            @Override
            public void onTriggerSelected(int triggerOrActionId, int type, List<Integer> selected) {
                if (type == TriggerIFTTTDialogFragment.TRIGGER) {
                    recipe.setTriggerId(triggerOrActionId);
                    setTrigger(triggerOrActionId);
                    recipe.setSelectedTrigger(selected);
                    setClickListener(action_event);
                } else {
                    recipe.setActionId(triggerOrActionId);
                    recipe.setSelectedAction(selected);
                    setAction(triggerOrActionId, selected);

                }


            }
        });
        newFragment.setType(type);
        newFragment.show(ft, TAG);
    }

    public interface CommunicationIFTTT{
        void onTriggerSelected(int triggerId, int type, List<Integer> selectedMulti);
    }

    private void conditionDialog(final int type){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select Condition");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("≥");
        arrayAdapter.add("≤");


        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int pos) {
                        if(type == TriggerIFTTTDialogFragment.TRIGGER) {
                            action_condition.setText(arrayAdapter.getItem(pos));
                            recipe.setConditionTrigger(arrayAdapter.getItem(pos));
                        }else{
                            action_event_condition.setText(arrayAdapter.getItem(pos));
                            recipe.setConditionEvent(arrayAdapter.getItem(pos));
                        }
                    }
                });
        builderSingle.show();
    }

    public void showChangeValueConditionDialog(final int type) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edittext, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        if(type == TriggerIFTTTDialogFragment.TRIGGER) {
            edt.setText(recipe.getConditionTriggerValue());
            dialogBuilder.setTitle("Trigger Value");
        }else{
            edt.setText( recipe.getConditionEventValue());
            dialogBuilder.setTitle("Event Value");
        }

        dialogBuilder.setMessage(Trigger.getLabelFromId(recipe.getTriggerId()));
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();


                if(type == TriggerIFTTTDialogFragment.TRIGGER) {
                    action_condition_value.setText(edt.getText().toString());
                    recipe.setConditionTriggerValue(edt.getText().toString());
                }else{
                    action_event_condition_value.setText(edt.getText().toString());
                    recipe.setConditionEventValue(edt.getText().toString());
                }

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void setListener(IFTTTDialogFragment.NewRecipeCreated listener) {
        this.listener = listener;
    }
}
