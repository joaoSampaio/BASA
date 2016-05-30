package pt.ulisboa.tecnico.basa.model;

import android.util.Log;

/**
 * Created by Sampaio on 23/05/2016.
 */
public class SSDP {

    private final static String LOCATION = "LOCATION:";
    private final static String SERVER = "SERVER:";
    private final static String ST = "ST:";
    private final static String USN = "USN:";


    private String location;
    private String server;
    private String st;
    private String usn;


    public SSDP(String parse){

        String[] values = parse.split("\r\n");
        for (String v : values){
            if(v.startsWith(LOCATION)){
                setLocation(v.split(LOCATION)[1]);
            } else if(v.startsWith(SERVER)){
                setServer(v.split(SERVER)[1]);
            }else if(v.startsWith(ST)){
                setSt(v.split(ST)[1]);
            }else if(v.startsWith(USN)){
                setUsn(v.split(USN)[1]);
            }

            Log.d("ssdp", "v: " + v);
        }

    }


    public String getLocation() {
        return location;
    }

    public String getServer() {
        return server;
    }

    public String getSt() {
        return st;
    }

    public String getUsn() {
        return usn;
    }

    public void setLocation(String location) {
        this.location = location.trim();
    }

    public void setServer(String server) {
        this.server = server.trim();
    }

    public void setSt(String st) {
        this.st = st.trim();;
    }

    public void setUsn(String usn) {
        this.usn = usn.trim();
    }
}
