package pt.ulisboa.tecnico.basa.manager;

import android.graphics.Bitmap;

import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.SendEmailService;
import pt.ulisboa.tecnico.basa.rest.mail.WelcomeTemplate;
import pt.ulisboa.tecnico.basa.util.ModelCache;
import pt.ulisboa.tecnico.basa.util.QRCodeGenerator;

/**
 * Created by joaosampaio on 07-06-2016.
 */
public class UserManager implements Manager {


    public UserManager() {

    }


    public String registerNewUser(String userName, String email, String optionalUuid) throws UserRegistrationException {

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        List<User> users = new ModelCache<List<User>>().loadModel(new TypeToken<List<User>>(){}.getType(), Global.OFFLINE_USERS);

        if(User.userEmailExists(users, email) && !User.getUserEmailFromList(users, email).getUuid().equals(optionalUuid)){
            throw new UserRegistrationException("Email already active");
        }
        uuidString = optionalUuid;
//        if(optionalUuid != null && !optionalUuid.isEmpty() && !User.userUuidExists(users, optionalUuid)){
//            uuidString = optionalUuid;
//        }

        //se utilizador com aquele mail e uuid ja existir nao vamos voltar a adiciona-lo
        if(!(User.userEmailExists(users, email) &&
                User.getUserEmailFromList(users, email).getUuid().equals(optionalUuid))) {

            users.add(new User(userName, email, uuidString));
            new ModelCache<>().saveModel(users, Global.OFFLINE_USERS);

        }
        try {
            sendMailRegister(email, uuid.toString());
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return uuidString;

    }




    private void sendMailRegister(String uuid, String email) throws WriterException {

        Bitmap image = QRCodeGenerator.encodeAsBitmap(uuid);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        new SendEmailService(new CallbackMultiple() {
            @Override
            public void success(Object response) {

            }

            @Override
            public void failed(Object error) {

            }
        }, email, "tema", WelcomeTemplate.getTemplate(), byteArray).execute();


    }

    public User getUser(String uuid){
        List<User> users = new ModelCache<List<User>>().loadModel(new TypeToken<List<User>>(){}.getType(), Global.OFFLINE_USERS);
        return User.getUserFromList(users, uuid);


    }


    @Override
    public void destroy() {

    }

}
