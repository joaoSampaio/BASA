package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.ModelCache;


public class AddNewIFTTTDialogFragment extends DialogFragment implements View.OnClickListener {

    private View rootView, containerSave;
    private ImageView action_trigger, action_event;
    private Spinner condition_spinner;
    private Button action_save_recipe;
    TextView textViewTriggerDescription, textViewRecipe;
    private IFTTTActiveRecipesFragment.NewRecipeCreated listener;
    private EditText editTextDescription, editTextShort;
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
        editTextShort = (EditText)rootView.findViewById(R.id.editTextShort);
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
        int resId = TriggerAction.getResId(triggerId);

        if(resId != -1) {
            Glide.with(this).load(resId)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(action_trigger);
        }
    }

    private void setAction(int actionId){
        int resId = TriggerAction.getResId(actionId);
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

            case R.id.action_trigger:
                showSelectAndEventDialog(TriggerAction.TRIGGER);
                break;
            case R.id.action_event:
                showSelectAndEventDialog(TriggerAction.TRIGGER_ACTION);
                break;

            case R.id.action_save_recipe:

                recipe.setActive(true);

                Log.d("json", "json:"+new Gson().toJson(recipe));
                Log.d("json", "json:"+new Gson().toJson(recipe.getTriggers()));

//                    recipe.setDescription(editTextDescription.getText().toString());
//                    recipe.setShortName(editTextShort.getText().toString());
//
                    List<Recipe> recipes = new ModelCache<List<Recipe>>().loadRecipes();
                    if(recipes == null)
                        recipes = new ArrayList<>();
                    recipes.add(recipe);
                    new ModelCache<List<Recipe>>().saveModel(recipes, Global.OFFLINE_RECIPES);
                    if(listener != null)
                        listener.onNewRecipe();
                    AppController.getInstance().getBasaManager().getEventManager().reloadSavedRecipes();
                    getDialog().dismiss();


                break;
        }
    }

    private void setClickListener(View view){
        view.setOnClickListener(this);
    }

    private void showSelectAndEventDialog(int type){
        TriggerActionIFTTTDialogFragment newFragment = TriggerActionIFTTTDialogFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String TAG = (type == TriggerAction.TRIGGER)? "select_trigger" : "select_action";
        Fragment prev = getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        newFragment.setSelectedTriggerAction(new SelectedTriggerAction() {
            @Override
            public void onSelectedTriggers(List<TriggerAction> triggers) {
                recipe.setTriggers(triggers);
                if(!triggers.isEmpty()) {
                    setTrigger(triggers.get(0).getTriggerActionId());
                    textViewTriggerDescription.setText(triggers.get(0).getParameterTitle());
                    setClickListener(action_event);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            applyToolTipPosition(action_trigger, textViewTriggerDescription);
                        }
                    },200);

                }


            }

            @Override
            public void onSelectedActions(List<TriggerAction> actions) {
                recipe.setActions(actions);
                if(!actions.isEmpty()) {
                    setAction(actions.get(0).getTriggerActionId());
                    textViewTriggerDescription.setText(actions.get(0).getParameterTitle());


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            applyToolTipPosition(action_event, textViewTriggerDescription);
                        }
                    },200);


                    textViewRecipe.setText(recipe.getRecipeDescription());
                    containerSave.setVisibility(View.VISIBLE);

                }
            }
        });

        newFragment.setType(type);
        newFragment.show(ft, TAG);
    }


    public interface SelectedTriggerAction {
        void onSelectedTriggers(List<TriggerAction> triggers);

        void onSelectedActions(List<TriggerAction> triggers);
    }

    public void setListener(IFTTTActiveRecipesFragment.NewRecipeCreated listener) {
        this.listener = listener;
    }





    private int mRelativeMasterViewY;

    private int mRelativeMasterViewX;

    private void applyToolTipPosition(final View mView, final View tooltip) {
        final int[] masterViewScreenPosition = new int[2];
        mView.getLocationOnScreen(masterViewScreenPosition);

        final Rect viewDisplayFrame = new Rect();
        mView.getWindowVisibleDisplayFrame(viewDisplayFrame);

        final int[] parentViewScreenPosition = new int[2];
        (tooltip).getLocationOnScreen(parentViewScreenPosition);

        final int masterViewWidth = mView.getWidth();
        final int masterViewHeight = mView.getHeight();

        mRelativeMasterViewX = masterViewScreenPosition[0] - parentViewScreenPosition[0];
        mRelativeMasterViewY = masterViewScreenPosition[1] - parentViewScreenPosition[1];
        final int relativeMasterViewCenterX = mRelativeMasterViewX + masterViewWidth / 2;

        int toolTipViewAboveY = mRelativeMasterViewY - tooltip.getHeight();
        int toolTipViewBelowY = Math.max(0, mRelativeMasterViewY + masterViewHeight);

//        int toolTipViewX = Math.max(0, relativeMasterViewCenterX - mWidth / 2);
//        if (toolTipViewX + mWidth > viewDisplayFrame.right) {
//            toolTipViewX = viewDisplayFrame.right - mWidth;
//        }

//        setX(toolTipViewX);
//        setPointerCenterX(relativeMasterViewCenterX);
//        tooltip.setX( relativeMasterViewCenterX - (int) mView.getX());

//        tooltip.setX(mView.getX());
//        tooltip.setX(relativeMasterViewCenterX);

        Log.d("tool", "mView.masterViewScreenPosition[0]():"+masterViewScreenPosition[0]);
        Log.d("tool", "tooltip.getX():"+tooltip.getX());
        Log.d("tool", "mView.getHeight():"+mView.getHeight());
        Log.d("tool", "tooltip.getHeight():"+tooltip.getHeight());
        Log.d("tool", "tooltip.getWidth():"+tooltip.getWidth());
        tooltip.setX(masterViewScreenPosition[0] + mView.getWidth()/2 - tooltip.getWidth()/2);
        tooltip.setY(masterViewScreenPosition[1] - mView.getHeight()/2 - tooltip.getHeight()/2);
        tooltip.setVisibility(View.VISIBLE);
        Log.d("tool", "tooltip.getX() setX:"+tooltip.getX());
        Log.d("tool", "tooltip.getY() setY:"+tooltip.getY());




        final boolean showBelow = toolTipViewAboveY < 0;


//        int toolTipViewY;
//        if (showBelow) {
//            toolTipViewY = toolTipViewBelowY;
//        } else {
//            toolTipViewY = toolTipViewAboveY;
//        }
//
//        mView.setTranslationX(toolTipViewX);
//        mView.setTranslationY( toolTipViewY);
    }

















}
