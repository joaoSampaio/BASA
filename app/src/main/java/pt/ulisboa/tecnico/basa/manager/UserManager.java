package pt.ulisboa.tecnico.basa.manager;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.UUID;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
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


    public String registerNewUser(String userName, String email) throws UserRegistrationException {


        UUID uuid = UUID.randomUUID();
        List<User> users = new ModelCache<List<User>>().loadModel(new TypeToken<List<User>>(){}.getType(), Global.OFFLINE_USERS);

        if(User.userNameExists(users, userName)){
            throw new UserRegistrationException("Username already active");
        }

        users.add(new User(userName , email, uuid.toString()));
        return uuid.toString();

    }

    public User getUser(String uuid){
        List<User> users = new ModelCache<List<User>>().loadModel(new TypeToken<List<User>>(){}.getType(), Global.OFFLINE_USERS);
        return User.getUserFromList(users, uuid);


    }


    @Override
    public void destroy() {

    }

    public MainActivity getActivity() {
        return activity;
    }
}
