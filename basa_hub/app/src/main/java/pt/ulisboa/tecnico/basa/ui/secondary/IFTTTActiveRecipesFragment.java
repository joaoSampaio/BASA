package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.PreMadeRecipeAdapter;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.util.ModelCache;


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


//        rootView.findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new ModelCache<List<Recipe>>().saveModel(new ArrayList<Recipe>(), Global.OFFLINE_RECIPES);
//                data.clear();
//                mAdapter.notifyDataSetChanged();
//            }
//        });



        data = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new PreMadeRecipeAdapter(getActivity(), data, this);
        mRecyclerView.setAdapter(mAdapter);
        refreshAdapter();
    }

    private void refreshAdapter(){
        List<Recipe> recipes = new ModelCache<List<Recipe>>().loadRecipes();
        if(recipes != null && recipes.size() > 0 && recipes.get(0) instanceof Recipe) {
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




    public interface NewRecipeCreated{
        void onNewRecipe();
    }

}
