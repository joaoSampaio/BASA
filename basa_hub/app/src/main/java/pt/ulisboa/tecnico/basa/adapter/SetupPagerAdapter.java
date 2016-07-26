package pt.ulisboa.tecnico.basa.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.ui.setup.SetupAdvancedFragment;
import pt.ulisboa.tecnico.basa.ui.setup.SetupDatabaseFragment;
import pt.ulisboa.tecnico.basa.ui.setup.SetupSecurityFragment;
import pt.ulisboa.tecnico.basa.ui.setup.SetupSimpleFragment;
import pt.ulisboa.tecnico.basa.ui.setup.SetupTemperatureLightFragment;
import pt.ulisboa.tecnico.basa.ui.setup.SetupWelcomeFragment;


public class SetupPagerAdapter extends FragmentStatePagerAdapter {

    public static final int SIZE = 6;

    public SetupPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case Global.PAGE_SETUP_WELCOME:
                fragment = new SetupWelcomeFragment();
                break;
            case Global.PAGE_SETUP_BASIC:
                fragment = new SetupSimpleFragment();
                break;
            case Global.PAGE_SETUP_DATABASE:
                fragment = new SetupDatabaseFragment();
                break;

            case Global.PAGE_SETUP_LIGHT_TEMP:
                fragment = new SetupTemperatureLightFragment();
                break;
            case Global.PAGE_SETUP_LOCATION:
                fragment = new SetupAdvancedFragment();
                break;
            case Global.PAGE_SETUP_SECURITY:
                fragment = new SetupSecurityFragment();
                break;

        }

        return fragment;
    }

    @Override
    public int getCount() {
        return SIZE;
    }



}
