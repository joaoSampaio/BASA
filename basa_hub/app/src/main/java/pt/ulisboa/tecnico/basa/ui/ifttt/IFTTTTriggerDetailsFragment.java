package pt.ulisboa.tecnico.basa.ui.ifttt;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.LinkedHashMap;
import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.TriggerDetailsAdapter;
import pt.ulisboa.tecnico.basa.model.recipe.TriggerAction;
import pt.ulisboa.tecnico.basa.util.TriggerActionParameterSelected;
import pt.ulisboa.tecnico.basa.util.TriggerOrActionSelected;


public class IFTTTTriggerDetailsFragment extends DialogFragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private TriggerDetailsAdapter mAdapter;
    private TextView textViewDescription, textViewParameterTitle;
    private View layout_header;
    private LinkedHashMap<String, Object> data;
    private TriggerAction triggerAction;
    private TriggerOrActionSelected triggerOrActionSelected;
    private ImageView imageViewDetail;
    private Toolbar toolbar;
    private LinearLayout layoutCustom;
    private int type = 0;
    private Button action_delete;


    public IFTTTTriggerDetailsFragment() {
        // Required empty public constructor
    }



    public static IFTTTTriggerDetailsFragment newInstance() {
        IFTTTTriggerDetailsFragment fragment = new IFTTTTriggerDetailsFragment();
        return fragment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTriggerAction(TriggerAction trigger) {
        this.triggerAction = trigger;
        if(data == null)
            data = new LinkedHashMap<>();
        data.putAll(trigger.getAlternatives());
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public TriggerOrActionSelected getTriggerOrActionSelected() {
        return triggerOrActionSelected;
    }

    public void setTriggerOrActionSelected(TriggerOrActionSelected triggerOrActionSelected) {
        this.triggerOrActionSelected = triggerOrActionSelected;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ifttt_trigger_details, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        toolbar.setTitle(triggerAction.getTitle());
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                if(getDialog() != null)
                    getDialog().dismiss();
            }
        });
        toolbar.setBackgroundColor(triggerAction.getColor());
        toolbar.setTitleTextColor(Color.WHITE);
        textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);
        textViewParameterTitle = (TextView)rootView.findViewById(R.id.textViewParameterTitle);
        layout_header = rootView.findViewById(R.id.layout_header);
        layout_header.setBackgroundColor(triggerAction.getColor());
        layoutCustom  = (LinearLayout)rootView.findViewById(R.id.layoutCustom);
        imageViewDetail = (ImageView)rootView.findViewById(R.id.imageViewDetail);
        Glide.with(this)
                .load(triggerAction.getResId())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(imageViewDetail);

        action_delete = (Button)rootView.findViewById(R.id.action_delete);

        textViewDescription.setText(triggerAction.getDescription());

        if(!triggerAction.getParameters().isEmpty()){

            action_delete.setVisibility(View.VISIBLE);
            textViewParameterTitle.setVisibility(View.VISIBLE);
            textViewParameterTitle.setText(Html.fromHtml("<h2><b>Current trigger: </b></h2><br>" + triggerAction.getParameterTitle()));
//            textViewParameterTitle.setText(triggerAction.getParameterTitle());
            action_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTriggerOrActionSelected().onTriggerActionDelete( triggerAction);
                    getDialog().dismiss();
                }
            });

        }

        if(data == null)
            data = new LinkedHashMap<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new TriggerDetailsAdapter(getActivity(), data, triggerAction, new TriggerActionParameterSelected() {
            @Override
            public void onTriggerOrActionParameterSelected(List<String> parameter) {
                triggerAction.getParameters().clear();
                triggerAction.getParameters().addAll(parameter);
                if(getTriggerOrActionSelected() != null) {
                    if(getType() == TriggerAction.TRIGGER)
                        getTriggerOrActionSelected().onTriggerSelected((TriggerAction) triggerAction);
                    else {
                        getTriggerOrActionSelected().onActionSelected((TriggerAction) triggerAction);
                    }

                }
                getDialog().dismiss();

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }



    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(triggerAction != null){
            triggerAction.setUpCustomView(layoutCustom);
        }

    }


    @Override
    public void onPause() {
        super.onPause();


        if(triggerAction != null){
            triggerAction.destroyCustomView();
            layoutCustom.removeAllViews();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



}
