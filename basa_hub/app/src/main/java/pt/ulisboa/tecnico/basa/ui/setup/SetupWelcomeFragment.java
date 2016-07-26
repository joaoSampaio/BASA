package pt.ulisboa.tecnico.basa.ui.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pt.ulisboa.tecnico.basa.R;


public class SetupWelcomeFragment extends Fragment{


    View rootView;

    public SetupWelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login_welcome, container, false);



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
