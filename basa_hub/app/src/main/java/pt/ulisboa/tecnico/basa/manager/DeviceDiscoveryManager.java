package pt.ulisboa.tecnico.basa.manager;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.model.SSDP;
import pt.ulisboa.tecnico.basa.util.SSDiscoveryProtocol;

public class DeviceDiscoveryManager implements Manager {


    SSDiscoveryProtocol ssDiscoveryProtocol;

    public DeviceDiscoveryManager(){

    }

    public void startDiscovery(final DevicesDiscovery mListener){
        Log.d("ssdp", "startDiscovery: ");
        ssDiscoveryProtocol = new SSDiscoveryProtocol(AppController.getAppContext(), new SSDiscoveryProtocol.SearchSSDP() {
            @Override
            public void onSearchFinish(List<SSDP> endpoints) {
                mListener.onDevicesDiscovered(endpoints);
            }
        });
        ssDiscoveryProtocol.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }



    @Override
    public void destroy() {

    }

    public interface DevicesDiscovery{
        void onDevicesDiscovered(List<SSDP> endpoints);
    }

}
