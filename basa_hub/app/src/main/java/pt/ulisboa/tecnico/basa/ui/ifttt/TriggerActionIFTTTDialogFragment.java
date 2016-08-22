package pt.ulisboa.tecnico.basa.ui.ifttt;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.HorizontalTriggerAdapter;
import pt.ulisboa.tecnico.basa.adapter.TriggerAdapter;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.LightOnAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.SpeechAction;
import pt.ulisboa.tecnico.basa.model.recipe.action.TemperatureAction;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LightSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.LocationTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.MotionSensorTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.SpeechTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TemperatureTrigger;
import pt.ulisboa.tecnico.basa.model.recipe.trigger.TimeTrigger;
import pt.ulisboa.tecnico.basa.util.GridSpacingItemHorizontalDecoration;
import pt.ulisboa.tecnico.basa.util.Tooltip;
import pt.ulisboa.tecnico.basa.util.TriggerOrActionSelected;
import pt.ulisboa.tecnico.basa.util.ViewClicked;


public class TriggerActionIFTTTDialogFragment extends DialogFragment {



    private View rootView, layout_multiple;
    private RecyclerView mRecyclerView, listSelected;
    private TriggerAdapter mAdapter;
    private TextView textViewBubble;
    private HorizontalTriggerAdapter mHorizontalAdapter;

    List<TriggerAction> triggersActions;
    private List<TriggerAction> data;
//    private AddNewIFTTTDialogFragment.CommunicationIFTTT listener;
    private AddNewIFTTTDialogFragment.SelectedTriggerAction selectedTriggerAction;
    private int type = 0;
    private boolean isSimpleTrigger;
    private Button action_add;
    private CheckBox checkMultiple;


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

        if(getTriggersActions() == null)
            triggersActions = new ArrayList<>();

