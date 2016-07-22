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

import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoFragment extends DialogFragment {
    View rootView;
    Toolbar toolbar;

    public InfoFragment() {
        // Required empty public constructor
    }


    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_info, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        if(!AppController.getInstance().isEmptyZones()) {
            if (toolbar != null) {
                toolbar.setTitle("Info");
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
        Glide.with(this).load(R.drawable.tagus1)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into((ImageView)rootView.findViewById(R.id.imageInfo));

    }

}
