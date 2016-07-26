package pt.ulisboa.tecnico.basa.ui.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import pt.ulisboa.tecnico.basa.R;


public class SetupAdvancedFragment extends Fragment implements View.OnClickListener{


    View rootView;
    EditText editTextBeacon, editTextMac;
    private static final int[] CLICK = {R.id.action_open_beacon, R.id.action_open_mac};

    public SetupAdvancedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_advanced, container, false);


        editTextBeacon = (EditText)rootView.findViewById(R.id.editTextBeacon);
        editTextMac = (EditText)rootView.findViewById(R.id.editTextMac);

        for (int id: CLICK)
            rootView.findViewById(id).setOnClickListener(this);


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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.action_open_beacon:

                break;

            case R.id.action_open_mac:

                break;



        }
    }
}
