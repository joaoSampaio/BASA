package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.RecipeAdapter;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.util.ModelCache;


public class IFTTTDialogFragment extends DialogFragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;
    private List<Recipe> data;


    public IFTTTDialogFragment() {
        // Required empty public constructor
    }



    public static IFTTTDialogFragment newInstance() {
        IFTTTDialogFragment fragment = new IFTTTDialogFragment();
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
        rootView = inflater.inflate(R.layout.fragment_ifttt, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){


        rootView.findViewById(R.id.button_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ModelCache<List<Recipe>>().saveModel(new ArrayList<Recipe>(), Global.OFFLINE_RECIPES);
                data.clear();
                mAdapter.notifyDataSetChanged();
            }
        });

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

        rootView.findViewById(R.id.action_new_recipe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewIFTTTDialogFragment newFragment = AddNewIFTTTDialogFragment.newInstance();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("new_recipe");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                newFragment.setListener(new NewRecipeCreated() {
                    @Override
                    public void onNewRecipe() {
                        refreshAdapter();
                    }
                });
                newFragment.show(ft, "new_recipe");
            }
        });

        data = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new RecipeAdapter(getActivity(), data);
        mRecyclerView.setAdapter(mAdapter);
        refreshAdapter();
    }

    private void refreshAdapter(){
        List<Recipe> recipes = new ModelCache<List<Recipe>>().loadModel(new TypeToken<List<Recipe>>(){}.getType(), Global.OFFLINE_RECIPES);
        if(recipes != null && recipes.size() > 0 && recipes.get(0) instanceof Recipe) {
            data.clear();
            Log.d("recipe", "recipes:"+recipes.size());
            data.addAll(recipes);
            mAdapter.notifyDataSetChanged();
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


    public interface NewRecipeCreated{
        void onNewRecipe();
    }

}
