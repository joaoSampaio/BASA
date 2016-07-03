package pt.ulisboa.tecnico.mybasaclient.rest.services;

/**
 * Created by sampaio on 08-10-2015.
 */
public interface CallbackFromService<T,V> {
    public void success(T response);
    public void failed(V error);

}
