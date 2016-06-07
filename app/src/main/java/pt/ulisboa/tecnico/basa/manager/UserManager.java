package pt.ulisboa.tecnico.basa.manager;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.UUID;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.model.Recipe;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.ui.MainActivity;
import pt.ulisboa.tecnico.basa.util.ModelCache;

/**
 * Created by joaosampaio on 07-06-2016.
 */
public class UserManager implements Manager {

    MainActivity activity;

    public UserManager(MainActivity activity) {
        this.activity = activity;





    }


    public String registerNewUser(String userName, String email){


        UUID uuid = UUID.randomUUID();
        List<User> users = new ModelCache<List<User>>().loadModel(new TypeToken<List<User>>(){}.getType(), Global.OFFLINE_USERS);
        if(!User.userNameExists(users, userName)){
            users.add(new User(userName , email, uuid.toString()));
            return uuid.toString();
        }else{
            return User.getUserNameFromList(users, userName).getUuid();
        }

    }



    @Override
    public void destroy() {

    }

    public MainActivity getActivity() {
        return activity;
    }
}
