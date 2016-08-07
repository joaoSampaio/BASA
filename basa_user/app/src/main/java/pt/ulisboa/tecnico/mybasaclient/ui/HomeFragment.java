package pt.ulisboa.tecnico.mybasaclient.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.adapter.DeviceAdapter;
import pt.ulisboa.tecnico.mybasaclient.app.AppController;
import pt.ulisboa.tecnico.mybasaclient.model.BasaDevice;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.rest.pojo.UserLocation;
import pt.ulisboa.tecnico.mybasaclient.rest.services.CallbackFromService;
import pt.ulisboa.tecnico.mybasaclient.rest.services.UpdateLocationService;
import pt.ulisboa.tecnico.mybasaclient.util.GridSpacingItemDecoration;
import pt.ulisboa.tecnico.mybasaclient.util.ViewPagerPageScroll;

/**
 * Created by Sampaio on 27/06/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    View rootView, header, header2;
    private ViewPagerPageScroll pageScroll;
    private TextView textViewTitle, zoneTitle, textViewSelected, textViewChoice;
    private RecyclerView mRecyclerView;
    private DeviceAdapter mAdapter;
    private List<BasaDevice> devices;
    private View settings, homeOrAwayBg;
    private ImageView imageLocationSelected, imageLocationChoice;
    private boolean isHomeAwayOpen = false;
    private Zone current;

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
        homeOrAwayBg = rootView.findViewById(R.id.homeOrAwayBg);
        textViewTitle = (TextView)rootView.findViewById(R.id.textViewTitle);
        zoneTitle = (TextView)rootView.findViewById(R.id.zoneTitle);


        for(int id : CLICK)
            rootView.findViewById(id).setOnClickListener(this);

        textViewSelected = (TextView)rootView.findViewById(R.id.textViewSelected);
        textViewChoice = (TextView)rootView.findViewById(R.id.textViewChoice);

        imageLocationSelected = (ImageView)rootView.findViewById(R.id.imageLocationSelected);
        imageLocationChoice = (ImageView)rootView.findViewById(R.id.imageLocationChoice);
        imageLocationSelected.setOnClickListener(this);
        imageLocationChoice.setOnClickListener(this);
        refreshHome();

        return rootView;
    }


    private void refreshHome(){
        current = AppController.getInstance().getCurrentZone();
        Log.d("home", "current:" + (current != null));
        Log.d("home", "loadZones:" + (AppController.getInstance().loadZones().size()));
        if(current != null) {
            settings.setVisibility(View.VISIBLE);
            textViewTitle.setText(current.getName());
            zoneTitle.setText(current.getName());

            devices.clear();
            devices.addAll(current.getDevices());

            if(mRecyclerView == null) {
                mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
                mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(3, 30, true));
                mAdapter = new DeviceAdapter((MainActivity) getActivity(), devices);
                mRecyclerView.setAdapter(mAdapter);
            }
            mAdapter.notifyDataSetChanged();

            updateHomeAway();
            showHomeAway(false, false);

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
                    AppController.getInstance().saveCurrentZone(current);

                    if(AppController.getInstance().getCurrentZone() != null && ((MainActivity)getActivity()).getmManager() != null)
                        ((MainActivity)getActivity()).getmManager().setCurrentZone(AppController.getInstance().getCurrentZone());
                    refreshHome();

                }
            }

            @Override
            public void updateZone(boolean refreshFirebase) {

                if(refreshFirebase && AppController.getInstance().getCurrentZone() != null && ((MainActivity)getActivity()).getmManager() != null)
                    ((MainActivity)getActivity()).getmManager().setCurrentZone(AppController.getInstance().getCurrentZone());
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
            case R.id.imageLocationSelected:

                if(isHomeAwayOpen){
                    showHomeAway(false, true);
                }else{
                    //set the names


                    showHomeAway(true, true);
                }



                break;
            case R.id.imageLocationChoice:
                current.setUserInZone(!current.isUserInZone());
                updateHomeAway();
                showHomeAway(false, true);
                for(BasaDevice device : current.getDevices()) {
                    new UpdateLocationService(device.getUrl(), new UserLocation(current.isUserInZone(), UserLocation.TYPE_BUILDING), new CallbackFromService() {
                        @Override
                        public void success(Object response) {
                        }

                        @Override
                        public void failed(Object error) {
                        }
                    }).execute();
                }
                break;


        }
    }

    private void updateHomeAway(){

        textViewSelected.setText(current.isUserInZone()? "Home" : "Away");
        textViewChoice.setText(current.isUserInZone()? "Away" : "Home");


        Glide.with(this)
                .load(current.isUserInZone()? R.drawable.ic_home_circle :  R.drawable.ic_home_circle_away)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageLocationSelected);

        Glide.with(this)
                .load(current.isUserInZone()? R.drawable.ic_home_circle_away :  R.drawable.ic_home_circle)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageLocationChoice);

//                imageLocationSelected


    }

    private void showHomeAway(final boolean show, boolean animate){

        Animation fadeout = new AlphaAnimation(1.0f, 0.0f);
        fadeout.setDuration(500);
        Animation fadein = new AlphaAnimation(0.0f, 1.0f);
        fadein.setDuration(500);
        fadein.setStartOffset(600);


        fadein.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                if(show) {
                    rootView.findViewById(R.id.homeOrAwayLayout2).setVisibility(View.VISIBLE);
                    homeOrAwayBg.setVisibility(View.VISIBLE);
                }else{
                    mRecyclerView.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                if(show){
                    homeOrAwayBg.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            Log.d("fff", "homeOrAwayBg222222");
                            if(isHomeAwayOpen){
                                showHomeAway(false, true);
                            }
                            return true;
                        }
                    });
                }
            }
        });

        fadeout.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                if(!show){
                    homeOrAwayBg.setOnTouchListener(null);
                    Log.d("fff", "setOnTouchListener(null);");
                }
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                if(show) {
                    mRecyclerView.setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                }else{
                    rootView.findViewById(R.id.homeOrAwayLayout2).setVisibility(View.GONE);
                    homeOrAwayBg.setVisibility(View.GONE);
                }
            }
        });





        if(show && !isHomeAwayOpen){
            ((MainActivity)getActivity()).enableSwipe(false);
//            imageLocationSelected.setOnClickListener(null);
//            imageLocationSelected.setClickable(false);


            if(animate){
                mRecyclerView.startAnimation(fadeout);
                header.startAnimation(fadeout);
                homeOrAwayBg.startAnimation(fadein);
                rootView.findViewById(R.id.homeOrAwayLayout2).startAnimation(fadein);
            }else{
                mRecyclerView.setVisibility(View.GONE);
                header.setVisibility(View.GONE);
                homeOrAwayBg.setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.homeOrAwayLayout2).setVisibility(View.VISIBLE);

                homeOrAwayBg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Log.d("fff", "homeOrAwayBg");
                        if(isHomeAwayOpen){
                            showHomeAway(false, true);
                        }
                        return true;
                    }
                });
            }

            isHomeAwayOpen = true;
//            textViewSelected.setText("");
        }else if(!show){
            ((MainActivity)getActivity()).enableSwipe(true);


            if(animate){
                homeOrAwayBg.startAnimation(fadeout);
                rootView.findViewById(R.id.homeOrAwayLayout2).startAnimation(fadeout);
                mRecyclerView.startAnimation(fadein);
                header.startAnimation(fadein);
            }else{
                mRecyclerView.setVisibility(View.VISIBLE);
                header.setVisibility(View.VISIBLE);
                homeOrAwayBg.setVisibility(View.GONE);
                rootView.findViewById(R.id.homeOrAwayLayout2).setVisibility(View.GONE);
                homeOrAwayBg.setOnTouchListener(null);
                Log.d("fff", "setOnTouchListener(null)2;");
            }





            isHomeAwayOpen = false;

        }

    }


    public interface CommunicationHomeFragment{

        void changeZone(String name);
        void updateZone(boolean refreshFirebase);

    }








}
