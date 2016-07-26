package pt.ulisboa.tecnico.basa.ui.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;


public class SetupSecurityFragment extends Fragment implements View.OnClickListener{


    View rootView;
    EditText editTextPin, editTextPin2;
    private static final int[] CLICK = { R.id.action_save};

    public SetupSecurityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_final, container, false);


        editTextPin = (EditText)rootView.findViewById(R.id.editTextPin);
        editTextPin2 = (EditText)rootView.findViewById(R.id.editTextPin2);

        for (int id: CLICK)
            rootView.findViewById(id).setOnClickListener(this);



        editTextPin.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                if(c.toString().equals(editTextPin2.getText().toString())) {
                    editTextPin2.setError(null);
                    AppController.getInstance().getDeviceConfig().setPinSha(c.toString());
                }else if(!editTextPin2.getText().toString().isEmpty()){
                    editTextPin2.setError("Pin does not match");
                } else{
                    editTextPin2.setError(null);
                }
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

        editTextPin2.addTextChangedListener(new TextWatcher() {

            // the user's changes are saved here
            public void onTextChanged(CharSequence c, int start, int before, int count) {

                if(c.toString().equals(editTextPin.getText().toString())) {
                    editTextPin2.setError(null);
                    AppController.getInstance().getDeviceConfig().setPinSha(c.toString());
                }else{
                    editTextPin2.setError("Pin does not match");
                }
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // this space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                // this one too
            }
        });

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

            case R.id.action_save:
                ((MainSetupActivity)getActivity()).saveConfig();
                break;



        }
    }
}
