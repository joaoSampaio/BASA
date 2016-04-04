package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.TriggerAdapter;
import pt.ulisboa.tecnico.basa.model.RecipeEvent;
import pt.ulisboa.tecnico.basa.model.Trigger;
import pt.ulisboa.tecnico.basa.model.TriggerAction;
import pt.ulisboa.tecnico.basa.util.DialogMultiSelect;
import pt.ulisboa.tecnico.basa.util.DialogOneChoiceSelect;
import pt.ulisboa.tecnico.basa.util.ViewClicked;


public class TriggerIFTTTDialogFragment extends DialogFragment {

    public static final int TRIGGER = 0;
    public static final int TRIGGER_ACTION = 1;

    private View rootView;
    private RecyclerView mRecyclerView;
    private TriggerAdapter mAdapter;
    private List<RecipeEvent> data;
    private AddNewIFTTTDialogFragment.CommunicationIFTTT listener;
    private int type = 0;

    public TriggerIFTTTDialogFragment() {
        // Required empty public constructor
    }



    public static TriggerIFTTTDialogFragment newInstance() {
        TriggerIFTTTDialogFragment fragment = new TriggerIFTTTDialogFragment();
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

        if(type == TRIGGER){
            populateDataTrigger();
            textViewDescription.setText("Select Trigger");
        }else {
            populateDataTriggerAction();
            textViewDescription.setText("Select Action");
        }


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new TriggerAdapter(getActivity(), data, new ViewClicked() {
            @Override
            public void onClick(final int id) {


                if(id == TriggerAction.LIGHT_ON){
                    new DialogMultiSelect(getActivity(), getLights(), getLightsSelected(), "Select the Lights", new DialogMultiSelect.DialogMultiSelectResponse() {
                        @Override
                        public void onSucess(boolean[] checkedValues) {

                            List<Integer> selectedMulti = new ArrayList<Integer>();
                            for(int i = 0; i<  checkedValues.length; i++){
                                if(checkedValues[i])
                                    selectedMulti.add(i);
                            }

                            listener.onTriggerSelected(id, type, selectedMulti);
                            getDialog().dismiss();
                        }
                    }).show();
                }else if(id == Trigger.SWITCH){
                    new DialogOneChoiceSelect(getActivity(), getCustomSwitches(), "Pick one switch", new DialogOneChoiceSelect.DialogOneSelectResponse() {
                        @Override
                        public void onSucess(int id, int type) {
                            List<Integer> selected = new ArrayList<Integer>();
                            //the id is the position of the switch
                            selected.add(id);
                            listener.onTriggerSelected(id, type, selected);
                            getDialog().dismiss();
                        }
                    }, type).show();
                }
                else {
                    listener.onTriggerSelected(id, type, null);
                    getDialog().dismiss();
                }




            }
        });
        mRecyclerView.setAdapter(mAdapter);



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


    private void populateDataTrigger(){
        data = new ArrayList<>();
        data.add(new Trigger(Trigger.CLAP, "Clap", R.drawable.ic_clap));
        data.add(new Trigger(Trigger.SWITCH, "Switch ON", R.drawable.ic_switch_on));
        data.add(new Trigger(Trigger.VOICE, "Voice", R.drawable.ic_voice));
        data.add(new Trigger(Trigger.HAND_PROXIMITY, "Hand proximity", R.drawable.ic_hand_proximity));
        data.add(new Trigger(Trigger.TEMPERATURE, "Temperature", R.drawable.ic_temperature_trigger));

    }

    private void populateDataTriggerAction(){
        data = new ArrayList<>();
        data.add(new TriggerAction(TriggerAction.LIGHT_ON, "Light ON", R.drawable.ic_light_on));
        data.add(new TriggerAction(TriggerAction.LIGHT_OFF, "Light OFF", R.drawable.ic_light));
        data.add(new TriggerAction(TriggerAction.TEMPERATURE, "Change temperature", R.drawable.ic_temperature_trigger));
        data.add(new TriggerAction(TriggerAction.EMAIL, "Send Email", R.drawable.ic_mail));
        data.add(new TriggerAction(TriggerAction.VOICE, "Say", R.drawable.ic_talk));
    }

    public void setListener(AddNewIFTTTDialogFragment.CommunicationIFTTT listener) {
        this.listener = listener;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String[] getLights(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int numLights = Integer.parseInt(preferences.getString("light_number", "1"));
        List<String> result = new ArrayList<>();
        for(int i = 1; i<=numLights; i++){
            result.add("Light "+i);
        }
        String[] array = new String[result.size()];
        return result.toArray(array);
    }

    public boolean[] getLightsSelected(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int numLights = Integer.parseInt(preferences.getString("light_number", "1"));
        boolean[] result = new boolean[numLights];
        for(int i = 0; i < numLights; i++){
            result[i] = false;
        }
        return result;
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
