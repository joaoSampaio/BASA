package pt.ulisboa.tecnico.basa.ui.ifttt;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.Tooltip;


public class AddNewIFTTTDialogFragment extends DialogFragment implements View.OnClickListener {

    private View rootView, containerSave;
    private ImageView action_trigger, action_event;
    private Spinner condition_spinner;
    private Button action_save_recipe;
    TextView textViewTriggerDescription, textViewRecipe;
    private IFTTTActiveRecipesFragment.NewRecipeCreated listener;
    private EditText editTextDescription, editTextShort;
    Recipe recipe;
    private boolean isEdit = false;

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
        if(recipe == null)
            recipe = new Recipe();
        containerSave = rootView.findViewById(R.id.containerSave);
        textViewTriggerDescription = (TextView)rootView.findViewById(R.id.textViewTriggerDescription);
        textViewRecipe = (TextView)rootView.findViewById(R.id.textViewRecipe);
        action_save_recipe = (Button)rootView.findViewById(R.id.action_save_recipe);
        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        textViewDescription.setText("Create a Recipe");
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


        editTextDescription = (EditText)rootView.findViewById(R.id.editTextDescription);
        action_trigger.setOnClickListener(this);


        if(!recipe.getActions().isEmpty()){
            isEdit = true;
            textViewDescription.setText("Edit Recipe");
            setUpEdit();

        }



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
    public void onResume() {
        super.onResume();

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                applyToolTipPosition(action_trigger, textViewTriggerDescription);
//            }
//        },1000);
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
        int resId = TriggerAction.getInvertedResId(triggerId);

        if(resId != -1) {
            Glide.with(this).load(resId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(action_trigger);
        }
    }

    private void setAction(int actionId){
        int resId = TriggerAction.getResId(actionId);
        if(resId != -1) {
            Glide.with(this).load(resId)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(action_event);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.action_trigger:
                showSelectAndEventDialog(TriggerAction.TRIGGER, recipe.getTriggers());
                break;
            case R.id.action_event:
                showSelectAndEventDialog(TriggerAction.TRIGGER_ACTION, recipe.getActions());
                break;

            case R.id.action_save_recipe:
                Log.d("json", "action_save_recipe:");


                if(recipe.getActions().isEmpty() || recipe.getTriggers().isEmpty()){
                    Toast.makeText(AppController.getAppContext(), "Trigger or action missing!", Toast.LENGTH_SHORT).show();
                    return;
                }



                recipe.setActive(true);
                Log.d("json", "recipe.setActive(true);:");
                Log.d("json", "json:"+new Gson().toJson(recipe));
                Log.d("json", "json:"+new Gson().toJson(recipe.getTriggers()));

//                    recipe.setDescription(editTextDescription.getText().toString());
//                    recipe.setShortName(editTextShort.getText().toString());
//
                List<Recipe> recipes = AppController.getInstance().getCustomRecipes();
                if(recipes == null)
                    recipes = new ArrayList<>();
                if(!isEdit) {
                    recipes.add(recipe);
                }else{

                    Recipe recipeSaved = Recipe.findRecipeById(recipes, recipe.getId());
                    if(recipeSaved != null){

                        recipeSaved.setTriggers(recipe.getTriggers());
                        recipeSaved.setActions(recipe.getActions());

                    }


                }
                Log.d("json", "recipes.add(recipe);:");
                AppController.getInstance().saveCustomRecipes(recipes);
                if(listener != null)
                    listener.onNewRecipe();
                Log.d("json", "listener.onNewRecipe():");
                AppController.getInstance().getBasaManager().getEventManager().reloadSavedRecipes();
                Log.d("json", "reloadSavedRecipes:");
                getDialog().dismiss();


                break;
        }
    }

    private void setClickListener(View view){
        view.setOnClickListener(this);
    }

    private void showSelectAndEventDialog(int type, List<TriggerAction> triggerActions){
        TriggerActionIFTTTDialogFragment newFragment = TriggerActionIFTTTDialogFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String TAG = (type == TriggerAction.TRIGGER)? "select_trigger" : "select_action";
        Fragment prev = getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        newFragment.setTriggersActions(triggerActions);
        newFragment.setSelectedTriggerAction(new SelectedTriggerAction() {
            @Override
            public void onSelectedTriggers(List<TriggerAction> triggers) {
                recipe.setTriggers(triggers);

                if(!recipe.getTriggers().isEmpty() && !recipe.getActions().isEmpty())
                    textViewRecipe.setText(recipe.getRecipeDescription());
                if(!triggers.isEmpty()) {
                    setTrigger(triggers.get(0).getTriggerActionId());
                    textViewTriggerDescription.setText(recipe.getTriggersDescription());
                    action_event.setOnClickListener(AddNewIFTTTDialogFragment.this);
                    if(recipe.getActions().isEmpty())
                        Glide.with(AddNewIFTTTDialogFragment.this)
                                .load(R.drawable.ic_add_ta_active)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(action_event);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Tooltip.applyToolTipPosition(action_trigger, textViewTriggerDescription);
                        }
                    },500);
                }else{
                    Glide.with(AddNewIFTTTDialogFragment.this)
                            .load(R.drawable.ic_add_ta_active)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(action_trigger);
                    editTextDescription.setText("");

                }
            }

            @Override
            public void onSelectedActions(List<TriggerAction> actions) {
                recipe.setActions(actions);

                if(!recipe.getTriggers().isEmpty() && !recipe.getActions().isEmpty())
                    textViewRecipe.setText(recipe.getRecipeDescription());

                if(!actions.isEmpty()) {
                    setAction(actions.get(0).getTriggerActionId());
                    textViewTriggerDescription.setText(recipe.getActionsDescription());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Tooltip.applyToolTipPosition(action_event, textViewTriggerDescription);
                        }
                    },500);


                    if(recipe.getTriggers().size() > 1 || recipe.getActions().size() > 1){
                        editTextDescription.setVisibility(View.VISIBLE);
                    }else{
                        editTextDescription.setVisibility(View.GONE);
                    }
                    containerSave.setVisibility(View.VISIBLE);
                    action_save_recipe.setOnClickListener(AddNewIFTTTDialogFragment.this);

                }
            }
        });

        newFragment.setType(type);
        newFragment.show(ft, TAG);
    }


    private void setUpEdit(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setTrigger(recipe.getTriggers().get(0).getTriggerActionId());
                setAction(recipe.getActions().get(0).getTriggerActionId());
            }
        },100);

        action_save_recipe.setText("Finish edit");
        textViewRecipe.setText(recipe.getRecipeDescription());
        containerSave.setVisibility(View.VISIBLE);
        action_save_recipe.setOnClickListener(AddNewIFTTTDialogFragment.this);
        action_event.setOnClickListener(AddNewIFTTTDialogFragment.this);
    }


    public interface SelectedTriggerAction {
        void onSelectedTriggers(List<TriggerAction> triggers);

        void onSelectedActions(List<TriggerAction> triggers);
    }

    public void setListener(IFTTTActiveRecipesFragment.NewRecipeCreated listener) {
        this.listener = listener;
    }


    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {

        this.recipe = recipe.createCopy();
    }
}
