package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import pt.ulisboa.tecnico.basa.util.LightingControl;
import pt.ulisboa.tecnico.basa.util.LightingControlEDUP;
import pt.ulisboa.tecnico.basa.util.NsdHelper;
import pt.ulisboa.tecnico.basa.util.UDPDiscovery;

public class DeviceDiscoveryManager implements Manager {

    private Context ctx;
    private NsdHelper mNsdHelper;
//    private UDPDiscovery udpDiscovery;

    public DeviceDiscoveryManager(Context ctx){
        this.ctx = ctx;
//        this.udpDiscovery = new UDPDiscovery(ctx, "BASA", 49000);
        mNsdHelper = new NsdHelper(ctx);
        mNsdHelper.initializeNsd();
        startDiscovery();
    }

    public void startDiscovery(){
        mNsdHelper.discoverServices();
//        this.udpDiscovery.startMessageReceiver();
//        this.udpDiscovery.sendMessage("abcde");
    }

    public void stopDiscovery(){
        mNsdHelper.stopDiscovery();
//        this.udpDiscovery.stopMessageReceiver();
    }


    @Override
    public void destroy() {
        if(mNsdHelper != null)
            stopDiscovery();
    }
}
