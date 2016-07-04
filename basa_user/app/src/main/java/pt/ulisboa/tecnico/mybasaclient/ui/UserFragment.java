package pt.ulisboa.tecnico.mybasaclient.ui;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.model.Zone;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;
import pt.ulisboa.tecnico.mybasaclient.util.NiceColor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    View rootView, zone1, zone2;
    TextView user_photo, textUsername, textEmail;
    private List<Zone> zones;
    private final static int[] CLICK = {R.id.action_add_zone, R.id.action_goToHome, R.id.user_info, R.id.user_account};


    public UserFragment() {
        // Required empty public constructor
    }


    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_user, container, false);
        init();
        return rootView;
    }

    private void init(){

        user_photo = (TextView)rootView.findViewById(R.id.user_photo);
        textUsername = (TextView)rootView.findViewById(R.id.textUsername);
        textEmail = (TextView)rootView.findViewById(R.id.textEmail);

        User user = User.getLoggedUser();

        user_photo.setText(getLetters(user.getUserName()));
        textUsername.setText(user.getUserName());
        textEmail.setText(user.getEmail());
        GradientDrawable bgShape = (GradientDrawable) user_photo.getBackground();
        bgShape.setColor(NiceColor.betterNiceColor(user.getUserName()));


        zone1 = rootView.findViewById(R.id.zone1);
        zone2 = rootView.findViewById(R.id.zone2);
        zone1.setBackgroundResource(R.drawable.zone_background);
        zone2.setBackgroundResource(R.drawable.zone_background);
        refreshZoneTabs();

        for (int id: CLICK)
            rootView.findViewById(id).setOnClickListener(this);

    }

    private void refreshZoneTabs(){
        zone1.setVisibility(View.INVISIBLE);
        zone2.setVisibility(View.INVISIBLE);
        zone1.setOnClickListener(null);
        zone2.setOnClickListener(null);
        zones = Zone.loadZones();
        if(!zones.isEmpty()){
            Zone current = Zone.getCurrentZone();
            zones = Zone.getOtherZones(zones, current);

            int i = 0;
            for(Zone z : zones){
                i++;
                if(i == 1){
                    zone1.setVisibility(View.VISIBLE);
                    TextView zoneTitleZone1 = (TextView)zone1.findViewById(R.id.zoneTitle);
                    zoneTitleZone1.setText(z.getName());
                    zone1.setOnClickListener(this);

                } else if(i== 2){
                    zone2.setVisibility(View.VISIBLE);
                    TextView zoneTitleZone2 = (TextView)zone2.findViewById(R.id.zoneTitle);
                    zoneTitleZone2.setText(z.getName());
                    zone2.setOnClickListener(this);
                }
            }
        }
    }

    public String getLetters(String username){
        String letter = "";
        String[] names = username.split(" ");
        for (String name: names) {
            if(name.length()> 1)
                letter+= name.substring(0,1);
            else if(name.length() == 1)
                letter+= name;
        }
        return letter.toUpperCase();
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setCommunicationUserFragment(new CommunicationUserFragment() {
            @Override
            public void refreshZones() {
                refreshZoneTabs();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setCommunicationUserFragment(null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.zone1:
                if(((MainActivity)getActivity()).getCommunicationHomeFragment() != null) {
                    ((MainActivity) getActivity()).getCommunicationHomeFragment().changeZone(zones.get(0).getName());
                }

                refreshZoneTabs();
                ((MainActivity) getActivity()).openViewpagerPage(Global.HOME);
                break;
            case R.id.zone2:
                if(((MainActivity)getActivity()).getCommunicationHomeFragment() != null) {
                    ((MainActivity) getActivity()).getCommunicationHomeFragment().changeZone(zones.get(1).getName());
                }
                refreshZoneTabs();
                ((MainActivity) getActivity()).openViewpagerPage(Global.HOME);
                break;
            case R.id.user_info:
                ((MainActivity) getActivity()).openPage(Global.DIALOG_INFO);
                break;
            case R.id.action_add_zone:
                ((MainActivity)getActivity()).openPage(Global.DIALOG_ADD_ZONE_PART1);
                break;
            case R.id.action_goToHome:
                ((MainActivity)getActivity()).openViewpagerPage(Global.HOME);
                break;
            case R.id.user_account:
                ((MainActivity) getActivity()).openPage(Global.DIALOG_ACCOUNT);
                break;


        }
    }


    public interface CommunicationUserFragment{
        void refreshZones();
    }

}
