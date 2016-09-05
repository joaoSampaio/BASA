package pt.ulisboa.tecnico.basa.rest.services;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import pt.ulisboa.tecnico.basa.app.AppController;


public abstract class ServerCommunicationService {

    //executa o pedido ao servidor
    public abstract void execute();

    public boolean isWifiAvailable(){
        ConnectivityManager connManager = (ConnectivityManager) AppController.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();

    }
}
