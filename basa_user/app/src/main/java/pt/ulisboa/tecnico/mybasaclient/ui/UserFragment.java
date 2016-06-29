package pt.ulisboa.tecnico.mybasaclient.ui;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import pt.ulisboa.tecnico.mybasaclient.Global;
import pt.ulisboa.tecnico.mybasaclient.MainActivity;
import pt.ulisboa.tecnico.mybasaclient.R;
import pt.ulisboa.tecnico.mybasaclient.model.User;
import pt.ulisboa.tecnico.mybasaclient.util.ModelCache;
import pt.ulisboa.tecnico.mybasaclient.util.NiceColor;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    View rootView;
    TextView user_photo, textUsername, textEmail;



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
            User user = new ModelCache<User>().loadModel(new TypeToken<User>() {
            }.getType(), Global.UUID_USER);

        user_photo.setText(getLetters(user.getUserName()));
        textUsername.setText(user.getUserName());
        textEmail.setText(user.getEmail());
        GradientDrawable bgShape = (GradientDrawable) user_photo.getBackground();
        bgShape.setColor(NiceColor.betterNiceColor(user.getUserName()));

        rootView.findViewById(R.id.action_add_zone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).openPage(Global.DIALOG_ADD_ZONE_PART1);
            }
        });

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



}
