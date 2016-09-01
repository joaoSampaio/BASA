package pt.ulisboa.tecnico.mybasaclient.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.adapter.VideoAdapter;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.firebase.FirebaseFileLink;
import pt.ulisboa.tecnico.mybasaclient.util.DividerItemDecoration;


public class DeviceVideoHistoryFragment extends DialogFragment {
    View rootView;
    Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private BasaDevice device;
    private List<FirebaseFileLink> history;
    private PlayVideoHistory playVideoHistory;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private ValueEventListener videoHistoryListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            GenericTypeIndicator<HashMap<String, FirebaseFileLink>> t = new GenericTypeIndicator<HashMap<String, FirebaseFileLink>>() {};

            HashMap<String, FirebaseFileLink> videos = dataSnapshot.getValue(t);
            if(videos != null) {
                history.clear();
                history.addAll(new TreeMap<String, FirebaseFileLink>(videos).values());
                if (mAdapter != null)
                    mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w("ddd", "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };

    public DeviceVideoHistoryFragment() {
        // Required empty public constructor
    }


    public static DeviceVideoHistoryFragment newInstance() {
        DeviceVideoHistoryFragment fragment = new DeviceVideoHistoryFragment();
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
        rootView =  inflater.inflate(R.layout.fragment_device_camera_history, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        device = AppController.getInstance().getCurrentDevice();
        if (toolbar!=null) {

            toolbar.setTitle(device.getName());
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(getDialog() != null)
                        getDialog().dismiss();
                }
            });
        }
        init();
        return rootView;
    }


    @Override
    public void onResume(){
        super.onResume();
        mDatabase.child("history").child(device.getId()).addValueEventListener(videoHistoryListener);
    }

    @Override
    public void onPause(){
        super.onPause();
        mDatabase.removeEventListener(videoHistoryListener);
    }

    private void init(){


        history = new ArrayList<>();

        View settings = rootView.findViewById(R.id.action_settings);
        settings.setVisibility(View.VISIBLE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).openPage(Global.DIALOG_DEVICE_SETTINGS);
            }
        });

        if(mRecyclerView == null) {

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new VideoAdapter(this, history);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setVideoSelected(new VideoAdapter.VideoSelected() {
                @Override
                public void onVideoSelected(int position) {

                    if(history.size() > position && getPlayVideoHistory() != null){
                        getPlayVideoHistory().onVideoSelected(history.get(position));
                        if(getDialog() != null)
                            getDialog().dismiss();
                    }
                }
            });
        }
    }

    public PlayVideoHistory getPlayVideoHistory() {
        return playVideoHistory;
    }

    public void setPlayVideoHistory(PlayVideoHistory playVideoHistory) {
        this.playVideoHistory = playVideoHistory;
    }

    public interface PlayVideoHistory{
        void onVideoSelected(FirebaseFileLink selected);
    }

}
