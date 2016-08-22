package pt.ulisboa.tecnico.basa.ui.ifttt;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.util.TaskCompleted;


public class RecipeDetailsFragment extends DialogFragment {



    private View rootView;
    private ImageView imageTrigger, imageAction;
    private TextView textViewRecipe;
    private View colorTrigger, colorAction;
    private Button action_edit, action_delete;
    private Recipe recipe;
    private TaskCompleted taskCompleted;

    public RecipeDetailsFragment() {
        // Required empty public constructor
    }



    public static RecipeDetailsFragment newInstance() {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
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
        rootView = inflater.inflate(R.layout.fragment_ifttt_recipe_detail, container, false);
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
        textViewDescription.setText("Personal recipe");


        imageTrigger = (ImageView)rootView.findViewById(R.id.imageTrigger);
        imageAction = (ImageView)rootView.findViewById(R.id.imageAction);
        textViewRecipe = (TextView)rootView.findViewById(R.id.textViewRecipe);
        colorTrigger = rootView.findViewById(R.id.layoutFirst);
        colorAction = rootView.findViewById(R.id.layoutSecond);

        action_edit = (Button)rootView.findViewById(R.id.action_edit_recipe);
        action_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewIFTTTDialogFragment newFragment = AddNewIFTTTDialogFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("new_recipe");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                newFragment.setRecipe(recipe);

                newFragment.setListener(new IFTTTActiveRecipesFragment.NewRecipeCreated() {
                    @Override
                    public void onNewRecipe() {

                        //refresh UI
                        updateRecipeUi();
                        //refreshAdapter();
                        if(getTaskCompleted() != null){
                            getTaskCompleted().onTaskCompleted();
                        }
                    }
                });
                newFragment.show(ft, "new_recipe");

            }
        });

        action_delete = (Button)rootView.findViewById(R.id.action_delete_recipe);
        action_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean success = AppController.getInstance().getCustomRecipes().remove(recipe);
                Log.d("recipe", "delete success:" + success);
                AppController.getInstance().saveCustomRecipes(AppController.getInstance().getCustomRecipes());
                if(getTaskCompleted() != null){
                    getTaskCompleted().onTaskCompleted();
                }
                getDialog().dismiss();
            }
        });

        updateRecipeUi();

    }

    private void updateRecipeUi(){
        if(!getRecipe().getTriggers().isEmpty() && !getRecipe().getActions().isEmpty()) {



            setIcon(getRecipe().getTriggers().get(0).getResId(), imageTrigger);
            setIcon(getRecipe().getActions().get(0).getResId(), imageAction);
            textViewRecipe.setText(getRecipe().getRecipeDescription());

            if(getRecipe().isActive()) {

                colorTrigger.setBackgroundColor(getRecipe().getTriggers().get(0).getColor());
                colorAction.setBackgroundColor(getRecipe().getActions().get(0).getColor());

            }else{
                int color = Color.parseColor("#bdbdbd");
                colorTrigger.setBackgroundColor(color);
                colorAction.setBackgroundColor(color);
            }
        }
    }


    private void setIcon(int resId, ImageView image){
        if(resId != -1) {
            Log.d("recipe", "setIcon:" + resId);
            Glide.with(RecipeDetailsFragment.this).load(resId)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public TaskCompleted getTaskCompleted() {
        return taskCompleted;
    }

    public void setTaskCompleted(TaskCompleted taskCompleted) {
        this.taskCompleted = taskCompleted;
    }


}
