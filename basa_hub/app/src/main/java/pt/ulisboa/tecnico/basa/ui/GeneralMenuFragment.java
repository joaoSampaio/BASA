package pt.ulisboa.tecnico.basa.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.MenuAdapter;
import pt.ulisboa.tecnico.basa.model.GeneralMenuItem;
import pt.ulisboa.tecnico.basa.ui.ifttt.IFTTTMainFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.EventHistoryFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.RegisterUserDialogFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.SettingsTemperatureFragment;
import pt.ulisboa.tecnico.basa.ui.secondary.StatisticsFragment;
import pt.ulisboa.tecnico.basa.util.ViewClicked;

//import android.support.v4.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;


public class GeneralMenuFragment extends Fragment {


    View rootView;
    boolean isOn = true;
    private RecyclerView mRecyclerView;
    private MenuAdapter mAdapter;
//    private static final int[] CLICK = {R.id.action_ifttt, R.id.action_settings, R.id.action_intruder,
//            R.id.action_settings_temperature, R.id.action_user, R.id.action_history};

    public GeneralMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_general_menu, container, false);



        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, 20, true));

        List<GeneralMenuItem> data = new ArrayList<>();
        data.add(new GeneralMenuItem(GeneralMenuItem.IFTTT));
//        data.add(new GeneralMenuItem(GeneralMenuItem.SETTINGS_TEMP));
        data.add(new GeneralMenuItem(GeneralMenuItem.HISTORY));
        data.add(new GeneralMenuItem(GeneralMenuItem.REGISTER));
        data.add(new GeneralMenuItem(GeneralMenuItem.SETTINGS));
//        data.add(new GeneralMenuItem(GeneralMenuItem.INTRUDER));
        data.add(new GeneralMenuItem(GeneralMenuItem.STATISTICS));

        mAdapter = new MenuAdapter(getActivity(), data, new ViewClicked() {
            @Override
            public void onClick(final int id) {


                DialogFragment newFragment = null;
                String tag = " ";
                switch (id){
                    case GeneralMenuItem.IFTTT:
                        newFragment = IFTTTMainFragment.newInstance();
                        tag = "IFTTTMainFragment";
                        break;
                    case GeneralMenuItem.SETTINGS:
                        ((Launch2Activity)getActivity()).openFragment(Global.PAGE_SETTINGS);
                        break;
                    case GeneralMenuItem.SETTINGS_TEMP:
                        newFragment = SettingsTemperatureFragment.newInstance();
                        tag = "SettingsTemperatureFragment";
                        break;
                    case GeneralMenuItem.REGISTER:
                        Log.d("register", "register user");
                        newFragment = RegisterUserDialogFragment.newInstance();
                        tag = "RegisterUserDialogFragment";
                        break;
                    case GeneralMenuItem.INTRUDER:

                        Launch2Activity activity = (Launch2Activity)getActivity();
                        activity.toggleScreen(!activity.isScreenOn());

                        Log.d("menu", "action_intruder user");
                        break;
                    case GeneralMenuItem.HISTORY:
                        newFragment = EventHistoryFragment.newInstance();
                        tag = "EventHistoryFragment";
                        break;
                    case GeneralMenuItem.STATISTICS:
                        newFragment = StatisticsFragment.newInstance();
                        tag = "StatisticsFragment";
                        break;

                }


                if(newFragment != null) {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag(tag);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    newFragment.show(ft, tag);
                }




            }
        });
        mRecyclerView.setAdapter(mAdapter);



        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }


}
