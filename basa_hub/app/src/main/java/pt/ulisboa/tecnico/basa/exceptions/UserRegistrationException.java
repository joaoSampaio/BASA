package pt.ulisboa.tecnico.basa.exceptions;

/**
 * Created by Sampaio on 08/06/2016.
 */
public class UserRegistrationException extends Exception {

    public UserRegistrationException(String detailMessage) {
        super(detailMessage);
    }

    public UserRegistrationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
