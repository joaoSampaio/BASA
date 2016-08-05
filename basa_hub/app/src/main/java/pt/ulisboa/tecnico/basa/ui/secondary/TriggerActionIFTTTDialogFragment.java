package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.TriggerAdapter;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.LightOnAction;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LocationTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TemperatureTrigger;
import pt.ulisboa.tecnico.basa.util.TriggerOrActionSelected;
import pt.ulisboa.tecnico.basa.util.ViewClicked;


public class TriggerActionIFTTTDialogFragment extends DialogFragment {



    private View rootView;
    private RecyclerView mRecyclerView;
    private TriggerAdapter mAdapter;
    List<TriggerAction> triggers;
    List<TriggerAction> actions;
    private List<TriggerAction> data;
//    private AddNewIFTTTDialogFragment.CommunicationIFTTT listener;
    private AddNewIFTTTDialogFragment.SelectedTriggerAction selectedTriggerAction;
    private int type = 0;
    private boolean isSimpleTrigger;

    public TriggerActionIFTTTDialogFragment() {
        // Required empty public constructor
    }



    public static TriggerActionIFTTTDialogFragment newInstance() {
        TriggerActionIFTTTDialogFragment fragment = new TriggerActionIFTTTDialogFragment();
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
        rootView = inflater.inflate(R.layout.fragment_ifttt_trigger, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){
        isSimpleTrigger = true;
        triggers = new ArrayList<>();
        actions = new ArrayList<>();
        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);

        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        if(type == TriggerAction.TRIGGER){
            populateDataTrigger();
            textViewDescription.setText("Select Trigger");
        }else {
            populateDataTriggerAction();
            textViewDescription.setText("Select Action");
        }


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mRecyclerView.setHasFixedSize(true);

        Log.d("log", "--**--data trigger:" + data.size());
        mAdapter = new TriggerAdapter(getActivity(), data, new ViewClicked() {
            @Override
            public void onClick(final int triggerOrActionId) {

                if(type == TriggerAction.TRIGGER) {
                    switch (triggerOrActionId) {
                        case TriggerAction.USER_LOCATION:
                            showTriggerDetails(new LocationTrigger(triggerOrActionId), TriggerAction.TRIGGER);
                            break;
                        case TriggerAction.TEMPERATURE:
                            showTriggerDetails(new TemperatureTrigger(triggerOrActionId), TriggerAction.TRIGGER);
                            break;
                    }

                    return;
                }

                if(type == TriggerAction.TRIGGER_ACTION) {
                    switch (triggerOrActionId) {
                        case TriggerAction.LIGHT_ON:
                            showTriggerDetails(new LightOnAction(triggerOrActionId), TriggerAction.TRIGGER_ACTION);
                            break;

                    }
                }

//                if(type == TRIGGER_ACTION && triggerOrActionId == TriggerAction.LIGHT_ON){
//                    Log.d("log", "TriggerAction.LIGHT_ON:");
//                    new DialogMultiSelect(getActivity(), getLights(), getLightsSelected(), "Select the Lights", new DialogMultiSelect.DialogMultiSelectResponse() {
//                        @Override
//                        public void onSucess(boolean[] checkedValues) {
//
//                            List<Integer> selectedMulti = new ArrayList<Integer>();
//                            for(int i = 0; i<  checkedValues.length; i++){
//                                if(checkedValues[i])
//                                    selectedMulti.add(i);
//                            }
//
//                            getDialog().dismiss();
//                        }
//                    }).show();
//                }else if(type == TRIGGER && triggerOrActionId == Trigger.SWITCH){
//                    Log.d("log", "Trigger.SWITCH:");
//                    new DialogOneChoiceSelect(getActivity(), getCustomSwitches(), "Pick one switch", new DialogOneChoiceSelect.DialogOneSelectResponse() {
//                        @Override
//                        public void onSucess(int id, int type) {
//                            List<Integer> selected = new ArrayList<Integer>();
//                            //the id is the position of the switch
//                            selected.add(id);
//                            getDialog().dismiss();
//                        }
//                    }, type).show();
//                }
//                else {
//                    getDialog().dismiss();
//                }




            }
        });
        mRecyclerView.setAdapter(mAdapter);



    }


    private void showTriggerDetails(TriggerAction triggerAction, int type){
        IFTTTTriggerDetailsFragment newFragment = IFTTTTriggerDetailsFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String TAG = "IFTTTTriggerDetailsFragment";
        Fragment prev = getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        newFragment.setTriggerAction(triggerAction);
        newFragment.setType(type);
        newFragment.setTriggerOrActionSelected(new TriggerOrActionSelected() {
            @Override
            public void onTriggerSelected(TriggerAction trigger) {
                triggers.add(trigger);
                if(isSimpleTrigger) {
                    getSelectedTriggerAction().onSelectedTriggers(triggers);

//                    listener.onTriggerSelected(triggers.get(0).getEventId(), type, null);
                    getDialog().dismiss();
                }

            }

            @Override
            public void onActionSelected(TriggerAction action) {
                actions.add(action);
                if(isSimpleTrigger) {
                    getSelectedTriggerAction().onSelectedActions(actions);

//                    listener.onTriggerSelected(triggers.get(0).getEventId(), type, null);
                    getDialog().dismiss();
                }
            }
        });
        newFragment.show(ft, TAG);
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


    public AddNewIFTTTDialogFragment.SelectedTriggerAction getSelectedTriggerAction() {
        return selectedTriggerAction;
    }

    public void setSelectedTriggerAction(AddNewIFTTTDialogFragment.SelectedTriggerAction selectedTriggerAction) {
        this.selectedTriggerAction = selectedTriggerAction;
    }

    private void populateDataTrigger(){
        data = new ArrayList<>();
        data.add(new LocationTrigger(TriggerAction.USER_LOCATION));
//        data.add(new Trigger(Trigger.CLAP));
//        data.add(new Trigger(Trigger.SWITCH));
//        data.add(new Trigger(Trigger.VOICE));
        data.add(new TemperatureTrigger(TriggerAction.TEMPERATURE));
    }

    private void populateDataTriggerAction(){
        data = new ArrayList<>();
        data.add(new LightOnAction(TriggerAction.LIGHT_ON));
//        data.add(new TriggerAction(TriggerAction.LIGHT_OFF, "Light OFF", R.drawable.ic_light));
//        data.add(new TriggerAction(TriggerAction.TEMPERATURE, "Change temperature", R.drawable.ic_temperature_trigger));
//        data.add(new TriggerAction(TriggerAction.EMAIL, "Send Email", R.drawable.ic_mail));
//        data.add(new TriggerAction(TriggerAction.VOICE, "Say", R.drawable.ic_talk));
    }


    public void setType(int type) {
        this.type = type;
    }






    public List<String> getCustomSwitches(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int numCustomSwitches = Integer.parseInt(preferences.getString("custom_button_number", "0"));
        List<String> customSwitches = new ArrayList<>();
        for(int i = 1; i <= numCustomSwitches; i++){
            customSwitches.add("Switch "+i);
        }
        return customSwitches;
    }


}
