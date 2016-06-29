package pt.ulisboa.tecnico.mybasaclient.ui;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddZonePart2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddZonePart2Fragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;
    EditText editTextZoneName;

    private final static int[] CLICK = {R.id.gotoPage2};

    public AddZonePart2Fragment() {
        // Required empty public constructor
    }


    public static AddZonePart2Fragment newInstance() {
        AddZonePart2Fragment fragment = new AddZonePart2Fragment();
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
        rootView =  inflater.inflate(R.layout.fragment_add_zone2, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        editTextZoneName = (EditText)rootView.findViewById(R.id.editTextZoneName);
        if (toolbar!=null) {
            toolbar.setTitle("Add Zone");
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

//            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    ((MainActivity)getActivity()).openPage(Global.DIALOG_ADD_ZONE_PART1);
                }
            });
        }

        init();
        return rootView;
    }

    private void init(){

        openKeyboard();
        for(int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);

        openPage(R.id.page2);
    }


    private void openPage(int page){

        rootView.findViewById(R.id.page2).setVisibility(View.GONE);
        rootView.findViewById(R.id.page3).setVisibility(View.GONE);
        rootView.findViewById(page).setVisibility(View.VISIBLE);

    }


    private void closeKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openKeyboard(){
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(rootView, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gotoPage2:
                editTextZoneName.setError(null);
                //validar e criar
                String name = editTextZoneName.getText().toString();
                if(name.length() >= 4){
                    closeKeyboard();
                    openPage(R.id.page3);
                    List<Zone> zones = Zone.loadZones();
                    zones.add(new Zone(name));
                    Zone.saveZones(zones);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(getDialog() != null)
                                getDialog().dismiss();
                        }
                    }, 1000);


                }else {
                    editTextZoneName.setError("Name too short");
                    editTextZoneName.requestFocus();
                }

                break;
        }
    }
}
