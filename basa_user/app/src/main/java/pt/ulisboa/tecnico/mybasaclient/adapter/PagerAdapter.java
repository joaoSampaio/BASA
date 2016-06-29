package pt.ulisboa.tecnico.mybasaclient.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.ui.HomeFragment;
import pt.ulisboa.tecnico.mybasaclient.ui.UserFragment;

/**
 * Created by sampaio on 29-06-2016.
 */
public class PagerAdapter extends FragmentPagerAdapter {

    int size = 2;
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(position == Global.HOME){
            fragment = HomeFragment.newInstance();
        }else {
            fragment = UserFragment.newInstance();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "teste";
    }


    @Override
    public float getPageWidth(int position) {
        float width = 1.0f;
        switch (position){

            case Global.USER:
                width = 0.75f;
                break;

        }
        return(width);
    }


    public static class Holder {
        private FragmentManager manager;
        public Holder(FragmentManager manager) {
            this.manager = manager;
        }



    }




}