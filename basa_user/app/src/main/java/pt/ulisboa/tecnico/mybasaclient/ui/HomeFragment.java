package pt.ulisboa.tecnico.mybasaclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.adapter.DeviceAdapter;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.util.GridSpacingItemDecoration;
import pt.ulisboa.tecnico.mybasaclient.util.ViewPagerPageScroll;

/**
 * Created by Sampaio on 27/06/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    View rootView, header, header2;
    private ViewPagerPageScroll pageScroll;
    private TextView textViewTitle, zoneTitle;
    private RecyclerView mRecyclerView;
    private DeviceAdapter mAdapter;
    private List<BasaDevice> devices;
    private View settings;

    private final static int[] CLICK = {R.id.settings, R.id.action_goToUser, R.id.header2};


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        settings = rootView.findViewById(R.id.settings);
        settings.setVisibility(View.GONE);
        header = rootView.findViewById(R.id.header);
        header2 = rootView.findViewById(R.id.header2);
        header2.setVisibility(View.GONE);
        header2.setAlpha(0);
        devices = new ArrayList<>();

        textViewTitle = (TextView)rootView.findViewById(R.id.textViewTitle);
        zoneTitle = (TextView)rootView.findViewById(R.id.zoneTitle);

        refreshHome();
        for(int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);

        return rootView;
    }


    private void refreshHome(){
        Zone current = Zone.getCurrentZone();
        if(current != null) {
            settings.setVisibility(View.VISIBLE);
            textViewTitle.setText(current.getName());
            zoneTitle.setText(current.getName());

            devices.clear();
            devices.addAll(current.getDevices());

//            devices.add(new BasaDevice("url", "2N.11.5", "O meu escritorio no Tagus", "token"));
//            devices.add(new BasaDevice("url", "2N.11.7", "O meu escritorio no Tagus", "token", 10));
//            devices.add(new BasaDevice("url", "Gabinete Prof", "O meu escritorio no Tagus", "token"));

            if(mRecyclerView == null) {
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 30, true));
                mAdapter = new DeviceAdapter((MainActivity) getActivity(), devices);
                mRecyclerView.setAdapter(mAdapter);
            }
            mAdapter.notifyDataSetChanged();



        }
    }


    @Override
    public void onResume() {
        super.onResume();
        pageScroll = new ViewPagerPageScroll() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(position == 0){
                    //starts with alpha 1 and at 0.8 is at 0
                    float alfaHeader = 5 * positionOffset -4 ;

                    float alfaHeader2 = -5 * positionOffset + (float) 3.75 ;

//                    if(positionOffset > 0.98)
//                        alfaHeader = 1;
                    header2.setVisibility((positionOffset < 0.95)? View.VISIBLE : View.GONE);

                    header.setAlpha(alfaHeader);
                    header2.setAlpha(alfaHeader2);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(position == 1){
                    header.setAlpha(1);
                }
            }
        };
        ((MainActivity)getActivity()).addPageListener(pageScroll);
        ((MainActivity)getActivity()).setCommunicationHomeFragment(new CommunicationHomeFragment() {
            @Override
            public void changeZone(String name) {

                Zone current = Zone.getZoneByName(name);
                if(current != null) {
                    Zone.saveCurrentZone(current);
                    refreshHome();
                }
            }

            @Override
            public void updateZone() {
                refreshHome();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).removePageListener(pageScroll);
        ((MainActivity)getActivity()).setCommunicationHomeFragment(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.settings:
                ((MainActivity)getActivity()).openPage(Global.DIALOG_SETTINGS_ZONE);
                break;
            case R.id.action_goToUser:
                ((MainActivity)getActivity()).openViewpagerPage(Global.USER);
                break;
            case R.id.header2:
                ((MainActivity)getActivity()).openViewpagerPage(Global.HOME);
                break;
        }
    }


    public interface CommunicationHomeFragment{

        void changeZone(String name);
        void updateZone();

    }








}
