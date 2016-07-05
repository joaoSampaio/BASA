package pt.ulisboa.tecnico.basa.model.registration;


import com.google.gson.reflect.TypeToken;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import pt.ulisboa.tecnico.basa.Global;
import pt.ulisboa.tecnico.basa.util.ModelCache;

public class UserRegistrationToken {

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private static final int LEN = 6;
    private static final int VALID_DURATION = 120000;

    private Map<String, Long> tokens;

    public UserRegistrationToken() {

        this.tokens = new HashMap<>();

    }

    public Map<String ,Long> getTokens() {
        return tokens;
    }

    public static String generateToken(){

        UserRegistrationToken tokens = UserRegistrationToken.load();

        StringBuilder sb = new StringBuilder( LEN );
        for( int i = 0; i < LEN; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        String token = sb.toString();

        long time = System.currentTimeMillis();
        if(!tokens.getTokens().containsKey(token)){
            tokens.getTokens().put(token, time);
            UserRegistrationToken.save(tokens);
        }else{
            //recursivamente vai obter um token valido
            token = UserRegistrationToken.generateToken();
        }



        return token;
    }

    //
    public static boolean isTokenValid(String token){

        UserRegistrationToken userRegistrationToken = UserRegistrationToken.load();
        if(userRegistrationToken.getTokens().containsKey(token)){

            Long timePast = userRegistrationToken.getTokens().get(token).longValue();
            long timeCurrent = System.currentTimeMillis();
            long time = timeCurrent - timePast;
            userRegistrationToken.getTokens().remove(token);
            UserRegistrationToken.save(userRegistrationToken);
            return time <= VALID_DURATION;
        }

        return false;


    }


    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }


    public static UserRegistrationToken load(){
        try {
            UserRegistrationToken tokens =  new ModelCache<UserRegistrationToken>().loadModel(new TypeToken<UserRegistrationToken>() {
            }.getType(), Global.OFFLINE_TOKEN);
            return (tokens != null && tokens.getTokens().size() >= 0) ? tokens : new UserRegistrationToken();
        }catch (Exception e){
            return new UserRegistrationToken();
        }
    }

    public static void save(UserRegistrationToken tokens){
        new ModelCache<UserRegistrationToken>().saveModel(tokens, Global.OFFLINE_TOKEN);
    }

}
