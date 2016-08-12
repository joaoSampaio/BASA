package pt.ulisboa.tecnico.basa.ui.secondary;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pt.ulisboa.tecnico.basa.R;
import pt.ulisboa.tecnico.basa.adapter.EventHistoryAdapter;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.EventHistory;


public class EventHistoryFragment extends DialogFragment {

    private View rootView;
    private RecyclerView mRecyclerView;
    private List<EventHistory> data;
    private Toolbar toolbar;
    private EventHistoryAdapter mEventHistoryAdapter;


    public EventHistoryFragment() {
        // Required empty public constructor
    }



    public static EventHistoryFragment newInstance() {
        EventHistoryFragment fragment = new EventHistoryFragment();
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
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        loadUI();
        return rootView;
    }

    public void loadUI(){


        TextView textViewDescription = (TextView)rootView.findViewById(R.id.textViewDescription);

        textViewDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        textViewDescription.setText("Event history");
        rootView.findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        if(data == null)
            data = AppController.getInstance().getHistory();
        Log.d("EVENT", "**------------------------**data:" + data.size());
//        data.add(new EventHistory("User arrived in office"));
//        data.add("Light turned on");
//        data.add("User Exit Office");
//        data.add("Um atexto grande e qualquer coisa");

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mEventHistoryAdapter = new EventHistoryAdapter(getActivity(), data);

        mRecyclerView.setAdapter(mEventHistoryAdapter);
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

        AppController.getInstance().getBasaManager().getEventManager().setUpdateHistory(new UpdateHistory() {
            @Override
            public void onUpdateHistory() {
                Log.d("EVENT","User:---->onUpdateHistory:" + ( AppController.getInstance().getHistory() == data));

                mEventHistoryAdapter.notifyDataSetChanged();
            }
        });


    }


    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().getBasaManager().getEventManager().setUpdateHistory(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface UpdateHistory{
        void onUpdateHistory();
    }

}
