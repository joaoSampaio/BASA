package pt.ulisboa.tecnico.mybasaclient.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddZonePart1Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddZonePart1Fragment extends DialogFragment implements View.OnClickListener {
    View rootView;
    Toolbar toolbar;

    private final static int[] CLICK = {R.id.gotoPage1};

    public AddZonePart1Fragment() {
        // Required empty public constructor
    }


    public static AddZonePart1Fragment newInstance() {
        AddZonePart1Fragment fragment = new AddZonePart1Fragment();
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
        rootView =  inflater.inflate(R.layout.fragment_add_zone, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        if(!Zone.loadZones().isEmpty()) {
            if (toolbar != null) {
                toolbar.setTitle("Add Zone");
                toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if (getActivity() != null)
                            ((MainActivity) getActivity()).dismissAllDialogs(getActivity().getSupportFragmentManager());
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
        Glide.with(this).load(R.drawable.room1)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView)rootView.findViewById(R.id.imageHome));


        for(int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.gotoPage1:
//                getDialog().dismiss();
                ((MainActivity)getActivity()).openPage(Global.DIALOG_ADD_ZONE_PART2);

                break;
        }
    }
}
