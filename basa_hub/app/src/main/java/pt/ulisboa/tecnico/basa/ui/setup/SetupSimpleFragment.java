package pt.ulisboa.tecnico.basa.ui.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.BasaDeviceConfig;


public class SetupSimpleFragment extends Fragment{


    View rootView;
    EditText editDescription, editName;
    private boolean showError;

    public SetupSimpleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_simple, container, false);
        editName = (EditText)rootView.findViewById(R.id.editTextName);
        editDescription = (EditText)rootView.findViewById(R.id.editTextDescription);
        this.showError = false;
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    @Override
    public void onResume(){
        super.onResume();
        Log.d("main", "  onResume:"+showError);

        editName.setError(null);
        if(showError){
            editName.setError("Must have 4 or more letters");
        }

        ((MainSetupActivity)getActivity()).setBasicListener(new MainSetupActivity.SetupDataInterface() {
            @Override
            public boolean onBasicDataReady(BasaDeviceConfig deviceConfig) {

                String name = editName.getText().toString();
                String description = editDescription.getText().toString();
                boolean error = false;
                showError = false;
                editName.setError(null);

                Log.d("main", "  name.length() < 4 _>" + ( name.length() < 4));
                if(name == null || name.length() < 4){
                    editName.setError("Must have 4 or more letters");
                    error = true;
                    showError = true;
                }

                if(error){
                    Toast.makeText(AppController.getAppContext(), "Basic information missing!", Toast.LENGTH_SHORT).show();
                    return false;
                }

                deviceConfig.setName(name);
                deviceConfig.setDescription(description);
                return true;

            }

            @Override
            public void onAdvancedDataReady(BasaDeviceConfig deviceConfig) {

            }
        });



    }

        @Override
    public void onPause(){
        super.onPause();
            ((MainSetupActivity)getActivity()).setBasicListener(null);
    }


}
