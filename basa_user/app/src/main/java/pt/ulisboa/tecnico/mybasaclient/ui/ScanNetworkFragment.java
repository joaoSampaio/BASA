package pt.ulisboa.tecnico.mybasaclient.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.adapter.IPNetworkAdapter;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.IPNetwork;
import pt.ulisboa.tecnico.mybasaclient.util.DividerItemDecoration;

/**
 * Created by Sampaio on 27/06/2016.
 */
public class ScanNetworkFragment extends DialogFragment {

    View rootView;
    Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private IPNetworkAdapter mAdapter;
    private List<IPNetwork> devices;

    public static ScanNetworkFragment newInstance() {
        ScanNetworkFragment fragment = new ScanNetworkFragment();
        return fragment;
    }


    public ScanNetworkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_scan_network, container, false);
        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        if (toolbar!=null) {
            toolbar.setTitle("Add Zone");
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    getDialog().dismiss();
                }
            });
        }

        devices = new ArrayList<>();

        if(mRecyclerView == null) {

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecyclerView.setHasFixedSize(true);
            mAdapter = new IPNetworkAdapter((MainActivity) getActivity(), devices);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));


            mRecyclerView.setAdapter(mAdapter);

        }
        WifiManager mWifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        AppController.getInstance().setScanResultAvailableListener(new ScanResultAvailableListener() {
            @Override
            public void onResultsAvailable(List<ScanResult> mScanResults) {
                devices.clear();
                for(ScanResult result: mScanResults)
                    devices.add(IPNetwork.convert(result));

                mAdapter.notifyDataSetChanged();
            }
        });

    }




    @Override
    public void onPause() {
        super.onPause();
        AppController.getInstance().setScanResultAvailableListener(null);
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface ScanResultAvailableListener{
        void onResultsAvailable(List<ScanResult> mScanResults);
    }

}