        isSimpleTrigger = triggersActions.isEmpty()? true : false;

        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewBubble = (TextView)rootView.findViewById(R.id.textViewBubble);
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
        layout_multiple = rootView.findViewById(R.id.layout_multiple);
        checkMultiple = (CheckBox)rootView.findViewById(R.id.checkMultiple);
        checkMultiple.setChecked(!isSimpleTrigger);
        checkMultiple.setVisibility(View.VISIBLE);
        checkMultiple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSimpleTrigger = !isChecked;
                layout_multiple.setVisibility(isSimpleTrigger? View.GONE : View.VISIBLE);



            }
        });

        action_add = (Button)rootView.findViewById(R.id.action_add);
        action_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(type == TriggerAction.TRIGGER) {
                    getSelectedTriggerAction().onSelectedTriggers(getTriggersActions());

                }else{
                    getSelectedTriggerAction().onSelectedActions(getTriggersActions());
                }
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
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 6));
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(6, 20, true));
        Log.d("log", "--**--data trigger:" + data.size());
        mAdapter = new TriggerAdapter(getActivity(), data, new ViewClicked() {
            @Override
            public void onClick(final int triggerOrActionId) {

                if(type == TriggerAction.TRIGGER) {
                    switch (triggerOrActionId) {
                        case TriggerAction.TRIGGER_USER_LOCATION:
                            showTriggerDetails(new LocationTrigger(), TriggerAction.TRIGGER);
                            break;
                        case TriggerAction.TRIGGER_TEMPERATURE:
                            showTriggerDetails(new TemperatureTrigger(), TriggerAction.TRIGGER);
                            break;
                        case TriggerAction.TRIGGER_SPEECH:
                            showTriggerDetails(new SpeechTrigger(), TriggerAction.TRIGGER);
                            break;
                        case TriggerAction.TRIGGER_LIGHT_SENSOR:
                            showTriggerDetails(new LightSensorTrigger(), TriggerAction.TRIGGER);
                            break;
                        case TriggerAction.TRIGGER_MOTION_SENSOR:
                            showTriggerDetails(new MotionSensorTrigger(), TriggerAction.TRIGGER);
                            break;
                        case TriggerAction.TRIGGER_TIME:
                            showTriggerDetails(new TimeTrigger(), TriggerAction.TRIGGER);
                            break;

                    }

                    return;
                }

                if(type == TriggerAction.TRIGGER_ACTION) {
                    switch (triggerOrActionId) {
                        case TriggerAction.ACTION_LIGHT_ON:
                            showTriggerDetails(new LightOnAction(triggerOrActionId), TriggerAction.TRIGGER_ACTION);
                            break;
                        case TriggerAction.ACTION_TALK:
                            showTriggerDetails(new SpeechAction(triggerOrActionId), TriggerAction.TRIGGER_ACTION);
                            break;
                        case TriggerAction.ACTION_CHANGE_TEMPERATURE:
                            showTriggerDetails(new TemperatureAction(triggerOrActionId), TriggerAction.TRIGGER_ACTION);
                            break;

                    }
                }

            }
        });
        mRecyclerView.setAdapter(mAdapter);

        listSelected = (RecyclerView) rootView.findViewById(R.id.listSelected);
        listSelected.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false));
        listSelected.addItemDecoration(new GridSpacingItemHorizontalDecoration(1, 20, true));

        mHorizontalAdapter = new HorizontalTriggerAdapter(getActivity(), getTriggersActions(), new HorizontalTriggerAdapter.MultiTriggerSelected() {
            @Override
            public void onMultiSelected(View v, int position) {
                showTriggerDetails(getTriggersActions().get(position), type);

            }

            @Override
            public void onMultiLongSelected(final View view, int position) {
                final String text = getTriggersActions().get(position).getParameterTitle();

                if(!textViewBubble.isShown()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewBubble.setText(text);
                            Tooltip.applyToolTipPosition(view, textViewBubble);
                        }
                    },100);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewBubble.setText(text);
                        Tooltip.applyToolTipPosition(view, textViewBubble);
                    }
                },50);
            }
        });
        listSelected.setAdapter(mHorizontalAdapter);
        layout_multiple.setVisibility(isSimpleTrigger? View.GONE : View.VISIBLE);
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
                if(!getTriggersActions().contains(trigger))
                    getTriggersActions().add(trigger);
                if(isSimpleTrigger) {
                    getSelectedTriggerAction().onSelectedTriggers(getTriggersActions());
                    getDialog().dismiss();
                }else{
                    mHorizontalAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onActionSelected(TriggerAction action) {

                if(!getTriggersActions().contains(action))
                    getTriggersActions().add(action);
                if(isSimpleTrigger) {
                    getSelectedTriggerAction().onSelectedActions(getTriggersActions());
                    getDialog().dismiss();
                }else{
                    mHorizontalAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTriggerActionDelete(TriggerAction trigger) {
                getTriggersActions().remove(trigger);
                if(textViewBubble.isShown()){
                    textViewBubble.setVisibility(View.INVISIBLE);
                }
                mHorizontalAdapter.notifyDataSetChanged();
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
        data.add(new LocationTrigger());
        data.add(new SpeechTrigger());
        data.add(new TemperatureTrigger());
        data.add(new LightSensorTrigger());
        data.add(new MotionSensorTrigger());
        data.add(new TimeTrigger());

    }

    private void populateDataTriggerAction(){
        data = new ArrayList<>();
        data.add(new LightOnAction(TriggerAction.ACTION_LIGHT_ON));
        data.add(new SpeechAction(TriggerAction.ACTION_TALK));
        data.add(new TemperatureAction(TriggerAction.ACTION_CHANGE_TEMPERATURE));
//        data.add(new TriggerAction(TriggerAction.LIGHT_OFF, "Light OFF", R.drawable.ic_light));
//        data.add(new TriggerAction(TriggerAction.TRIGGER_TEMPERATURE, "Change temperature", R.drawable.ic_temperature_trigger));
//        data.add(new TriggerAction(TriggerAction.EMAIL, "Send Email", R.drawable.ic_mail));
//        data.add(new TriggerAction(TriggerAction.TRIGGER_SPEECH, "Say", R.drawable.ic_talk));
    }


    public void setType(int type) {
        this.type = type;
    }


    public List<TriggerAction> getTriggersActions() {
        return triggersActions;
    }

    public void setTriggersActions(List<TriggerAction> triggersActions) {

        this.triggersActions = new ArrayList<>(triggersActions);
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
