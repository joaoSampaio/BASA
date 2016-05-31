package pt.ulisboa.tecnico.basa.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by Sampaio on 29/05/2016.
 */
public class MulticastLockSingleton {

    private static MulticastLockSingleton instance = null;
    private WifiManager wifi;
    private int references = 0;
    private WifiManager.MulticastLock lock;
    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private MulticastLockSingleton(){ }

    /* Static 'instance' method */
    public static MulticastLockSingleton getInstance(Context ctx) {
        if(instance == null) {
            instance = new MulticastLockSingleton();
            instance.setWifi((WifiManager)ctx.getSystemService( ctx.getApplicationContext().WIFI_SERVICE ));
            instance.setReferences(0);
        }
        return instance;
    }

    public void acquireLock( ) {
        if(instance.getReferences() == 0 || lock == null) {
            Log.d("ListenEDUPMulticast", "acquireLock");
            lock = wifi.createMulticastLock("The Lock");
            lock.acquire();
        }
        references++;
    }

    public void releaseLock( ) {

        references--;
        if(references == 0 ) {
            lock.release();
        }

    }

    public WifiManager getWifi() {
        return wifi;
    }

    public void setWifi(WifiManager wifi) {
        this.wifi = wifi;
    }

    public int getReferences() {
        return references;
    }

    public void setReferences(int references) {
        this.references = references;
    }

    public WifiManager.MulticastLock getLock() {
        return lock;
    }

    public void setLock(WifiManager.MulticastLock lock) {
        this.lock = lock;
    }
}
