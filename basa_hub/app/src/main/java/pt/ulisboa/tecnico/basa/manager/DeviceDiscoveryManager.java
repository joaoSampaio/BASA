package pt.ulisboa.tecnico.basa.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.SSDP;
import pt.ulisboa.tecnico.basa.util.LightingControl;
import pt.ulisboa.tecnico.basa.util.LightingControlEDUP;
import pt.ulisboa.tecnico.basa.util.NsdHelper;
import pt.ulisboa.tecnico.basa.util.SSDiscoveryProtocol;
import pt.ulisboa.tecnico.basa.util.UDPDiscovery;

public class DeviceDiscoveryManager implements Manager {

    private Context ctx;
    private NsdHelper mNsdHelper;
    SSDiscoveryProtocol ssDiscoveryProtocol;
//    private UDPDiscovery udpDiscovery;

    public DeviceDiscoveryManager(Context ctx){
        this.ctx = ctx;
//        this.udpDiscovery = new UDPDiscovery(ctx, "BASA", 49000);


//        mNsdHelper = new NsdHelper(ctx);
//        mNsdHelper.initializeNsd();
//        startDiscovery();




    }

    public void startDiscovery(final DevicesDiscovery mListener){
        Log.d("ssdp", "startDiscovery: ");
        ssDiscoveryProtocol = new SSDiscoveryProtocol(ctx, new SSDiscoveryProtocol.SearchSSDP() {
            @Override
            public void onSearchFinish(List<SSDP> endpoints) {
                mListener.onDevicesDiscovered(endpoints);
            }
        });
        ssDiscoveryProtocol.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//        mNsdHelper.discoverServices();
//        this.udpDiscovery.startMessageReceiver();
//        this.udpDiscovery.sendMessage("abcde");
    }

    public void stopDiscovery(){
//        mNsdHelper.stopDiscovery();
//        this.udpDiscovery.stopMessageReceiver();
    }


    @Override
    public void destroy() {
        if(mNsdHelper != null)
            stopDiscovery();
    }

    public interface DevicesDiscovery{
        void onDevicesDiscovered(List<SSDP> endpoints);
    }

}
