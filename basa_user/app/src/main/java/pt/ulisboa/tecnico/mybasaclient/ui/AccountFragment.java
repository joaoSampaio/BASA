package pt.ulisboa.tecnico.mybasaclient.ui;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    User user;
    CheckBox checkboxFirebase, checkboxTracking, checkBoxTestRoom, checkBoxTestBuilding;

    private final static int[] CLICK = {R.id.editUsername, R.id.editEmail , R.id.sign_out,
            R.id.enableFirebase, R.id.enableTracking, R.id.enableTestLocationRoom, R.id.enableTestLocationBuilding};

    public AccountFragment() {
        // Required empty public constructor
    }


    public static AccountFragment newInstance() {
        AccountFragment fragment = new AccountFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_account, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        if(!AppController.getInstance().isEmptyZones()) {
            if (toolbar != null) {
                toolbar.setTitle("Account");
                toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (getDialog() != null)
                            getDialog().dismiss();
                    }
                });
            }
        }else{
            toolbar.setVisibility(View.GONE);
        }

        init();
        return rootView;
    }

    private void init(){
        user = AppController.getInstance().getLoggedUser();
        checkboxFirebase = (CheckBox) rootView.findViewById(R.id.checkboxFirebase);
        checkboxFirebase.setChecked(user.isEnableFirebase());

        checkboxTracking = (CheckBox) rootView.findViewById(R.id.checkboxTracking);
        checkboxTracking.setChecked(user.isEnableTracking());

        checkBoxTestRoom = (CheckBox) rootView.findViewById(R.id.checkBoxTestRoom);
        checkBoxTestBuilding = (CheckBox) rootView.findViewById(R.id.checkBoxTestBuilding);

        checkBoxTestRoom.setChecked(user.isEnableTestRoomLocation());
        checkBoxTestBuilding.setChecked(user.isEnableTestBuildingLocation());


        for(int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editUsername:

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setCancelable(false);
                final EditText editText = new EditText(getActivity());

                alertDialogBuilder.setView(editText);
                alertDialogBuilder.setTitle("Edit username");
                editText.setText(user.getUserName());
                editText.setSelection(editText.getText().length());
                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("Rename", null)
                        .setNegativeButton("Cancel", null);
                final AlertDialog alert = alertDialogBuilder.create();
                alert.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface arg0) {

                        Button okButton = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                        okButton.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View arg0) {

                                String name = editText.getText().toString().trim();

                                if(name.length() >= 4){
                                    user.setUserName(name);
                                    AppController.getInstance().setLoggedUser(user);
                                    alert.dismiss();
                                }else{
                                    Snackbar snack = Snackbar.make(editText, "Name too short", Snackbar.LENGTH_SHORT);
                                    View view = snack.getView();
                                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                                    tv.setTextColor(Color.RED);
                                    snack.show();
                                }
                            }
                        });
                    }
                });
                alert.show();


                break;
            case R.id.editEmail:

                ((MainActivity)getActivity()).openPage(Global.DIALOG_DEVICE_SCAN_WIFI);


                break;
            case R.id.sign_out:

                ((MainActivity)getActivity()).signOut();

                break;

            case R.id.enableTracking:
                user.setEnableTracking(!user.isEnableTracking());
                checkboxTracking.setChecked(user.isEnableTracking());
                break;

            case R.id.enableFirebase:
                Log.d("arc", "user.isEnableFirebase():"+user.isEnableFirebase());
                user.setEnableFirebase(!user.isEnableFirebase());
                checkboxFirebase.setChecked(user.isEnableFirebase());
                Log.d("arc", "depois user.isEnableFirebase():"+user.isEnableFirebase());
                break;

            case R.id.enableTestLocationRoom:

                AppController.getInstance().beaconStart();

                user.setEnableTestRoomLocation(!user.isEnableTestRoomLocation());
                checkBoxTestRoom.setChecked(user.isEnableTestRoomLocation());
                break;

            case R.id.enableTestLocationBuilding:
                user.setEnableTestBuildingLocation(!user.isEnableTestBuildingLocation());
                checkBoxTestBuilding.setChecked(user.isEnableTestBuildingLocation());
                break;

        }
    }
}
