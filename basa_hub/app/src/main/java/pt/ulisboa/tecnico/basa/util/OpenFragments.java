package pt.ulisboa.tecnico.basa.util;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.ui.Launch2Activity;

/**
 * Created by Sampaio on 19/08/2016.
 */
public class OpenFragments {

    private Launch2Activity activity;

    public OpenFragments(Launch2Activity activity) {
        this.activity = activity;
    }

    public void openPage(int page){

        DialogFragment newFragment = null;
        String tag = " ";

        switch (page) {
            case Global.DIALOG_PICK_TIME:
                newFragment = SelectDayTimeDialog.newInstance();
                tag = "SelectDayTimeDialog";
                break;

        }


        if(newFragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(tag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            newFragment.show(ft, tag);
        }


    }




    public Launch2Activity getActivity() {
        return activity;
    }
}
