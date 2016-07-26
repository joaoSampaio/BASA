package pt.ulisboa.tecnico.basa.ui.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.app.AppController;


public class SetupDatabaseFragment extends Fragment{


    View rootView, container_firebase_yes, container_firebase_no;
    Button generateUuid;
    EditText editUuid;
    private Switch switchFirebase;

    public SetupDatabaseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_setup_database, container, false);


        generateUuid = (Button)rootView.findViewById(R.id.action_generate_uuid);
        editUuid = (EditText)rootView.findViewById(R.id.editTextUuid);
        switchFirebase = (Switch)rootView.findViewById(R.id.switchFirebase);

        container_firebase_yes = rootView.findViewById(R.id.container_firebase_yes);
        container_firebase_no = rootView.findViewById(R.id.container_firebase_no);
        AppController.getInstance().getDeviceConfig().setFirebaseEnabled(true);

        switchFirebase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                AppController.getInstance().getDeviceConfig().setFirebaseEnabled(isChecked);
                if(isChecked && FirebaseAuth.getInstance().getCurrentUser() != null){
                    AppController.getInstance().getDeviceConfig()
                            .setUuid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }
                AppController.getInstance().getDeviceConfig().setFirebaseEnabled(isChecked);
                container_firebase_yes.setVisibility(isChecked? View.VISIBLE : View.GONE);
                container_firebase_no.setVisibility(isChecked? View.GONE : View.VISIBLE);

            }
        });

        rootView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainSetupActivity)getActivity()).signIn();
            }
        });

        container_firebase_no.setVisibility(View.GONE);
        switchFirebase.setChecked(true);

        generateUuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uuid = UUID.randomUUID().toString();
                editUuid.setText(uuid);
                AppController.getInstance().getDeviceConfig()
                        .setUuid(uuid);
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
        ((MainSetupActivity)getActivity()).setSignInReady(new SignInReady() {
            @Override
            public void onSignIn(FirebaseUser user) {
                if(getActivity() != null) {
                    ((TextView) rootView.findViewById(R.id.textViewName)).setText(user.getDisplayName());
                    ((TextView) rootView.findViewById(R.id.textViewEmail)).setText(user.getEmail());
                    ((TextView) rootView.findViewById(R.id.textViewId)).setText(user.getUid());
                }
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() != null){

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            ((TextView) rootView.findViewById(R.id.textViewName)).setText(user.getDisplayName());
            ((TextView) rootView.findViewById(R.id.textViewEmail)).setText(user.getEmail());
            ((TextView) rootView.findViewById(R.id.textViewId)).setText(user.getUid());


        }

    }

        @Override
    public void onPause(){
        super.onPause();
            ((MainSetupActivity)getActivity()).setSignInReady(null);
    }


    public interface SignInReady{
        void onSignIn(FirebaseUser user);
    }


}
