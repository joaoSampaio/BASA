package pt.ulisboa.tecnico.basa.manager;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.exceptions.UserRegistrationException;
import pt.ulisboa.tecnico.basa.model.UserLocation;
import pt.ulisboa.tecnico.basa.model.event.InterestEventAssociation;
import pt.ulisboa.tecnico.basa.model.User;
import pt.ulisboa.tecnico.basa.model.event.Event;
import pt.ulisboa.tecnico.basa.model.event.EventUserLocation;
import pt.ulisboa.tecnico.basa.model.event.EventTime;
import pt.ulisboa.tecnico.basa.rest.CallbackMultiple;
import pt.ulisboa.tecnico.basa.rest.SendEmailService;
import pt.ulisboa.tecnico.basa.rest.mail.WelcomeTemplate;
import pt.ulisboa.tecnico.basa.util.ModelCache;
import pt.ulisboa.tecnico.basa.util.QRCodeGenerator;

/**
 * Created by joaosampaio on 07-06-2016.
 */
public class UserManager implements Manager {

    private Map<String, Long> buildingLocation;
    private Map<String, Long> officeLocation;
    private final static int TIMEOUT_OFFICE = 20 * 1000;
    private final static int TIMEOUT_BUILDING = 2*60*1000;

    private InterestEventAssociation interestTime;
    public UserManager() {

        this.buildingLocation = new HashMap<>();
        this.officeLocation = new HashMap<>();



//        interestLocation = new InterestEventAssociation(Event.TRIGGER_USER_LOCATION, new EventManager.RegisterInterestEvent() {
//            @Override
//            public void onRegisteredEventTriggered(Event event) {
//                if(event instanceof EventUserLocation){
//                    int type =((EventUserLocation)event).getLocation();
//                    String userId = ((EventUserLocation) event).getUserId();
//                    if(type == EventUserLocation.TYPE_OFFICE) {
//
//                        if(((EventUserLocation)event).isInBuilding()){
//                            long time = ((EventUserLocation)event).getTime();
//                            officeLocation.put(userId, time);
//                            AppController.getInstance().getBasaManager().getLightingManager().turnONLight(0, true, true);
//                            AppController.getInstance().getBasaManager().getTextToSpeechManager().speak("User detected, turning on light");
//                        }else{
//                            officeLocation.remove(userId);
//                        }
//
//                    }else if(type == EventUserLocation.TYPE_BUILDING){
//                        if(((EventUserLocation)event).isInBuilding()){
//                            long time = ((EventUserLocation)event).getTime();
//                            buildingLocation.put(userId, time);
//                            AppController.getInstance().getBasaManager().getLightingManager().turnONLight(0, true, true);
//                            AppController.getInstance().getBasaManager().getTextToSpeechManager().speak("User detected, turning on light");
//                        }else{
//                            buildingLocation.remove(userId);
//                        }
//                    }
//                }
//            }
//        }, 0);
//        AppController.getInstance().getBasaManager().getEventManager().registerInterest(interestLocation);

        interestTime = new InterestEventAssociation(Event.TIME, new EventManager.RegisterInterestEvent() {
            @Override
            public void onRegisteredEventTriggered(Event event) {
                if(event instanceof EventTime){
                    long time = ((EventTime)event).getDate();

                    Map<String, Long> tmp = new HashMap<>(buildingLocation);
                    Long current = System.currentTimeMillis();
                    for ( Map.Entry<String, Long> entry : tmp.entrySet() ){
                        //when a timeout occurs
                        if(current > (entry.getValue() + TIMEOUT_BUILDING)){
                            buildingLocation.remove(entry.getKey());
                            AppController.getInstance().getBasaManager().getEventManager().addEvent(new EventUserLocation(entry.getKey(), false, EventUserLocation.TYPE_BUILDING, false));
                        }
                    }

                    tmp = new HashMap<>(officeLocation);
                    Log.d("sss","check timeout " + officeLocation.size() );
                    for ( Map.Entry<String, Long> entry : tmp.entrySet() ){
                        //when a timeout occurs
                        if(current > (entry.getValue() + TIMEOUT_OFFICE)){
                            officeLocation.remove(entry.getKey());
                            AppController.getInstance().getBasaManager().getEventManager().addEvent(new EventUserLocation(entry.getKey(), false, EventUserLocation.TYPE_OFFICE, false));
                        }
                    }



                }
            }
        }, 0);
        AppController.getInstance().getBasaManager().getEventManager().registerInterest(interestTime);

    }


    public int numActiveUsersBuilding(){
        Map<String, Long> tmp = new HashMap<>(buildingLocation);
        Long current = System.currentTimeMillis();
        int num = 0;
        for ( Map.Entry<String, Long> entry : tmp.entrySet() ){
            //within normal time interval
            if(current < (entry.getValue() + TIMEOUT_BUILDING))
                num++;
        }
        return num;
    }


    public int numActiveUsersOffice(){
        Map<String, Long> tmp = new HashMap<>(officeLocation);
        Long current = System.currentTimeMillis();
        int num = 0;
        for ( Map.Entry<String, Long> entry : tmp.entrySet() ){
            //within normal time interval
            if(current < (entry.getValue() + TIMEOUT_OFFICE))
                num++;
        }
        return num;
    }

    public boolean isUserInside(String userID, int type){
        Long date = null;
        Long current = System.currentTimeMillis();
        if(EventUserLocation.TYPE_OFFICE == type){
            date = officeLocation.get(userID);
        }else{
            date = buildingLocation.get(userID);
        }

        return (date != null && current < (date + TIMEOUT_OFFICE));
    }

    public void addUserHeartbeat(String userID, UserLocation userLocation){

        boolean isInside = isUserInside(userID, userLocation.getType());


        int type = userLocation.getType();

        if(type == UserLocation.TYPE_OFFICE) {
//
            if(userLocation.isInBuilding()){
                long time = System.currentTimeMillis();
                if(userLocation.getDuration() > 0){
                    time = time + userLocation.getDuration();
                }
                officeLocation.put(userID, time);

                //add heartbeat to building
                addUserHeartbeat(userID, new UserLocation(true, UserLocation.TYPE_BUILDING ));



            }else{
                officeLocation.remove(userID);
            }




        }else if(type == UserLocation.TYPE_BUILDING){

            if(userLocation.isInBuilding()){
                long time = System.currentTimeMillis();
                if(userLocation.getDuration() > 0){
                    time = time + userLocation.getDuration();
                }
                buildingLocation.put(userID, time);
            }else{
                buildingLocation.remove(userID);
            }
        }

        AppController.getInstance().getBasaManager().getEventManager()
                .addEvent(new EventUserLocation(userID,
                        userLocation.isInBuilding(),
                        userLocation.getType(),
                        !isInside));

    }



    public String registerNewUser(String userName, String email, String optionalUuid) throws UserRegistrationException {

        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        List<User> users = User.getUsers();

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
