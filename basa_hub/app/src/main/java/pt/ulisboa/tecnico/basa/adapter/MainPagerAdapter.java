package pt.ulisboa.tecnico.basa.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.ui.GeneralMenuFragment;
import pt.ulisboa.tecnico.basa.ui.LightsFragment;
import pt.ulisboa.tecnico.basa.ui.PreferencesFragment;
import pt.ulisboa.tecnico.basa.ui.TemperatureFragment;


public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case Global.PAGE_LIGHTS:
                fragment = new LightsFragment();
                break;
            case Global.PAGE_TEMPERATURE:
                fragment = new TemperatureFragment();
                break;
            case Global.PAGE_OPTIONS:
                fragment = new GeneralMenuFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }



}
