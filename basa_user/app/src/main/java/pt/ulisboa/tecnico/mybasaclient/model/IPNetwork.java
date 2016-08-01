package pt.ulisboa.tecnico.mybasaclient.model;

import android.net.wifi.ScanResult;

import java.util.List;

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

    public static void addNonDuplicates(List<IPNetwork> original, List<IPNetwork> received){

        for (IPNetwork networkReceive : received){
            if(!existsBssid(original, networkReceive.getMac())){
                original.add(networkReceive);
            }
        }
    }

    public static boolean existsBssid(List<IPNetwork> list, String bssid){
        for (IPNetwork network : list){
            if(network.getMac().equals(bssid))
                return true;
        }
        return false;
    }


}
