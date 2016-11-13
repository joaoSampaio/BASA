package pt.ulisboa.tecnico.basa.ui.ifttt;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.PreMadeRecipeAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.recipe.Recipe;
import pt.ulisboa.tecnico.basa.util.ViewClicked;


public class IFTTTPreMadeRecipesFragment extends Fragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private PreMadeRecipeAdapter mAdapter;
    private List<Recipe> data;


    public IFTTTPreMadeRecipesFragment() {
        // Required empty public constructor
    }



    public static IFTTTPreMadeRecipesFragment newInstance() {
        IFTTTPreMadeRecipesFragment fragment = new IFTTTPreMadeRecipesFragment();
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

        if(mRecyclerView == null) {

            data.add(new Recipe());
            data.add(new Recipe());


            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            mRecyclerView.setHasFixedSize(true);
//            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 30, true));
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

                }
            });

            mRecyclerView.setAdapter(mAdapter);
        }
        mAdapter.notifyDataSetChanged();


    }



    @Override
    public void onStart() {
        super.onStart();
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



}
