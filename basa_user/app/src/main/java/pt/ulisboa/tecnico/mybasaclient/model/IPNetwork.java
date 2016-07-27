package pt.ulisboa.tecnico.mybasaclient.model;

import android.net.wifi.ScanResult;

/**
 * Created by Sampaio on 27/07/2016.
 */
public class IPNetwork {

    private String mac;
    private String ssid;

    public IPNetwork( String mac, String ssid) {
        this.mac = mac;
        this.ssid = ssid;
    }



    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public static IPNetwork convert(ScanResult wifiResult){
        return new IPNetwork(wifiResult.BSSID, wifiResult.SSID);

    }
}
