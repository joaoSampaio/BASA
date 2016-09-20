package pt.ulisboa.tecnico.basa.util;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.manager.LightingManager;

/**
 * Created by Sampaio on 16/05/2016.
 */
public class LightingControlEDUP implements LightingControl {

    private final static String STARTING_TEXT = "7e7e000d0002";
    //7E7E000D0002
    private final static String ENDING_TEXT = "7f7f";
    private LightingManager lightingManager;
    private ListenEDUPMulticast listenEDUPMulticast;
    public LightingControlEDUP(LightingManager lightingManager) {
        this.lightingManager = lightingManager;
        listenEDUPMulticast = new ListenEDUPMulticast();
        new Thread(listenEDUPMulticast).start();
    }

    public void destroy(){
        MulticastLockSingleton.getInstance(AppController.getAppContext()).releaseLock();
        listenEDUPMulticast.stopSocket();
    }


    @Override
    public void sendLightCommand(boolean[] lighting) {

        Log.d("light", "sendLightCommand");
        int[] lights = new int[3];
        for (int i = 0; i< lighting.length; i++)
            lights[i] = lighting[i] ? 1 :  0;


//        new ContactEDUP(lights).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        new Thread(new ContactEDUPRunnable(lights)).start();
    }



    private static byte[] combine(String content){

        byte[] start = hexStringToByteArray("7e7e001c0001");
        byte[] data = content.getBytes();
        byte[] end = hexStringToByteArray("7f7f");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write( start );
            outputStream.write( data );
            outputStream.write( end );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray( );
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private class ContactEDUPRunnable implements Runnable {
        /*
         * Defines the code to run for this task.
         */

        int[] lights;

        public ContactEDUPRunnable(int[] lights) {
            this.lights = lights;
        }

        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);


            Log.d("light", "doInBackground");
//            String content = "ZH037CC7097B7CA91";
            String content = AppController.getInstance().getDeviceConfig().getEdupLightId();

            for (int light : lights)
                content+=light;

            Log.d("light", "content:"+content);
            String ipaddress = "47.88.138.88";
//            String ipaddress = "servers.chitco.com.cn";
            int portnumber = 8081;
            String modifiedSentence;
            Socket clientSocket;
            try
            {
                clientSocket = new Socket(ipaddress, portnumber);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.write(combine(content));
                clientSocket.setSoTimeout(10000);
                modifiedSentence = inFromServer.readLine();
                clientSocket.close();
                outToServer.close();
                inFromServer.close();
                Log.d("light", "modifiedSentence:"+modifiedSentence);
            }
            catch (Exception exc)
            {
                exc.printStackTrace();

            }
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        Log.d("ListenEDUPMulticast", "bytes:"+bytes.length);
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }


    private class ListenEDUPMulticast implements Runnable {
        /*
         * Defines the code to run for this task.
         */

        private DatagramSocket socket;
        private Context ctx;
        private boolean run;
        public ListenEDUPMulticast() {
            this.ctx = AppController.getAppContext();
            this.run = true;
        }

        public void stopSocket(){
            run = false;
            if(socket != null)
                socket.close();
        }

        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY);
            Log.d("ListenEDUPMulticast", "ListenEDUPMulticast");
            MulticastLockSingleton multicastLockSingleton;
            WifiManager wifi = (WifiManager)ctx.getSystemService( ctx.getApplicationContext().WIFI_SERVICE );

            if(wifi != null) {

                multicastLockSingleton = MulticastLockSingleton.getInstance(ctx);
                multicastLockSingleton.acquireLock();
//                WifiManager.MulticastLock lock = wifi.createMulticastLock("TheLock");
//                lock.acquire();

                try {


                    WifiManager wm = (WifiManager) AppController.getAppContext().getSystemService(Activity.WIFI_SERVICE);
                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                    Log.d("ListenEDUPMulticast", "ListenEDUPMulticast -> " + ip);
                    InetAddress IPAddress =  InetAddress.getByName(ip);

                    byte[] buf = new byte[1024];


                    socket = new DatagramSocket(null);
                    InetSocketAddress address = new InetSocketAddress("0.0.0.0", 8089);
//                    InetSocketAddress address = new InetSocketAddress(ip, 8089);
                    socket.setReuseAddress(true);
//                    socket.setBroadcast(true);
                    socket.bind(address);



                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    while (run) {

                        Log.d("ListenEDUPMulticast", "Waiting for data + ");
                        try {

                            socket.receive(packet);
                            Log.d("light", "Data received");
                            String s = new String(packet.getData(), 0, packet.getLength(), "US-ASCII");

                            Log.d("light", " packet.getLength():-> " +  packet.getLength());
                            Log.d("light", "packet.getData():-> " + packet.getData());
                            byte[] content = Arrays.copyOfRange(packet.getData(),0,packet.getLength());
                            String broadcast = bytesToHex(content);
                            Log.d("ListenEDUPMulticast", "bytesToHex:-> " + broadcast);
                            Log.d("ListenEDUPMulticast", "bytesToHex:-> " + broadcast.length());

                            Log.d("ListenEDUPMulticast", "receive:-> " + s);
                            Log.d("ListenEDUPMulticast", "length:-> " + s.length());
                            broadcast = broadcast.toLowerCase();
                            if(broadcast.startsWith(STARTING_TEXT) &&
                                    broadcast.endsWith(ENDING_TEXT) &&
                                    (System.currentTimeMillis() - getLightingManager().getTimeLastLightClick())> 1000){
                                String valuableContent = broadcast.replace(STARTING_TEXT, "").replace(ENDING_TEXT, "");

                                String hexString = hexToASCII(valuableContent);
                                Log.d("webserver", "hexString:"+hexString);

                                char[] c = hexString.toCharArray();
                                final boolean[] values = {c[1] == '1', c[2] == '1', c[3] == '1'};

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("light", "values:"+values.toString());
                                        getLightingManager().setLightState(values, false, true);
                                    }
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("ListenEDUPMulticast", "IOException:-> ");
                        }

                    }

                } catch (SocketException  e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                multicastLockSingleton.releaseLock();
            }
        }
    }


    private class ContactEDUP extends AsyncTask<Void, Void, Void>{

        int[] lights;

        public ContactEDUP(int[] lights) {
            this.lights = lights;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("light", "edup onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.d("light", "doInBackground");
            String content = "ZH037CC7097B7CA91";
            for (int light : lights)
                content+=light;

            Log.d("light", "content:"+content);
            String ipaddress = "47.88.138.88";
//            String ipaddress = "servers.chitco.com.cn";
            int portnumber = 8081;
            String modifiedSentence;
            Socket clientSocket;
            try
            {
                clientSocket = new Socket(ipaddress, portnumber);
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToServer.write(combine(content));
                clientSocket.setSoTimeout(10000);
                modifiedSentence = inFromServer.readLine();
                clientSocket.close();
                outToServer.close();
                inFromServer.close();
                Log.d("light", "modifiedSentence:"+modifiedSentence);
            }
            catch (Exception exc)
            {
                exc.printStackTrace();
            }
            return null;
        }
    }

    public LightingManager getLightingManager() {
        return lightingManager;
    }
}
