package pt.ulisboa.tecnico.basa.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.model.SSDP;

/**
 * Created by Sampaio on 23/05/2016.
 */
public class SSDiscoveryProtocol extends AsyncTask {

    SocketAddress mSSDPMulticastGroup;
    MulticastSocket mSSDPSocket;
    public static final String ADDRESS = "239.255.255.250";
    public static final int PORT = 1900;
    private SearchSSDP searchSSDP;
    Context ctx;
    List<String> addresses;
    List<String> addresses2 = new ArrayList<>();
    List<SSDP> endpoints = new ArrayList<>();
    public SSDiscoveryProtocol(Context ctx, SearchSSDP searchSSDP) {
        this.ctx = ctx;
        this.searchSSDP = searchSSDP;
        addresses = new ArrayList<>();
        Log.d("ssdp", "SSDiscoveryProtocol new instance: ");
    }

    @Override
    protected Object doInBackground(Object[] params) {


        Log.d("ssdp", "doInBackground: ");

        WifiManager wifi = (WifiManager)ctx.getSystemService( ctx.getApplicationContext().WIFI_SERVICE );

        if(wifi != null) {

            WifiManager.MulticastLock lock = wifi.createMulticastLock("The Lock");
            lock.acquire();

            DatagramSocket socket = null;

            try {

                InetAddress group = InetAddress.getByName("239.255.255.250");
                int port = 1900;
                String query =
                        "M-SEARCH * HTTP/1.1\r\n" +
                                "HOST: 239.255.255.250:1900\r\n"+
                                "MAN: \"ssdp:discover\"\r\n"+
                                "MX: 1\r\n"+
//                                "ST: ssdp:all\r\n"+  // Use this for all UPnP Devices
                                "ST: urn:schemas-basa-pt:service:climate:1\r\n"+
                                "\r\n";

                socket = new DatagramSocket();
                socket.setReuseAddress(true);
                socket.setSoTimeout(5000);
                DatagramPacket dgram = new DatagramPacket(query.getBytes(), query.length(),
                        group, port);
                socket.send(dgram);

                long time = System.currentTimeMillis();
                long curTime = System.currentTimeMillis();

                // Let's consider all the responses we can get in 1 second
                Log.d("ssdp", "antes while: ");
                while (curTime - time < 3000) {
                    byte[] buf = new byte[1024];
                    DatagramPacket p = new DatagramPacket(buf, buf.length);

                    socket.receive(p);

                    String s = new String(p.getData(), 0, p.getLength());
                    addresses2.add("body:" + s);
                    Log.d("ssdp", "receive:-> " + s);
                    if (s.toUpperCase().startsWith("HTTP/1.1 200")) {
                        addresses.add(p.getAddress().getHostAddress());

                    }
                    curTime = System.currentTimeMillis();
                }




            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("ssdp", "IOException: " + e.getMessage());
                e.printStackTrace();
            }
            finally {
                socket.close();
            }
            lock.release();

            Log.d("ssdp", "fim: " + addresses.size());
            int i= 0;
            for (String s : addresses){

                Log.d("ssdp", "received: " + s);

            }

            for (String s : addresses2){

                endpoints.add(new SSDP(s));

            }
// body:HTTP/1.1 200 OK EXT: CACHE-CONTROL: max-age=1200 SERVER: Arduino/1.0 UPNP/1.1 Philips hue bridge 2012/929000226503 USN: uuid:11111111-fca6-4070-85f4-1fbfb9add62c ST: urn:schemas-basa-pt:service:climate:1 LOCATION: http://192.168.0.102:80/description.xml



        }
        return null;

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        searchSSDP.onSearchFinish(endpoints);
    }



    public interface SearchSSDP {
        void onSearchFinish(List<SSDP> endpoints);
    }

}
