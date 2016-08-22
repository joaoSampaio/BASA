package pt.ulisboa.tecnico.basa.ui.ifttt;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.PreMadeRecipeAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.util.TaskCompleted;
import pt.ulisboa.tecnico.basa.util.ViewClicked;


public class IFTTTActiveRecipesFragment extends Fragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private PreMadeRecipeAdapter mAdapter;
    private List<Recipe> data;



    public IFTTTActiveRecipesFragment() {
        // Required empty public constructor
    }



    public static IFTTTActiveRecipesFragment newInstance() {
        IFTTTActiveRecipesFragment fragment = new IFTTTActiveRecipesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ifttt, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){


        data = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new PreMadeRecipeAdapter(getActivity(), data, this, new PreMadeRecipeAdapter.UpdateRecipeList() {
            @Override
            public void updateActiveRecipe(int position, boolean active) {
                final Recipe recipe = data.get(position);
                recipe.setActive(active);
                AppController.getInstance().saveCustomRecipes(data);
                mAdapter.notifyDataSetChanged();
                AppController.getInstance().getBasaManager().getEventManager().reloadSavedRecipes();
            }
        }, new ViewClicked() {
            @Override
            public void onClick(int position) {
                showRecipeDetails(position);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        refreshAdapter();
    }

    private void refreshAdapter(){
        List<Recipe> recipes = AppController.getInstance().getCustomRecipes();
        if(recipes != null) {
            data.clear();
            Log.d("recipe", "recipes:"+recipes.size());
            data.addAll(recipes);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(getParentFragment() instanceof IFTTTMainFragment){
            ((IFTTTMainFragment)getParentFragment()).setNewRecipeCreated(new NewRecipeCreated() {
                @Override
                public void onNewRecipe() {
                    refreshAdapter();
                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(getParentFragment() instanceof IFTTTMainFragment){
            ((IFTTTMainFragment)getParentFragment()).setNewRecipeCreated(null);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void showRecipeDetails(int position){
        RecipeDetailsFragment newFragment = RecipeDetailsFragment.newInstance();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String TAG = "RecipeDetailsFragment";
        Fragment prev = getFragmentManager().findFragmentByTag(TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        newFragment.setRecipe(data.get(position));
        newFragment.setTaskCompleted(new TaskCompleted() {
            @Override
            public void onTaskCompleted() {
                refreshAdapter();
            }
        });
        newFragment.show(ft, TAG);
    }



    public interface NewRecipeCreated{
        void onNewRecipe();
    }

}
