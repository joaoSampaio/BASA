package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;


public class IFTTTMainFragment extends DialogFragment {

    private View rootView;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private IFTTTActiveRecipesFragment.NewRecipeCreated newRecipeCreated;

    public IFTTTMainFragment() {
        // Required empty public constructor
    }



    public static IFTTTMainFragment newInstance() {
        IFTTTMainFragment fragment = new IFTTTMainFragment();
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
        rootView = inflater.inflate(R.layout.fragment_ifttt_main, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){


        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        toolbar.setTitle("IFTTT");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                if(getDialog() != null)
                    getDialog().dismiss();
            }
        });
        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

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
                newFragment.setListener(new IFTTTActiveRecipesFragment.NewRecipeCreated() {
                    @Override
                    public void onNewRecipe() {
                        //refreshAdapter();
                        if(getNewRecipeCreated() != null)
                            getNewRecipeCreated().onNewRecipe();
                    }
                });
                newFragment.show(ft, "new_recipe");
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        IFTTTActiveRecipesFragment frag = IFTTTActiveRecipesFragment.newInstance();
        adapter.addFragment(IFTTTActiveRecipesFragment.newInstance(), "Active Recipes");
        adapter.addFragment(IFTTTPreMadeRecipesFragment.newInstance(), "Pré-made Recipes");
        viewPager.setAdapter(adapter);
    }

    public IFTTTActiveRecipesFragment.NewRecipeCreated getNewRecipeCreated() {
        return newRecipeCreated;
    }

    public void setNewRecipeCreated(IFTTTActiveRecipesFragment.NewRecipeCreated newRecipeCreated) {
        this.newRecipeCreated = newRecipeCreated;
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


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
